package saros.intellij.ui.views.buttons;

import org.picocontainer.annotations.Inject;
import saros.SarosPluginContext;
import saros.intellij.ui.actions.LeaveSessionAction;
import saros.session.ISarosSession;
import saros.session.ISarosSessionManager;
import saros.session.ISessionLifecycleListener;
import saros.session.SessionEndReason;

public class LeaveSessionButton extends SimpleButton {

  public static final String LEAVE_SESSION_ICON_PATH = "/icons/famfamfam/session_leave_tsk.png";

  private final ISessionLifecycleListener sessionLifecycleListener =
      new ISessionLifecycleListener() {
        @Override
        public void sessionStarted(ISarosSession newSarosSession) {
          setEnabledFromUIThread(true);
        }

        @Override
        public void sessionEnded(ISarosSession oldSarosSession, SessionEndReason reason) {

          setEnabledFromUIThread(false);
        }
      };

  @Inject private ISarosSessionManager sessionManager;

  /**
   * Creates a LeaveSessionButton and registers the sessionListener.
   *
   * <p>LeaveSessionButton is created as disabled.
   */
  public LeaveSessionButton() {
    super(new LeaveSessionAction(), "Leave session", LEAVE_SESSION_ICON_PATH, "leave");
    SarosPluginContext.initComponent(this);
    sessionManager.addSessionLifecycleListener(sessionLifecycleListener);
    setEnabled(false);
  }
}
