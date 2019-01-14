package saros.intellij.ui.views;

import java.awt.FlowLayout;
import javax.swing.JToolBar;
import saros.intellij.ui.actions.NotImplementedAction;
import saros.intellij.ui.views.buttons.ConnectButton;
import saros.intellij.ui.views.buttons.ConsistencyButton;
import saros.intellij.ui.views.buttons.FollowButton;
import saros.intellij.ui.views.buttons.LeaveSessionButton;
import saros.intellij.ui.views.buttons.SimpleButton;

/**
 * Saros toolbar. Displays several buttons for interacting with Saros.
 *
 * <p>FIXME: Replace by IDEA toolbar class.
 */
public class SarosToolbar extends JToolBar {
  public static final String ADD_CONTACT_ICON_PATH = "/icons/famfamfam/contact_add_tsk.png";
  public static final String OPEN_REFS_ICON_PATH = "/icons/famfamfam/test_con.gif";

  private static final boolean ENABLE_FOLLOW_MODE =
      Boolean.getBoolean("saros.intellij.ENABLE_FOLLOW_MODE");
  private static final boolean ENABLE_ADD_CONTACT =
      Boolean.getBoolean("saros.intellij.ENABLE_ADD_CONTACT");
  private static final boolean ENABLE_PREFERENCES =
      Boolean.getBoolean("saros.intellij.ENABLE_PREFERENCES");

  public SarosToolbar() {
    super("Saros IDEA toolbar");
    setLayout(new FlowLayout(FlowLayout.RIGHT));
    addToolbarButtons();
  }

  private void addToolbarButtons() {

    add(new ConnectButton());

    if (ENABLE_ADD_CONTACT) {
      add(
          new SimpleButton(
              new NotImplementedAction("addContact"),
              "Add contact to list",
              ADD_CONTACT_ICON_PATH,
              "addContact"));
    }

    if (ENABLE_PREFERENCES) {
      add(
          new SimpleButton(
              new NotImplementedAction("preferences"),
              "Open preferences",
              OPEN_REFS_ICON_PATH,
              "preferences"));
    }

    if (ENABLE_FOLLOW_MODE) {
      add(new FollowButton());
    }

    add(new ConsistencyButton());

    add(new LeaveSessionButton());
  }
}
