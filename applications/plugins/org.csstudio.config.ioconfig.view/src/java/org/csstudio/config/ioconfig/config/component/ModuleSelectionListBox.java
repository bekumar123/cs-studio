package org.csstudio.config.ioconfig.config.component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.component.labelprovider.ModuleListLabelProvider;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBOReadOnly;
import org.csstudio.config.ioconfig.model.types.ModuleLabel;
import org.csstudio.config.ioconfig.model.types.PrototypeList;
import org.csstudio.config.ioconfig.model.types.ModuleName;
import org.csstudio.config.ioconfig.model.types.ModuleNumber;
import org.csstudio.config.ioconfig.model.types.ParsedModuleInfo;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class ModuleSelectionListBox implements IComponent, IModuleNumberProvider, IRefreshable {

    protected static final Logger LOG = LoggerFactory.getLogger(ModuleSelectionListBox.class);

    private Text filter;

    private final Composite composite;
    private final PrototypeList prototypeList;
    private final ParsedModuleInfo parsedModuleInfo;
    private final Optional<ModuleNumber> selectedModuleNumber;

    private TableViewer tableViewerModuleList;
    private ModuleSelectionListBoxConfigurator moduleSelectionListBoxConfigurator;
    private Optional<ISelectionChangedListener> selectionChangedListener = Optional.absent();

    //@formatter:off
    public ModuleSelectionListBox(
            @Nonnull final Composite composite,   
            @Nonnull final PrototypeList prototypeList,
            @Nonnull final ParsedModuleInfo parsedModuleInfo,
            @Nonnull Optional<ModuleNumber> selectedModuleNumber) {
            //@formatter:on

        Preconditions.checkNotNull(composite, "composite must not be null");
        Preconditions.checkNotNull(prototypeList, "prototypeList must not be null");
        Preconditions.checkNotNull(parsedModuleInfo, "parsedModuleInfo must not be null");
        Preconditions.checkNotNull(selectedModuleNumber, "selectedModuleNumber must not be null");

        this.composite = composite;
        this.prototypeList = prototypeList;
        this.parsedModuleInfo = parsedModuleInfo;

        this.selectedModuleNumber = selectedModuleNumber;
        this.moduleSelectionListBoxConfigurator = new ModuleSelectionListBoxConfigurator();
    }

    public IModuleSelectionListBoxConfigurator config() {
        return moduleSelectionListBoxConfigurator;
    }

    public void buildComponent() {

        Composite gridComposite = new Composite(composite, SWT.NONE);
        gridComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        gridComposite.setLayout(new GridLayout(1, false));

        filter = buildFilterText(gridComposite);

        tableViewerModuleList = new TableViewer(gridComposite, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        layoutData.minimumWidth = 250;

        tableViewerModuleList.getTable().setLayoutData(layoutData);
        tableViewerModuleList.setContentProvider(new ModuleListContentProvider());

        //@formatter:off
        tableViewerModuleList.setLabelProvider(new ModuleListLabelProvider(
                tableViewerModuleList.getTable(),
                prototypeList,
                parsedModuleInfo));
                //@formatter:on

        if (selectedModuleNumber.isPresent()) {
            Object[] elements = new Object[] { prototypeList.getModule(selectedModuleNumber.get()) };
            tableViewerModuleList.setSelection(new StructuredSelection(elements));
            if (moduleSelectionListBoxConfigurator.isAutoFilter()) {
                ModuleLabel moduleLabel = prototypeList.getModuleLabel(selectedModuleNumber.get());
                filter.setText(moduleLabel.buildLabelWithoutModuleNumber());
            }
        }
        
        setTypListFilter(filter);
        tableViewerModuleList.setInput(prototypeList);

        tableViewerModuleList.getTable().showSelection();
        filter.addModifyListener(new FilterModifyListener(this));

        if (moduleSelectionListBoxConfigurator.isReadOnly()) {
            tableViewerModuleList.getTable().setEnabled(false);
            filter.setEditable(false);
        } else {
            if (this.selectionChangedListener.isPresent()) {
                addSelectionChangedListener(selectionChangedListener.get());
            }
        }

    }

    public void disableEditing() {
        tableViewerModuleList.getTable().setEnabled(false);
        filter.setEditable(false);
    }

    public void addSelectionChangedListener(ISelectionChangedListener selectionChangedListener) {
        if (tableViewerModuleList == null) {
            this.selectionChangedListener = Optional.of(selectionChangedListener);
        } else {
            if (moduleSelectionListBoxConfigurator.isReadOnly()) {
                throw new IllegalStateException("You must not add a SelectionChangedListener to a read only table");
            }
            tableViewerModuleList.addSelectionChangedListener(selectionChangedListener);
        }
    }

    public Optional<ModuleNumber> getModuleNumber() {
        return selectedModuleNumber;
    }

    public void select(final ModuleNumber moduleNumber) {
        
        Preconditions.checkNotNull(moduleNumber, "moduleNumber must not be null");
        
        GSDModuleDBOReadOnly gsdModuleDBOReadOnly = prototypeList.getModule(moduleNumber);
        if (gsdModuleDBOReadOnly != null) {
            tableViewerModuleList.setSelection(new StructuredSelection(gsdModuleDBOReadOnly), true);
        }
    }

    public void selectFirstRow() {
        tableViewerModuleList.getTable().select(0);
    }

    public StructuredSelection getStructuredSelection() {
        return (StructuredSelection) tableViewerModuleList.getSelection();
    }

    public void refresh() {
        tableViewerModuleList.refresh();
    }

    @Nonnull
    private Text buildFilterText(@Nonnull final Composite filterComposite) {
        final Text filter = new Text(filterComposite, SWT.SINGLE | SWT.BORDER | SWT.SEARCH);
        filter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        filter.setMessage("Module Filter");
        return filter;
    }

    private void setTypListFilter(@Nonnull final Text filter) {

        if (filter == null) {
            return;
        }

        tableViewerModuleList.addFilter(new ViewerFilter() {
            @Override
            public boolean select(@Nullable final Viewer viewer, @Nullable final Object parentElement,
                    @Nullable final Object element) {
                if (element instanceof GSDModuleDBOReadOnly) {
                    final GSDModuleDBOReadOnly gsdModuleDbo = (GSDModuleDBOReadOnly) element;
                    if (filter.getText() == null || filter.getText().length() < 1) {
                        return true;
                    }
                    return gsdModuleDbo.moduleNameContains(filter.getText());
                }
                return false;
            }
        });

        if (moduleSelectionListBoxConfigurator.isIgnoreModulesWithoutPrototype()) {

            // only display modules with channel configuration
            tableViewerModuleList.addFilter(new ViewerFilter() {
                @Override
                public boolean select(@Nullable final Viewer viewer, @Nullable final Object parentElement,
                        @Nullable final Object element) {
                    if (element instanceof GSDModuleDBOReadOnly) {
                        final GSDModuleDBOReadOnly gsdModuleDbo = (GSDModuleDBOReadOnly) element;
                        return prototypeList.hasChannelConfiguration(gsdModuleDbo.getModuleNumber());
                    }
                    return true;
                }
            });
        }

    }

    /**
     * This class provides the content for the table.
     */
    private static class ModuleListContentProvider implements IStructuredContentProvider {

        public final Object[] getElements(@Nullable final Object arg0) {
            if (arg0 instanceof PrototypeList) {
                PrototypeList prototypeList = (PrototypeList) arg0;
                Object[] objects = prototypeList.toArray();
                return objects;
            }
            return null;
        }

        @Override
        public final void dispose() {
        }

        @Override
        public final void inputChanged(@Nullable final Viewer arg0, @Nullable final Object arg1,
                @Nullable final Object arg2) {
        }

    }

    private static final class FilterModifyListener implements ModifyListener {
        private final IRefreshable refreshable;

        public FilterModifyListener(@Nonnull final IRefreshable refreshable) {
            this.refreshable = refreshable;
        }

        @Override
        public void modifyText(@Nonnull final ModifyEvent e) {
            refreshable.refresh();
        }
    }

}
