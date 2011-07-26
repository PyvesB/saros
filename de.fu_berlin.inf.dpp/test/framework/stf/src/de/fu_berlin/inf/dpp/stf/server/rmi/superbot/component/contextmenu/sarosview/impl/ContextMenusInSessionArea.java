package de.fu_berlin.inf.dpp.stf.server.rmi.superbot.component.contextmenu.sarosview.impl;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import de.fu_berlin.inf.dpp.net.JID;
import de.fu_berlin.inf.dpp.stf.server.bot.widget.ContextMenuHelper;
import de.fu_berlin.inf.dpp.stf.server.rmi.superbot.component.contextmenu.sarosview.IContextMenusInSessionArea;
import de.fu_berlin.inf.dpp.stf.server.rmi.superbot.impl.SuperBot;

public final class ContextMenusInSessionArea extends ContextMenusInSarosView
    implements IContextMenusInSessionArea {

    private static final Logger log = Logger
        .getLogger(ContextMenusInSessionArea.class);

    protected JID participantJID;

    private static final ContextMenusInSessionArea INSTANCE = new ContextMenusInSessionArea();

    public static ContextMenusInSessionArea getInstance() {
        return INSTANCE;
    }

    public void setParticipantJID(JID jid) {
        this.participantJID = jid;
    }

    public void grantWriteAccess() throws RemoteException {
        log.trace("granting write access to: " + participantJID.getBase());

        if (hasWriteAccess()) {
            throw new RuntimeException("user \"" + participantJID.getBase()
                + "\" already has write access!.");
        }

        SWTBotTreeItem treeItem = getTreeItem();

        try {
            treeItem.select();
            ContextMenuHelper.clickContextMenu(tree, CM_GRANT_WRITE_ACCESS);
        } catch (RuntimeException e) {
            logError(log, e, tree, treeItem);
            throw e;
        }
        waitUntilHasWriteAccess();
    }

    public void restrictToReadOnlyAccess() throws RemoteException {
        log.trace("revoking write access from: " + participantJID.getBase());

        if (!hasWriteAccess()) {
            throw new RuntimeException("user \"" + participantJID.getBase()
                + "\" already has read-only access!");
        }

        SWTBotTreeItem treeItem = getTreeItem();

        try {
            treeItem.select();
            ContextMenuHelper.clickContextMenu(tree,
                CM_RESTRICT_TO_READ_ONLY_ACCESS);
        } catch (RuntimeException e) {
            logError(log, e, tree, treeItem);
            throw e;
        }

        waitUntilHasReadOnlyAccess();
    }

    public void followParticipant() throws RemoteException {

        if (isFollowing()) {
            log.debug(participantJID.getBase() + " is already followed by you.");
            return;
        }
        if (SuperBot.getInstance().getJID().equals(participantJID)) {
            throw new RuntimeException("you can't follow yourself");
        }

        SWTBotTreeItem treeItem = getTreeItem();

        try {
            treeItem.select();
            ContextMenuHelper.clickContextMenu(tree, CM_FOLLOW_PARTICIPANT);
        } catch (RuntimeException e) {
            logError(log, e, tree, treeItem);
            throw e;
        }
    }

    public void stopFollowing() throws RemoteException {
        log.debug(" JID of the followed user: " + participantJID.getBase());

        SWTBotTreeItem treeItem = getTreeItem();

        try {
            treeItem.select();
            ContextMenuHelper.clickContextMenu(tree, CM_STOP_FOLLOWING);
        } catch (RuntimeException e) {
            logError(log, e, tree, treeItem);
            throw e;
        }

        waitUntilIsNotFollowing();
    }

    public void jumpToPositionOfSelectedBuddy() throws RemoteException {
        if (SuperBot.getInstance().getJID().equals(participantJID)) {
            throw new RuntimeException(
                "you can't jump to the position of yourself");
        }

        SWTBotTreeItem treeItem = getTreeItem();

        try {
            treeItem.select();
            ContextMenuHelper.clickContextMenu(tree,
                CM_JUMP_TO_POSITION_SELECTED_BUDDY);
        } catch (RuntimeException e) {
            logError(log, e, tree, treeItem);
            throw e;
        }

    }

    public void addProjects(String... projectNames) throws RemoteException {

        SWTBotTreeItem treeItem = getTreeItem();

        try {
            treeItem.select();
            ContextMenuHelper.clickContextMenu(tree, ADD_PROJECTS);
        } catch (RuntimeException e) {
            logError(log, e, tree, treeItem);
            throw e;
        }

        SuperBot.getInstance().confirmShellAddProjectsToSession(projectNames);
    }

    public void addBuddies(String... jidOfInvitees) throws RemoteException {
        SWTBotTreeItem treeItem = getTreeItem();

        try {
            treeItem.select();
            ContextMenuHelper.clickContextMenu(tree, ADD_BUDDIES);
        } catch (RuntimeException e) {
            logError(log, e, tree, treeItem);
            throw e;
        }

        SuperBot.getInstance().confirmShellAddBuddyToSession(jidOfInvitees);
    }

    public void shareProjects(String projectName, JID... jids)
        throws RemoteException {

        SWTBotTreeItem treeItem = getTreeItem();

        try {
            treeItem.select();
            ContextMenuHelper.clickContextMenu(tree, SHARE_PROJECTS);
        } catch (RuntimeException e) {
            logError(log, e, tree, treeItem);
            throw e;
        }

        SuperBot.getInstance().confirmShellShareProjects(projectName, jids);
    }

    public boolean hasWriteAccess() throws RemoteException {
        log.trace("checking if participant '" + participantJID.getBase()
            + "' has write access");

        SWTBotTreeItem treeItem = null;
        try {
            treeItem = getTreeItem();
            treeItem.select();
            return !ContextMenuHelper.getContextMenu(tree,
                CM_GRANT_WRITE_ACCESS).isEnabled()
                && !treeItem.getText().contains(PERMISSION_NAME);
        } catch (RuntimeException e) {
            logError(log, e, tree, treeItem);
            return false;
        }
    }

    public boolean hasReadOnlyAccess() throws RemoteException {
        log.trace("checking if participant '" + participantJID.getBase()
            + "' has read only access");
        SWTBotTreeItem treeItem = null;
        try {
            treeItem = getTreeItem();
            treeItem.select();
            return !ContextMenuHelper.getContextMenu(tree,
                CM_RESTRICT_TO_READ_ONLY_ACCESS).isEnabled()
                && treeItem.getText().contains(PERMISSION_NAME);
        } catch (RuntimeException e) {
            logError(log, e, tree, treeItem);
            return false;
        }

    }

    public boolean isFollowing() throws RemoteException {
        log.trace("checking if local user is following participant: "
            + participantJID.getBase());

        SWTBotTreeItem treeItem = null;
        try {
            treeItem = getTreeItem();
            treeItem.select();
            return ContextMenuHelper.existsContextMenu(tree, CM_STOP_FOLLOWING)
                && ContextMenuHelper.isContextMenuEnabled(tree,
                    CM_STOP_FOLLOWING);
        } catch (RuntimeException e) {
            logError(log, e, tree, treeItem);
            return false;
        }
    }

    public void waitUntilHasWriteAccess() throws RemoteException {
        log.trace("waiting for participant '" + participantJID.getBase()
            + "' to gain write access");
        new SWTBot().waitUntil(new DefaultCondition() {
            public boolean test() throws Exception {
                return hasWriteAccess();
            }

            public String getFailureMessage() {
                return "unable to grant write access to "
                    + participantJID.getBase();
            }
        });
    }

    public void waitUntilHasReadOnlyAccess() throws RemoteException {
        log.trace("waiting for participant '" + participantJID.getBase()
            + "' to gain read only access");
        new SWTBot().waitUntil(new DefaultCondition() {
            public boolean test() throws Exception {
                return hasReadOnlyAccess();
            }

            public String getFailureMessage() {
                return "unable to restrict " + participantJID.getBase()
                    + " to read-only access";
            }
        });
    }

    public void waitUntilIsFollowing() throws RemoteException {
        log.trace("waiting to follow participant: " + participantJID.getBase());
        new SWTBot().waitUntil(new DefaultCondition() {
            public boolean test() throws Exception {
                return isFollowing();
            }

            public String getFailureMessage() {
                return "unable to follow " + participantJID.getBase();
            }
        });
    }

    public void waitUntilIsNotFollowing() throws RemoteException {
        log.trace("waiting to stop following participant: "
            + participantJID.getBase());
        new SWTBot().waitUntil(new DefaultCondition() {
            public boolean test() throws Exception {
                return !isFollowing();
            }

            public String getFailureMessage() {
                return "unable to stop following mode on user"
                    + participantJID.getBase();
            }
        });
    }
}