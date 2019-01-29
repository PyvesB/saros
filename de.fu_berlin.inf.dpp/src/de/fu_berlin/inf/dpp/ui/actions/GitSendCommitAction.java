package de.fu_berlin.inf.dpp.ui.actions;

import de.fu_berlin.inf.dpp.SarosPluginContext;
import de.fu_berlin.inf.dpp.session.ISarosSession;
import de.fu_berlin.inf.dpp.session.ISarosSessionManager;
import de.fu_berlin.inf.dpp.session.User;
import de.fu_berlin.inf.dpp.ui.ImageManager;
import de.fu_berlin.inf.dpp.ui.Messages;
import de.fu_berlin.inf.dpp.ui.util.selection.SelectionUtils;
import de.fu_berlin.inf.dpp.ui.util.selection.retriever.SelectionRetrieverFactory;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.picocontainer.annotations.Inject;

public class GitSendCommitAction extends Action implements Disposable {

  public static final String ACTION_ID = GitSendCommitAction.class.getName();

  private ISelectionListener selectionListener =
      new ISelectionListener() {
        @Override
        public void selectionChanged(IWorkbenchPart part, ISelection selection) {
          updateEnablement();
        }
      };

  @Inject private ISarosSessionManager sessionManager;

  public GitSendCommitAction() {
    super(Messages.GitSendCommitAction_title);
    SarosPluginContext.initComponent(this);

    setId(ACTION_ID);
    setToolTipText(Messages.GitSendCommitAction_title);

    setImageDescriptor(ImageManager.getImageDescriptor("icons/elcl16/changecolor.png"));

    SelectionUtils.getSelectionService().addSelectionListener(selectionListener);

    SarosPluginContext.initComponent(this);

    updateEnablement();
  }

  private void updateEnablement() {
    setEnabled(singleParticipantSelected());
  }

  private boolean singleParticipantSelected() {
    List<User> sessionUsers =
        SelectionRetrieverFactory.getSelectionRetriever(User.class).getSelection();

    return (sessionUsers.size() == 1 && !sessionUsers.get(0).isLocal());
  }

  @Override
  public void run() {

    ISarosSession session = sessionManager.getSession();
    if (session == null) return;

    session.gitSendCommitRequest();
  }

  @Override
  public void dispose() {
    SelectionUtils.getSelectionService().removeSelectionListener(selectionListener);
  }
}
