package saros.negotiation;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import saros.communication.extensions.StartActivityQueuingResponse;
import saros.exceptions.LocalCancellationException;
import saros.exceptions.SarosCancellationException;
import saros.filesystem.IChecksumCache;
import saros.filesystem.IProject;
import saros.filesystem.IWorkspace;
import saros.monitoring.IProgressMonitor;
import saros.negotiation.NegotiationTools.CancelOption;
import saros.negotiation.stream.IncomingStreamProtocol;
import saros.net.IReceiver;
import saros.net.ITransmitter;
import saros.net.xmpp.JID;
import saros.net.xmpp.XMPPConnectionService;
import saros.observables.FileReplacementInProgressObservable;
import saros.session.ISarosSession;
import saros.session.ISarosSessionManager;

/** Receive shared Projects and display them instant using a stream based solution. */
public class InstantIncomingProjectNegotiation extends AbstractIncomingProjectNegotiation {

  private static final Logger log = Logger.getLogger(InstantIncomingProjectNegotiation.class);

  public InstantIncomingProjectNegotiation(
      final JID peer, //
      final String negotiationID, //
      final List<ProjectNegotiationData> projectNegotiationData, //
      final ISarosSessionManager sessionManager, //
      final ISarosSession session, //
      final FileReplacementInProgressObservable fileReplacementInProgressObservable, //
      final IWorkspace workspace, //
      final IChecksumCache checksumCache, //
      final XMPPConnectionService connectionService, //
      final ITransmitter transmitter, //
      final IReceiver receiver //
      ) {
    super(
        peer,
        TransferType.INSTANT,
        negotiationID,
        projectNegotiationData,
        sessionManager,
        session,
        fileReplacementInProgressObservable,
        workspace,
        checksumCache,
        connectionService,
        transmitter,
        receiver);
  }

  @Override
  protected void transfer(
      IProgressMonitor monitor, Map<String, IProject> projectMapping, List<FileList> missingFiles)
      throws IOException, SarosCancellationException {

    awaitActivityQueueingActivation(monitor);

    /*
     * the user who sends this ProjectNegotiation is now responsible for the
     * resources of the contained projects
     */
    for (Entry<String, IProject> entry : projectMapping.entrySet()) {
      final String projectID = entry.getKey();
      final IProject project = entry.getValue();

      session.addProjectMapping(projectID, project);
      /* TODO change queuing to resource based queuing */
      session.enableQueuing(project);
    }

    transmitter.send(
        ISarosSession.SESSION_CONNECTION_ID,
        getPeer(), //
        StartActivityQueuingResponse.PROVIDER //
            .create( //
            new StartActivityQueuingResponse(getSessionID(), getID())));

    checkCancellation(CancelOption.NOTIFY_PEER);

    /* only get files, if something is missing */
    int filesMissing = 0;
    for (FileList list : missingFiles) filesMissing += list.getPaths().size();

    if (filesMissing > 0) receiveStream(monitor, filesMissing);
  }

  private void receiveStream(IProgressMonitor monitor, int fileCount)
      throws SarosCancellationException, IOException {

    String message = "Receiving files from " + getPeer().getName() + "...";
    monitor.beginTask(message, fileCount);
    monitor.subTask("Waiting for Host to start...");

    awaitTransferRequest();

    monitor.subTask("Host is starting to send...");
    log.debug(this + ": Host is starting to send...");

    IncomingFileTransfer transfer = transferListener.getRequest().accept();
    InputStream in = null;
    try {
      in = transfer.recieveFile();

      IncomingStreamProtocol isp;
      isp = new IncomingStreamProtocol(in, session, monitor);
      isp.receiveStream();
    } catch (XMPPException e) {
      throw new LocalCancellationException(e.getMessage(), CancelOption.NOTIFY_PEER);
    } finally {
      IOUtils.closeQuietly(in);
    }

    log.debug(this + ": stream transmission done");
    monitor.done();
  }
}
