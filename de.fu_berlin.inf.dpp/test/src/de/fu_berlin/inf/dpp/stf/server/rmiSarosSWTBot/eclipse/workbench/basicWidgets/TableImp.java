package de.fu_berlin.inf.dpp.stf.server.rmiSarosSWTBot.eclipse.workbench.basicWidgets;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.tableHasRows;

import java.rmi.RemoteException;
import java.util.List;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;

import de.fu_berlin.inf.dpp.stf.server.rmiSarosSWTBot.conditions.SarosConditions;
import de.fu_berlin.inf.dpp.stf.server.rmiSarosSWTBot.eclipse.EclipseComponentImp;
import de.fu_berlin.inf.dpp.stf.server.sarosSWTBot.widgets.ContextMenuHelper;

public class TableImp extends EclipseComponentImp implements Table {

    private static transient TableImp tableImp;

    /**
     * {@link TableImp} is a singleton, but inheritance is possible.
     */
    public static TableImp getInstance() {
        if (tableImp != null)
            return tableImp;
        tableImp = new TableImp();
        return tableImp;
    }

    /**************************************************************
     * 
     * exported functions
     * 
     **************************************************************/

    /**********************************************
     * 
     * states
     * 
     **********************************************/
    public boolean existsTableItem(String itemText) throws RemoteException {
        return existsTableItem(bot.table(), itemText);
    }

    public boolean existsTableItemInView(String viewTitle, String itemText)
        throws RemoteException {
        return existsTableItem(getTableInView(viewTitle), itemText);
    }

    public boolean existsTableItem(SWTBotTable table, String itemText) {
        return table.containsItem(itemText);
    }

    public boolean existsContextMenuOfTableItem(String itemName,
        String contextName) throws RemoteException {
        return existsContextMenuOfTableItem(bot.table(), itemName, contextName);
    }

    public boolean existsContextMenuOfTableItemInView(String viewName,
        String itemName, String contextName) throws RemoteException {
        return existsContextMenuOfTableItem(getTableInView(viewName), itemName,
            contextName);
    }

    public boolean existsContextMenuOfTableItem(SWTBotTable table,
        String itemText, String contextName) {
        selectTableItem(table, itemText);
        return ContextMenuHelper.existsContextMenu(table, contextName);
    }

    public boolean isContextMenuOfTableItemVisible(String itemText,
        String contextName) throws RemoteException {
        return isContextMenuOfTableItemVisible(bot.table(), itemText,
            contextName);
    }

    public boolean isContextMenuOfTableItemVisibleInView(String viewTitle,
        String itemText, String contextName) throws RemoteException {
        return isContextMenuOfTableItemVisible(getTableInView(viewTitle),
            itemText, contextName);
    }

    public boolean isContextMenuOfTableItemEnabled(String itemText,
        String contextName) throws RemoteException {
        return isContextMenuOfTableItemEnabled(bot.table(), itemText,
            contextName);
    }

    public boolean isContextMenuOfTableItemEnabledInView(String viewTitle,
        String itemText, String contextName) throws RemoteException {
        return isContextMenuOfTableItemEnabled(getTableInView(viewTitle),
            itemText, contextName);
    }

    public List<String> getTableColumns() throws RemoteException {
        return bot.table().columns();
    }

    /**********************************************
     * 
     * actions
     * 
     **********************************************/
    public void selectTableItem(String itemText) throws RemoteException {
        selectTableItem(bot.table(), itemText);
    }

    public void selectTableItemInView(String viewTitle, String itemText)
        throws RemoteException {
        selectTableItem(getTableInView(viewTitle), itemText);
    }

    public void clickContextMenuOfTableItem(String itemText, String contextName)
        throws RemoteException {
        clickContextMenuOfTableItem(bot.table(), itemText, contextName);
    }

    public void clickContextMenuOfTableItemInView(String viewTitle,
        String itemText, String contextName) throws RemoteException {
        clickContextMenuOfTableItem(getTableInView(viewTitle), itemText,
            contextName);
    }

