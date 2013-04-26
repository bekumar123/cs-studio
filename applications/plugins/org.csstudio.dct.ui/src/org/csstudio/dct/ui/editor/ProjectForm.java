package org.csstudio.dct.ui.editor;

import java.util.List;

import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.commands.ChangeDbdFileCommand;
import org.csstudio.dct.model.commands.ChangeLibraryFileCommand;
import org.csstudio.dct.ui.editor.tables.ITableRow;
import org.csstudio.dct.ui.editor.tables.WorkspaceResourceCellEditor;
import org.csstudio.domain.common.strings.StringUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackEvent;
import org.eclipse.gef.commands.CommandStackEventListener;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Editing form for projects.
 * 
 * @author Sven Wende
 * 
 */
public final class ProjectForm extends AbstractForm<IProject> {

    private CommandStack commandStack;

    private static final Logger LOG = LoggerFactory.getLogger(ProjectForm.class);

    /**
     * Constructor.
     * 
     * @param editor
     *            the editor instance
     */
    public ProjectForm(DctEditor editor) {
        super(editor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doCreateControl(ExpandBar bar, CommandStack commandStack) {
        this.commandStack = commandStack;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetInput(IProject project) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetFormLabel(IProject input) {
        return "Project";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAddCommonRows(List<ITableRow> rows, IProject project) {
        rows.add(new BeanPropertyTableRowAdapter("IOC", project, "ioc", false));
        rows.add(new DbdFileTableRowAdapter(project));
        rows.add(new LibraryFileTableRowAdapter(project));
    }

    /**
     * Row adapter for the dbd file setting.
     * 
     * @author Sven Wende
     * 
     */
    private static class DbdFileTableRowAdapter extends AbstractTableRowAdapter<IProject> {
        public DbdFileTableRowAdapter(IProject delegate) {
            super(delegate);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected String doGetKey(IProject project) {
            return "DDB File Path";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected RGB doGetForegroundColorForKey(IProject delegate) {
            RGB rgb = super.doGetForegroundColorForKey(delegate);

            if (!StringUtil.hasLength(delegate.getDbdPath())) {
                rgb = new RGB(255, 0, 0);
            }
            return rgb;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected String doGetValue(IProject project) {
            return project.getDbdPath();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Command doSetValue(IProject project, Object value) {
            return new ChangeDbdFileCommand(project, value.toString());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected CellEditor doGetValueCellEditor(IProject delegate, Composite parent) {
            return new WorkspaceResourceCellEditor(parent, new String[] { "dbd" }, "Select DBD-File");
        }

    }

    /**
     * Row adapter for the dbd file setting.
     * 
     * @author Sven Wende
     * 
     */
    private class LibraryFileTableRowAdapter extends AbstractTableRowAdapter<IProject> {

        public LibraryFileTableRowAdapter(IProject delegate) {
            super(delegate);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected String doGetKey(IProject project) {
            return "Library File Path";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected RGB doGetForegroundColorForKey(IProject delegate) {
            RGB rgb = super.doGetForegroundColorForKey(delegate);

            if (!StringUtil.hasLength(delegate.getDbdPath())) {
                rgb = new RGB(255, 0, 0);
            }
            return rgb;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected String doGetValue(IProject project) {
            return project.getLibraryPath();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Command doSetValue(IProject project, Object value) {
            commandStack.addCommandStackEventListener(new CommandStackEventListener() {
                public void stackChanged(CommandStackEvent arg0) {
                    if (arg0.isPostChangeEvent()) {
                        Display.getDefault().asyncExec(new Runnable() {
                            public void run() {
                                IWorkbench wb = PlatformUI.getWorkbench();
                                IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
                                IWorkbenchPage page = window.getActivePage();
                                IEditorPart editor = page.getActiveEditor();
                                IEditorInput editorInput = editor.getEditorInput();
                                page.saveEditor(editor, false);
                                page.closeEditor(editor, true);
                                try {
                                    page.openEditor(editorInput, "org.csstudio.dct.ui.DctEditor");
                                } catch (PartInitException e) {
                                    LOG.error("Can't reopen editor", e);
                                }
                            }
                        });
                    }

                }
            });
            return new ChangeLibraryFileCommand(project, value.toString());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected CellEditor doGetValueCellEditor(IProject delegate, Composite parent) {
            return new WorkspaceResourceCellEditor(parent, new String[] { "css-dct" }, "Select Library-File");
        }

    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    protected String doGetAdditionalBreadcrumbLinks(IProject project) {
        return null;
    }
}
