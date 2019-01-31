package de.fu_berlin.inf.dpp.intellij.eventhandler.editor.editorstate;

import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.Topic;
import de.fu_berlin.inf.dpp.intellij.eventhandler.DisableableHandler;

/**
 * Abstract class defining the base functionality needed to create and register/unregister a
 * disableable local editor status change handler.
 *
 * <p>Each handler extending this class should override {@link #subscribe()} to register the needed
 * listener objects as follows:
 *
 * <pre>{@code
 * @Override
 * protected void subscribe() {
 *   super.subscribe();
 *
 *   // register the needed listeners
 *   messageBusConnection.subscribe(TOPIC, LISTENER);
 * }
 * }</pre>
 *
 * @see MessageBusConnection#subscribe(Topic, Object)
 */
public abstract class AbstractLocalEditorStatusChangeHandler implements DisableableHandler {

  private final Project project;

  private boolean enabled;

  @SuppressWarnings("WeakerAccess")
  protected MessageBusConnection messageBusConnection;

  /**
   * Abstract class for local editor status change handlers. The handler is enabled by default. The
   * listeners are <b>not</b> registered by default, meaning each implementing class should call
   * {@link #subscribe()} as part of their constructor.
   *
   * @param project the current Intellij project instance
   */
  AbstractLocalEditorStatusChangeHandler(Project project) {
    this.project = project;

    this.enabled = true;
  }

  /** Creates a MessageBusConnection. */
  protected void subscribe() {
    messageBusConnection = project.getMessageBus().connect();
  }

  /** Disconnects and drops the held MessageBusConnection. */
  private void unsubscribe() {
    messageBusConnection.disconnect();

    messageBusConnection = null;
  }

  /**
   * Enables or disables the handler. This is done by registering or unregistering the held
   * listener.
   *
   * <p>This method does nothing if the given state already matches the current state.
   *
   * @param enabled <code>true</code> to enable the handler, <code>false</code> disable the handler
   */
  @Override
  public void setEnabled(boolean enabled) {
    if (this.enabled && !enabled) {
      unsubscribe();

      this.enabled = false;

    } else if (!this.enabled && enabled) {
      subscribe();

      this.enabled = true;
    }
  }

  /**
   * Returns whether the handler is currently enabled. This also represents whether there currently
   * are any listeners registered.
   *
   * @return whether the handler is currently enabled
   */
  protected boolean isEnabled() {
    return enabled;
  }
}
