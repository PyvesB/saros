package saros.intellij.ui.actions;

import java.util.ArrayList;
import java.util.List;
import org.picocontainer.annotations.Inject;
import saros.intellij.editor.EditorManager;
import saros.session.ISarosSession;
import saros.session.ISarosSessionManager;
import saros.session.ISessionLifecycleListener;
import saros.session.SessionEndReason;
import saros.session.User;
import saros.ui.util.ModelFormatUtils;
import saros.util.ThreadUtils;

/** Action to activateor deactivate follow mode. */
public class FollowModeAction extends AbstractSarosAction {

  public static final String NAME = "follow";

  private final ISessionLifecycleListener sessionLifecycleListener =
      new ISessionLifecycleListener() {
        @Override
        public void sessionStarted(final ISarosSession session) {
          ThreadUtils.runSafeAsync(
              LOG,
              new Runnable() {

                @Override
                public void run() {
                  FollowModeAction.this.session = session;
                }
              });
        }

        @Override
        public void sessionEnded(ISarosSession oldSarosSession, SessionEndReason reason) {

          ThreadUtils.runSafeAsync(
              LOG,
              new Runnable() {

                @Override
                public void run() {
                  session = null;
                }
              });
        }
      };

  @Inject public ISarosSessionManager sessionManager;

  @Inject public EditorManager editorManager;

  private ISarosSession session;

  public FollowModeAction() {
    sessionManager.addSessionLifecycleListener(sessionLifecycleListener);
  }

  @Override
  public String getActionName() {
    return NAME;
  }

  public void execute(String userName) {
    if (session == null) {
      return;
    }

    editorManager.setFollowing(findUser(userName));

    actionPerformed();
  }

  @Override
  public void execute() {
    // never called
  }

  public User getCurrentlyFollowedUser() {
    return editorManager.getFollowedUser();
  }

  public List<User> getCurrentRemoteSessionUsers() {
    if (session == null) return new ArrayList<User>();

    return session.getRemoteUsers();
  }

  private User findUser(String userName) {
    if (userName == null) {
      return null;
    }

    for (User user : getCurrentRemoteSessionUsers()) {
      String myUserName = ModelFormatUtils.getDisplayName(user);
      if (myUserName.equalsIgnoreCase(userName)) {
        return user;
      }
    }

    return null;
  }
}
