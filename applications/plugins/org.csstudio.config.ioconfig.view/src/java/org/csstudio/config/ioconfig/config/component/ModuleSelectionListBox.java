package org.csstudio.config.ioconfig.config.component;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.component.labelprovider.ModuleListLabelProvider;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel2;
import org.csstudio.config.ioconfig.model.types.ModuleList;
import org.csstudio.config.ioconfig.model.types.ModuleName;
import org.csstudio.config.ioconfig.model.types.ModuleNumber;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
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
import com.google.common.base.Strings;

public class ModuleSelectionListBox implements IComponent, IModuleNumberProvider, IRefreshable {

    protected static final Logger LOG = LoggerFactory.getLogger(ModuleSelectionListBox.class);

    private Text filter;
    
    private final Composite composite;
    private final ModuleList moduleList;
    private final GSDFileDBO gsdFile;
    private final Optional<ModuleNumber> selectedModuleNumber;

    private TableViewer moduleTypList;
    private ModuleSelectionListBoxConfigurator moduleSelectionListBoxConfigurator;
    private Optional<ISelectionChangedListener> selectionChangedListener = Optional.absent();
    
    //@formatter:off
    public ModuleSelectionListBox(
            @Nonnull final Composite composite,   
            @Nonnull final ModuleList moduleList,
            @Nonnull final GSDFileDBO gsdFile,
            Optional<ModuleNumber> selectedModuleNumber) {
            //@formatter:on

        Preconditions.checkNotNull(composite, "composite must not be null");
        Preconditions.checkNotNull(gsdFile, "gsdFile must not be null");
        Preconditions.checkNotNull(selectedModuleNumber, "selectedModuleNumber must not be null");
        Preconditions.checkNotNull(gsdFile.getParsedGsdFileModel(), "getParsedGsdFileModel must not return null");

        this.composite = composite;
        this.moduleList = moduleList;
                
        this.gsdFile = gsdFile;
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

        moduleTypList = new TableViewer(gridComposite, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        layoutData.minimumWidth = 250;

        moduleTypList.getTable().setLayoutData(layoutData);
        moduleTypList.setContentProvider(new ModuleListContentProvider());
        moduleTypList.setLabelProvider(new ModuleListLabelProvider(moduleTypList.getTable(),
                moduleSelectionListBoxConfigurator));

        if (selectedModuleNumber.isPresent()) {
            final Optional<ModuleName> selectModuleName = moduleList.getModuleName(selectedModuleNumber);
            if (selectModuleName.isPresent()) {
                Object[] elements = new Object[]{moduleList.getObject(selectedModuleNumber)};
                moduleTypList.setSelection(new StructuredSelection(elements));
                if (moduleSelectionListBoxConfigurator.isAutoFilter()) {
                    filter.setText(selectModuleName.get().getValue());
                }
            }
        }

        setTypListFilter(filter);
        moduleTypList.setInput(moduleList);

        moduleTypList.getTable().showSelection();
        filter.addModifyListener(new FilterModifyListener(this));

        if (moduleSelectionListBoxConfigurator.isReadOnly()) {
            moduleTypList.getTable().setEnabled(false);
            filter.setEditable(false);
        } else {
            if (this.selectionChangedListener.isPresent()) {
                addSelectionChangedListener(selectionChangedListener.get());
            }
        }

    }

    public void setEditable(boolean enabled) {
        moduleTypList.getTable().setEnabled(enabled);    
        filter.setEditable(enabled);
    }
    
    public void addSelectionChangedListener(ISelectionChangedListener selectionChangedListener) {
        if (moduleTypList == null) {
            this.selectionChangedListener = Optional.of(selectionChangedListener);
        } else {
            if (moduleSelectionListBoxConfigurator.isReadOnly()) {
                throw new IllegalStateException("You must not add a SelectionChangedListener to a read only table");
            }
            moduleTypList.addSelectionChangedListener(selectionChangedListener);
        }
    }

    public Optional<ModuleNumber> getModuleNumber() {
        return selectedModuleNumber;
    }

    public void select(GsdModuleModel2 gsdModuleModel) {
        moduleTypList.setSelection(new StructuredSelection(gsdModuleModel), true);
    }

    public void selectFirstRow() {
        moduleTypList.getTable().select(0);
    }

    public void refresh() {
        moduleTypList.refresh();
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

        if (Strings.nullToEmpty(filter.getText()).isEmpty()) {
            return;
        }

        moduleTypList.addFilter(new ViewerFilter() {

            @Override
            public boolean select(@Nullable final Viewer viewer, @Nullable final Object parentElement,
                    @Nullable final Object element) {
                if (element instanceof GsdModuleModel2) {
                    final GsdModuleModel2 gsdModuleModel = (GsdModuleModel2) element;
                    if (filter.getText() == null || filter.getText().length() < 1) {
                        return true;
                    }
                    final String filterString = ".*" + filter.getText().replaceAll("\\*", ".*") + ".*";
                    return gsdModuleModel.toString().matches(filterString);
                }
                return false;
            }

        });

        if (moduleSelectionListBoxConfigurator.isIgnoreModulesWithoutPrototype()) {

            // only display modules with prototype
            moduleTypList.addFilter(new ViewerFilter() {
                @Override
                public boolean select(@Nullable final Viewer viewer, @Nullable final Object parentElement,
                        @Nullable final Object element) {
                    if (element instanceof GsdModuleModel2) {
                        final GsdModuleModel2 gmm = (GsdModuleModel2) element;
                        return hasProtype(ModuleNumber.moduleNumber(gmm.getModuleNumber()));
                    }
                    return true;
                }
            });
        }

    }

    private boolean hasProtype(Optional<ModuleNumber> moduleNumber) {
        if (!moduleNumber.isPresent()) {
            return false;
        }
        GSDModuleDBO module = null;
        if (gsdFile != null) {
            module = gsdFile.getGSDModule(moduleNumber.get().getModuleNumberWithoutVersionInfo());
        }
        return module != null;
    }
    
    /**
     * This class provides the content for the table.
     */
    private static class ModuleListContentProvider implements IStructuredContentProvider {

        public final Object[] getElements(@Nullable final Object arg0) {
            if (arg0 instanceof ModuleList) {
                ModuleList moduleList = (ModuleList)arg0;
                Object[] objects = moduleList.toArray();
                return objects;
            }
            return null;
        }

        @Override
        public final void dispose() {
            // We don't create any resources, so we don't dispose any
        }

        @Override
        public final void inputChanged(@Nullable final Viewer arg0, @Nullable final Object arg1,
                @Nullable final Object arg2) {
            // do nothing
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
