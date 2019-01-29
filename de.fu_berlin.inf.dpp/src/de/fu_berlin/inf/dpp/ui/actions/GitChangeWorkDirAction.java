package de.fu_berlin.inf.dpp.ui.actions;

import de.fu_berlin.inf.dpp.SarosPluginContext;
import de.fu_berlin.inf.dpp.annotations.Component;
import de.fu_berlin.inf.dpp.session.ISarosSession;
import de.fu_berlin.inf.dpp.session.ISarosSessionManager;
import de.fu_berlin.inf.dpp.session.User;
import de.fu_berlin.inf.dpp.ui.ImageManager;
import de.fu_berlin.inf.dpp.ui.Messages;
import de.fu_berlin.inf.dpp.ui.util.SWTUtils;
import de.fu_berlin.inf.dpp.ui.util.selection.SelectionUtils;
import de.fu_berlin.inf.dpp.ui.util.selection.retriever.SelectionRetrieverFactory;
import java.io.File;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.picocontainer.annotations.Inject;

@Component(module = "action")
public class GitChangeWorkDirAction extends Action implements Disposable {

  public static final String ACTION_ID = GitChangeWorkDirAction.class.getName();

  private ISelectionListener selectionListener =
      new ISelectionListener() {
        @Override
        public void selectionChanged(IWorkbenchPart part, ISelection selection) {
          updateEnablement();
        }
      };

  @Inject private ISarosSessionManager sessionManager;

  public GitChangeWorkDirAction() {
    super(Messages.GitChangeWorkDirAction_title);
    SarosPluginContext.initComponent(this);

    setId(ACTION_ID);
    setToolTipText(Messages.GitChangeWorkDirAction_title);

    setImageDescriptor(ImageManager.getImageDescriptor("icons/elcl16/changecolor.png"));

    SelectionUtils.getSelectionService().addSelectionListener(selectionListener);

    updateEnablement();
  }

  public void updateEnablement() {
    List<User> participants =
        SelectionRetrieverFactory.getSelectionRetriever(User.class).getSelection();

    ISarosSession session = sessionManager.getSession();

    setEnabled(
        session != null
            && participants.size() == 1
            && participants.get(0).equals(session.getLocalUser()));
  }

  @Override
  public void run() {

    ISarosSession session = sessionManager.getSession();
    if (session == null) return;

    final DirectoryDialog dg = new DirectoryDialog(SWTUtils.getShell(), SWT.OPEN);
    dg.setText(Messages.GitChangeWorkDirAction_directorydialog_text);

    final String path = dg.open();

    if (path == null) return;

    final File directory = new File(path);

    session.gitChangeWorkDir(directory);
  }

  @Override
  public void dispose() {
    SelectionUtils.getSelectionService().removeSelectionListener(selectionListener);
  }
}
