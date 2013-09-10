package org.csstudio.dct.ui.editor.tables;

import java.util.List;

import org.csstudio.dct.ui.editor.tables.editingsupport.ArchivedColumnLabelProvider;
import org.csstudio.dct.ui.editor.tables.editingsupport.ArchivedEditingSupport;
import org.csstudio.dct.ui.editor.tables.editingsupport.DelegatingColumnEditingSupport;
import org.csstudio.dct.ui.editor.tables.editingsupport.LabelProvider;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

/**
 * Convenience wrapper for a SWT table viewer that allows for easy and fine
 * grained customization of all aspects of a table, like
 * 
 * <ul>
 * <li>number of columns</li>
 * <li>fore and background color of used in cells</li>
 * <li>font used in cells</li>
 * <li>cell editors</li>
 * <li>cell content</li>
 * </ul>
 * 
 * The model for the table is a list of {@link ITableRow}s. Each
 * {@link ITableRow} represents an adapter for an arbitrary object.
 * 
 * To use this table just prepare {@link ITableRow} adapters for your model
 * objects.
 * 
 * @author Sven Wende
 * 
 */
public final class ConvenienceTableWrapper {
        
    private ColumnConfig[] columnConfigurations;
    private TableViewer viewer;
    private CommandStack commandStack;
    private Table table;

    /**
     * Constructor.
     * 
     * @param parent
     *            the parent composite
     * @param style
     *            the SWT style constants describing the behavior and appearance
     *            of the table
     * @param commandStack
     *            a command stack which is used when table cells are edited
     * @param columnConfigurations
     *            the configuration the table columns
     */
    public ConvenienceTableWrapper(Composite parent, int style, CommandStack commandStack,
            ColumnConfig[] columnConfigurations) {
        this.columnConfigurations = columnConfigurations;
        this.commandStack = commandStack;
        viewer = doCreateViewer(parent, style);
    }

    /**
     * Sets the table input.
     * 
     * @param input
     *            a list with table rows representing the table input
     */
    public void setInput(List<ITableRow> input) {
        if (input != null) {
            viewer.setInput(input);
            viewer.refresh();
        }
    }

    /**
     * Returns the table viewer.
     * 
     * @return the table viewer
     */
    public TableViewer getViewer() {
        return viewer;
    }

    /**
     * Template method. Subclasses should create the table viewer here.
     * 
     * @param parent
     *            a widget which will be the parent of the new instance (cannot
     *            be null)
     * @param style
     *            the style of widget to construct
     * @return the table viewer
     */
    private TableViewer doCreateViewer(Composite parent, int style) {
        // create table
        table = new Table(parent, style | SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.DOUBLE_BUFFERED
                | SWT.SCROLL_PAGE);
        table.setLinesVisible(true);
        table.setHeaderVisible(false);

        // create viewer
        viewer = new TableViewer(table);
        viewer.setContentProvider(new ContentProvider());
        
        // create columns
        String[] columnNames = new String[columnConfigurations.length];

        for (int i = 0; i < columnConfigurations.length; i++) {
            
            columnNames[i] = columnConfigurations[i].getId();
            TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
            column.getColumn().setText(columnConfigurations[i].getTitle());
            column.getColumn().setMoveable(false);
            column.getColumn().setWidth(columnConfigurations[i].getWidth());
                        
            if (columnConfigurations[i].isCheckBox()) {
                column.setEditingSupport(new ArchivedEditingSupport(viewer, commandStack));                
                column.setLabelProvider(new ArchivedColumnLabelProvider());
            } else {
                column.setEditingSupport(new DelegatingColumnEditingSupport(viewer, i, commandStack));
                column.setLabelProvider(new LabelProvider());
            }
        }

        viewer.setColumnProperties(columnNames);

        ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);

        // configure keyboard support
        TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(viewer,
                new FocusCellOwnerDrawHighlighter(viewer));

        ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(viewer) {
            protected boolean isEditorActivationEvent(final ColumnViewerEditorActivationEvent event) {
                return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
                        || event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
                        || (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.F2)
                        || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
            }

        };

        TableViewerEditor.create(viewer, focusCellManager, actSupport, ColumnViewerEditor.TABBING_HORIZONTAL
                | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL
                | ColumnViewerEditor.KEYBOARD_ACTIVATION);

        // .. sorter
        viewer.setSorter(new ViewerSorter() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                ITableRow r1 = (ITableRow) e1;
                ITableRow r2 = (ITableRow) e2;
                return r1.compareTo(r2);
            }
        });

        return viewer;
    }

  
    /**
     * Content provider implementation.
     * 
     * @author Sven Wende
     */
    static final class ContentProvider implements IStructuredContentProvider {
        /**
         * {@inheritDoc}
         */
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {

        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        public Object[] getElements(final Object parent) {
            return ((List<ITableRow>) parent).toArray();
        }

        /**
         * {@inheritDoc}
         */
        public void dispose() {

        }
    }

 
}
