package de.fu_berlin.inf.dpp.editor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IElementStateListener;

import de.fu_berlin.inf.dpp.activities.SPath;
import de.fu_berlin.inf.dpp.filesystem.ResourceAdapterFactory;
import de.fu_berlin.inf.dpp.session.IReferencePointManager;
import de.fu_berlin.inf.dpp.ui.util.SWTUtils;

/**
 * Listener registered on Editors to be informed about their dirty state.
 * 
 * There is one global DirtyStateListener for all editors!
 */
public class DirtyStateListener implements IElementStateListener {

    private static final Logger LOG = Logger
        .getLogger(DirtyStateListener.class);

    private final EditorManager editorManager;

    private final Map<IDocumentProvider, Set<IEditorInput>> documentProviders = new HashMap<IDocumentProvider, Set<IEditorInput>>();

    private boolean enabled = true;

    DirtyStateListener(EditorManager editorManager) {
        this.editorManager = editorManager;
    }

    @Override
    public void elementDirtyStateChanged(Object element, boolean isDirty) {

        if (!enabled)
            return;

        if (isDirty)
            return;

        // FIXME this should be handled in the editor manager itself
        if (!editorManager.hasWriteAccess)
            return;

        if (!(element instanceof FileEditorInput))
            return;

        final IFile file = ((FileEditorInput) element).getFile();

        final IReferencePointManager referencePointManager = editorManager
            .getReferencePointManager();

        /*
         * FIXME why must we sync on SWT ? This should only be called in SWT
         * already
         */
        SWTUtils.runSafeSWTSync(LOG, new Runnable() {

            @Override
            public void run() {

                /*
                 * FIXME the logic already only installs listener on shared
                 * editors, why is this check here needed again ?
                 */

                // Only trigger save events for files managed in the editor pool
                if (!editorManager.isManaged(file))
                    return;

                LOG.debug("Dirty state reset for: " + file);
                editorManager.sendEditorActivitySaved(new SPath(
                    ResourceAdapterFactory.create(file), referencePointManager));
            }
        });
    }

    @Override
    public void elementContentAboutToBeReplaced(Object element) {
        // ignore
    }

    @Override
    public void elementContentReplaced(Object element) {
        // ignore
    }

    @Override
    public void elementDeleted(Object element) {
        // ignore
    }

    @Override
    public void elementMoved(Object originalElement, Object movedElement) {
        // ignore
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public void register(IDocumentProvider documentProvider, IEditorInput input) {

        Set<IEditorInput> inputs = documentProviders.get(documentProvider);

        if (inputs == null) {
            inputs = new HashSet<IEditorInput>();
            documentProviders.put(documentProvider, inputs);
        }

        if (inputs.contains(input))
            return;

        if (inputs.size() == 0)
            documentProvider.addElementStateListener(this);

        inputs.add(input);
    }

    public void unregister(IDocumentProvider documentProvider,
        IEditorInput input) {

        Set<IEditorInput> inputs = documentProviders.get(documentProvider);

        if (inputs == null || !inputs.remove(input))
            return;

        if (inputs.size() == 0) {
            documentProvider.removeElementStateListener(this);
            documentProviders.remove(documentProvider);
        }
    }

    public void unregisterAll() {

        for (IDocumentProvider provider : documentProviders.keySet()) {
            // ????
            LOG.warn("DocumentProvider was not correctly"
                + " unregistered yet, EditorPool must be corrupted: "
                + documentProviders);
            provider.removeElementStateListener(this);
        }
        documentProviders.clear();
    }

}