    public void selectCheckBoxInTable(String itemText) throws RemoteException {
        for (int i = 0; i < bot.table().rowCount(); i++) {
            if (bot.table().getTableItem(i).getText(0).equals(itemText)) {
                bot.table().getTableItem(i).check();
                log.debug("Found checkbox item \"" + itemText + "\".");
                return;
            }
        }
        throw new WidgetNotFoundException("No checkbox item found with text \""
            + itemText + "\".");
    }

    public void selectCheckBoxsInTable(List<String> itemTexts)
        throws RemoteException {
        for (int i = 0; i < bot.table().rowCount(); i++) {
            String next = bot.table().getTableItem(i).getText(0);
            if (itemTexts.contains(next)) {
                bot.table().getTableItem(i).check();
            }
        }
    }

    /**********************************************
     * 
     * waits until
     * 
     **********************************************/
    public void waitUntilTableItemExisted(Table basic, String itemText)
        throws RemoteException {
        waitUntil(SarosConditions.existTableItem(this, itemText));
    }

    public void waitUntilTableHasRows(int row) throws RemoteException {
        waitUntil(tableHasRows(bot.table(), row));
    }

    public void waitUntilIsContextMenuOfTableItemEnabled(Table basic,
        String itemText, String contextName) throws RemoteException {
        waitUntil(SarosConditions.ExistContextMenuOfTableItem(this, itemText,
            contextName));
    }

    /**************************************************************
     * 
     * inner functions
     * 
     **************************************************************/
    /**
     * 
     * @param viewTitle
     *            the title on the view tab.
     * @return a {@link SWTBotTable} with the specified <code>none</code> in the
     *         given view.
     */
    public SWTBotTable getTableInView(String viewTitle) {
        return bot.viewByTitle(viewTitle).bot().table();
    }

    public void clickContextMenuOfTableItem(SWTBotTable table, String itemText,
        String contextName) {
        selectTableItem(table, itemText);
        ContextMenuHelper.clickContextMenu(table, contextName);
    }

    public void selectTableItem(SWTBotTable table, String itemText) {
        try {
            table.getTableItem(itemText).select();
        } catch (WidgetNotFoundException e) {
            log.warn("tableItem matching the itemText " + itemText
                + " doesn't exist.", e);
        }
    }

    public boolean isContextMenuOfTableItemEnabled(SWTBotTable table,
        String itemText, String contextName) {
        selectTableItem(table, itemText);
        return ContextMenuHelper.isContextMenuEnabled(table, contextName);
    }

    public boolean isContextMenuOfTableItemVisible(SWTBotTable table,
        String itemText, String contextName) {
        return getTableItem(table, itemText).contextMenu(contextName)
            .isVisible();
    }

    /**
     * 
     * @param itemText
     *            the table item' name, which you want to select.
     * @return a {@link SWTBotTableItem} specified with the given label.
     */
    public SWTBotTableItem getTableItem(String itemText) {
        return getTableItem(bot.table(), itemText);
    }

    /**
     * 
     * @param viewTitle
     *            the title on the view tab.
     * @param itemText
     *            the table item' name
     * @return a {@link SWTBotTableItem} specified with the given label in the
     *         given view.
     */
    public SWTBotTableItem getTableItemInView(String viewTitle, String itemText) {
        return getTableItem(getTableInView(viewTitle), itemText);
    }

    /**
     * 
     * @param table
     *            the parent widget of the found tableItem.
     * @param itemText
     *            the table item' name
     * @return a {@link SWTBotTableItem} in the given table specified with the
     *         given itemText.
     */
    public SWTBotTableItem getTableItem(SWTBotTable table, String itemText) {
        try {
            return table.getTableItem(itemText);
        } catch (WidgetNotFoundException e) {
            log.warn("tableItem matching the itemText " + itemText
                + " doesn't exist.", e);
        }
        return null;
    }

}
