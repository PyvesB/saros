package de.fu_berlin.inf.dpp.stf.server.rmiSarosSWTBot.eclipse.workbench.menuBar;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import de.fu_berlin.inf.dpp.stf.server.rmiSarosSWTBot.eclipse.EclipseComponentImp;
import de.fu_berlin.inf.dpp.stf.server.rmiSarosSWTBot.eclipse.workbench.Perspective;

public class WindowMImp extends EclipseComponentImp implements WindowM {

    private static transient WindowMImp windowImp;

    /**
     * {@link FileMImp} is a singleton, but inheritance is possible.
     */
    public static WindowMImp getInstance() {
        if (windowImp != null)
            return windowImp;
        windowImp = new WindowMImp();
        return windowImp;
    }

    /**********************************************
     * 
     * change setting with preferences dialog
     * 
     **********************************************/
    public void setNewTextFileLineDelimiter(String OS) throws RemoteException {
        clickMenuPreferences();
        SWTBotTree tree = bot.tree();
        tree.expandNode(TREE_ITEM_GENERAL_IN_PRFERENCES).select(TREE_ITEM_WORKSPACE_IN_PREFERENCES);

        if (OS.equals("Default")) {
            bot.radioInGroup("Default", "New text file line delimiter").click();
        } else {
            bot.radioInGroup("Other:", "New text file line delimiter").click();
            bot.comboBoxInGroup("New text file line delimiter")
                .setSelection(OS);
        }
        bot.button(APPLY).click();
        bot.button(OK).click();
        shellC.waitUntilShellClosed(SHELL_PREFERNCES);
    }

    public String getTextFileLineDelimiter() throws RemoteException {
        clickMenuPreferences();
        SWTBotTree tree = bot.tree();
        tree.expandNode(TREE_ITEM_GENERAL_IN_PRFERENCES).select(TREE_ITEM_WORKSPACE_IN_PREFERENCES);
        if (bot.radioInGroup("Default", "New text file line delimiter")
            .isSelected()) {
            shellC.closeShell(SHELL_PREFERNCES);
            return "Default";
        } else if (bot.radioInGroup("Other:", "New text file line delimiter")
            .isSelected()) {
            SWTBotCombo combo = bot
                .comboBoxInGroup("New text file line delimiter");
            String itemName = combo.items()[combo.selectionIndex()];
            shellC.closeShell(SHELL_PREFERNCES);
            return itemName;
        }
        shellC.closeShell(SHELL_PREFERNCES);
        return "";
    }

    public void clickMenuPreferences() throws RemoteException {
        if (getOS() == TypeOfOS.MAC)
            menuW.clickMenuWithTexts("Eclipse", "Preferences...");
        else
            menuW.clickMenuWithTexts(MENU_WINDOW, MENU_PREFERENCES);
    }

    /**********************************************
     * 
     * show view with main menu
     * 
     **********************************************/
    public void showViewProblems() throws RemoteException {
        showViewWithName(TREE_ITEM_GENERAL_IN_SHELL_SHOW_VIEW, TREE_ITEM_PROBLEM_IN_SHELL_SHOW_VIEW);
    }

    public void showViewProjectExplorer() throws RemoteException {
        showViewWithName(TREE_ITEM_GENERAL_IN_SHELL_SHOW_VIEW, TREE_ITEM_PROJECT_EXPLORER_IN_SHELL_SHOW_VIEW);
    }

    public void showViewWithName(String category, String nodeName)
        throws RemoteException {
        workbenchC.activateWorkbench();
        menuW.clickMenuWithTexts(MENU_WINDOW, MENU_SHOW_VIEW, MENU_OTHER);
        shellC.confirmShellWithTreeWithFilterText(MENU_SHOW_VIEW, category,
            nodeName, OK);
    }

    /**********************************************
     * 
     * all related actions with perspective
     * 
     **********************************************/
    public void openPerspective() throws RemoteException {
        switch (Perspective.WHICH_PERSPECTIVE) {
        case JAVA:
            openPerspectiveJava();
            break;
        case DEBUG:
            openPerspectiveDebug();
            break;
        case RESOURCE:
            openPerspectiveResource();
            break;
        default:
            openPerspectiveJava();
            break;
        }
    }

    public void openPerspectiveResource() throws RemoteException {
        openPerspectiveWithId(ID_RESOURCE_PERSPECTIVE);
    }

    public void openPerspectiveJava() throws RemoteException {
        openPerspectiveWithId(ID_JAVA_PERSPECTIVE);
    }

    public boolean isJavaPerspectiveActive() throws RemoteException {
        return isPerspectiveActive(ID_JAVA_PERSPECTIVE);
    }

    public void openPerspectiveDebug() throws RemoteException {
        openPerspectiveWithId(ID_DEBUG_PERSPECTIVE);
    }

    public boolean isDebugPerspectiveActive() throws RemoteException {
        return isPerspectiveActive(ID_DEBUG_PERSPECTIVE);
    }

    protected void precondition() throws RemoteException {
        workbenchC.activateWorkbench();
    }

    /**
     * Open a perspective using Window->Open Perspective->Other... The method is
     * defined as helper method for other openPerspective* methods and should
     * not be exported using rmi.
     * 
     * 1. if the perspective already exist, return.
     * 
     * 2. activate the saros-instance-window(alice / bob / carl). If the
     * workbench isn't active, delegate can't find the main menus.
     * 
     * 3. click main menus Window -> Open perspective -> Other....
     * 
     * 4. confirm the pop-up window "Open Perspective".
     * 
     * @param persID
     *            example: "org.eclipse.jdt.ui.JavaPerspective"
     */
    public void openPerspectiveWithId(final String persID)
        throws RemoteException {
        if (!isPerspectiveActive(persID)) {
            workbenchC.activateWorkbench();
            try {
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        final IWorkbench wb = PlatformUI.getWorkbench();
                        IPerspectiveDescriptor[] descriptors = wb
                            .getPerspectiveRegistry().getPerspectives();
                        for (IPerspectiveDescriptor per : descriptors) {
                            log.debug("installed perspective id:" + per.getId());
                        }
                        final IWorkbenchWindow win = wb
                            .getActiveWorkbenchWindow();
                        try {
                            wb.showPerspective(persID, win);
                        } catch (WorkbenchException e) {
                            log.debug("couldn't open perspective wit ID"
                                + persID, e);
                        }
                    }
                });
            } catch (IllegalArgumentException e) {
                log.debug("Couldn't initialize perspective with ID" + persID,
                    e.getCause());
            }

        }

    }

    /**
     * 
     * @param id
     *            id which identify a perspective
     * @return<tt>true</tt>, if the perspective specified with the given id is
     *                       active.
     */
    public boolean isPerspectiveActive(String id) {
        return bot.perspectiveById(id).isActive();
    }

    /**
     * 
     * @param title
     *            the title of a perspective.
     * @return<tt>true</tt>, if the perspective specified with the given title
     *                       is open.
     */
    public boolean isPerspectiveOpen(String title) {
        return getPerspectiveTitles().contains(title);
    }

    /**
     * 
     * @return titles of all available perspectives.
     */
    protected List<String> getPerspectiveTitles() {
        ArrayList<String> list = new ArrayList<String>();
        for (SWTBotPerspective perspective : bot.perspectives())
            list.add(perspective.getLabel());
        return list;
    }

}
