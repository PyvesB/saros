package saros.intellij.ui.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.picocontainer.annotations.Inject;
import saros.SarosPluginContext;
import saros.intellij.editor.EditorManager;
import saros.session.User;

/** Session pop-up menu that displays the option to follow a participant. */
class SessionPopMenu extends JPopupMenu {

  @Inject private EditorManager editorManager;

  public SessionPopMenu(final User user) {
    SarosPluginContext.initComponent(this);
    JMenuItem menuItemFollowParticipant = new JMenuItem("Follow participant");
    menuItemFollowParticipant.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent actionEvent) {
            editorManager.setFollowing(user);
          }
        });
    add(menuItemFollowParticipant);
  }
}
