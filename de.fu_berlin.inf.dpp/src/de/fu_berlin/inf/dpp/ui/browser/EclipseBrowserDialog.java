package de.fu_berlin.inf.dpp.ui.browser;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import de.fu_berlin.inf.dpp.ui.ide_embedding.IBrowserDialog;

/**
 * Implements the Eclipse wrapper for the SWT-specific dialog shell.
 */
public class EclipseBrowserDialog implements IBrowserDialog {

    private final Shell shell;

    public EclipseBrowserDialog(Shell shell) {
        this.shell = shell;
        adjustShellSize();
        centerShellRelativeToParent();
    }

    @Override
    public void close() {
        shell.close();
    }

    @Override
    public void reopen() {
        centerShellRelativeToParent();
        shell.setActive();
        shell.open();
    }

    private void centerShellRelativeToParent() {
        final Composite composite = shell.getParent();

        if (!(composite instanceof Shell))
            return;

        final Shell parent = (Shell) composite;

        final Rectangle parentShellBounds = parent.getBounds();
        final Point shellSize = shell.getSize();

        shell.setLocation(parentShellBounds.x
            + (parentShellBounds.width - shellSize.x) / 2, parentShellBounds.y
            + (parentShellBounds.height - shellSize.y) / 2);
    }

    /**
     * If the shell is bigger that the parent, the shell size is set to 80% of
     * the parent size.
     */
    private void adjustShellSize() {
        final Composite composite = shell.getParent();

        if (!(composite instanceof Shell))
            return;

        final Shell parent = (Shell) composite;

        final Rectangle parentShellBounds = parent.getBounds();
        final Rectangle shellBounds = shell.getBounds();

        if (shellBounds.height > parentShellBounds.height
            || shellBounds.width > parentShellBounds.width) {
            int nWidth = (int) (parentShellBounds.width * 0.8);
            int nHeight = (int) (parentShellBounds.height * 0.8);

            shell.setSize(nWidth, nHeight);
        }

    }
}
