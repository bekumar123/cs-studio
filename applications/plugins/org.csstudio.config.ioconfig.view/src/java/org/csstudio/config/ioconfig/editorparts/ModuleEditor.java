/*
                System.out.println("111111111111");
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.config.ioconfig.editorparts;

import java.io.IOException;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.component.CurrentUserParamDataComponent;
import org.csstudio.config.ioconfig.config.component.IONamesComponent;
import org.csstudio.config.ioconfig.config.component.ModifiedCallback;
import org.csstudio.config.ioconfig.config.component.ModuleSelectionListBox;
import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel2;
import org.csstudio.config.ioconfig.model.types.ModuleNumber;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public final class ModuleEditor extends AbstractGsdNodeEditor<ModuleDBO> {

    public static final String ID = "org.csstudio.config.ioconfig.view.editor.module";

    protected static final Logger LOG = LoggerFactory.getLogger(ModuleEditor.class);

    private ModuleDBO module;

    private ModuleSelectionListBox moduleSelectionListBox;

    private Optional<ModuleNumber> selectedModuleNumber;

    private Composite gridComposite;

    private CurrentUserParamDataComponent currentUserParamDataComponent;

    private IONamesComponent ioNamesComponent;

    private final class TextChangedCallback implements ModifiedCallback {
        @Override
        public void modified(final String event, boolean modified) {
            Preconditions.checkNotNull(event, "event must not be null");
            setSavebuttonEnabled(event, modified);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(@Nonnull final Composite parent) {

        selectedModuleNumber = Optional.absent();
        
        module = getNode();

        super.createPartControl(parent);

        setSavebuttonEnabled(null, getNode().isPersistent());
        buildIoNames("IO-Names");
        buildModule("Module");

        selectTabFolder(0);
        
        if (module.getModuleNumber() == -1) {
            getSaveButton().setEnabled(false);
            getNameWidget().setEnabled(false);
        }
        
    }

    private Text moduleConfigData;

    /**
     * @param head
     *            the tabItemName
     * 
     */
    private void buildModule(@Nonnull final String head) {
        final Composite comp = getNewTabItem(head, 2);
        comp.setLayout(new GridLayout(2, false));

        buildNameGroup(comp);

        final Group topGroup = new Group(comp, SWT.NONE);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
        topGroup.setLayoutData(layoutData);
        topGroup.setLayout(new GridLayout(3, false));
        topGroup.setText("Module selection");

        makeDescGroup(comp, 1);

        moduleConfigData = new Text(topGroup, SWT.SINGLE | SWT.LEAD | SWT.READ_ONLY);
        moduleConfigData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
        moduleConfigData.setText(module.getConfigurationData());
        moduleConfigData.setEditable(false);
        moduleConfigData.setEnabled(false);

        buildModuleTypList(comp, topGroup);
    }

    private void buildIoNames(@Nonnull final String head) {
        ioNamesComponent = new IONamesComponent(getNewTabItem(head, 2));
        ioNamesComponent.buildComponent();
        ioNamesComponent.addModifyCallbackIONames(new TextChangedCallback());
        ioNamesComponent.addModifyCallbackChannelDesc(new TextChangedCallback());
    }

    private void buildNameGroup(@Nonnull final Composite comp) {
        final Group gName = new Group(comp, SWT.NONE);
        gName.setText("Name");
        gName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        gName.setLayout(new GridLayout(3, false));

        setNameWidget(new Text(gName, SWT.BORDER | SWT.SINGLE));
        final Text nameWidget = getNameWidget();
        if (nameWidget != null) {
            setText(nameWidget, module.getName(), 255);
            nameWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        }
        setIndexSpinner(ConfigHelper
                .getIndexSpinner(gName, module, getMLSB(), "Sort index:", getProfiBusTreeView(), 99));
    }

    //@formatter:off
    private void buildModuleTypList(
            @Nonnull final Composite comp,
            @Nonnull final Group topGroup) {
            //@formatter:on

        gridComposite = new Composite(topGroup, SWT.NONE);
        gridComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        gridComposite.setLayout(new GridLayout(2, false));

        moduleSelectionListBox = new ModuleSelectionListBox(gridComposite, getGsdFile(),
                ModuleNumber.moduleNumber(module.getModuleNumber()));
        moduleSelectionListBox.config().ignoreModulesWithoutPrototype();

        Optional<ModuleNumber> moduleNumber = ModuleNumber.moduleNumber(module.getModuleNumber());

        if (moduleNumber.isPresent()) {
            
            moduleSelectionListBox.config().readOnly().autoFilter();
            ioNamesComponent.updateIONamesText(module);
            
        } else {

            //@formatter:off
            moduleSelectionListBox.addSelectionChangedListener(new ISelectionChangedListener() {
                
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    
                    if (!(event.getSelection() instanceof IStructuredSelection)) {
                        throw new IllegalStateException("selection must be IStrucutredSeleciton");
                    }
                    
                    IStructuredSelection structuredSelection = (IStructuredSelection)event.getSelection();
                    final GsdModuleModel2 selectedModule = (GsdModuleModel2)(structuredSelection.getFirstElement());
                                        
                    selectedModuleNumber = ModuleNumber.moduleNumber(selectedModule.getModuleNumber());
                    
                    getSaveButton().setEnabled(true);
                                        
                }
                
            });
            //@formatter:on
            
        }

        moduleSelectionListBox.buildComponent();

        try {
            makeCurrentUserParamData(gridComposite);
            comp.layout();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param topGroup
     *            The parent Group for the CurrentUserParamData content.
     * @throws IOException
     */
    protected void makeCurrentUserParamData(@Nonnull final Composite topGroup) throws IOException {

        if (currentUserParamDataComponent != null) {
            currentUserParamDataComponent.dispose();
        }

        currentUserParamDataComponent = new CurrentUserParamDataComponent(topGroup, this);
        currentUserParamDataComponent.buildComponent();

        topGroup.getParent().layout();

    }

    public void updateModulConfigData() {
        moduleConfigData.setText(module.getConfigurationData());
    }

    @Override
    protected void currentUserPrmDataChanged() {
        super.currentUserPrmDataChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSave(@Nullable final IProgressMonitor monitor) {
        super.doSave(monitor);
        
        try {
            //
            // create new module
            //
            if (module.getModuleNumber() == -1) {
                module.setNewModel(selectedModuleNumber.get().getValue(), AbstractNodeEditor.getUserName());
                GSDModuleDBO gsdModule = module.getGSDModule();
                module.setName(gsdModule.getName());
                setPartName(gsdModule.getName());
                getNameWidget().setText(gsdModule.getName());            
            }
        } catch (PersistenceException e1) {
            LOG.error("Can't create new module.", e1);
            DeviceDatabaseErrorDialog.open(null, "Can't create new module.", e1);
            return;
        }

        // Module
        final Text nameWidget = getNameWidget();
        if (nameWidget != null) {
            module.setName(nameWidget.getText());
            nameWidget.setData(nameWidget.getText());
        }

        final Spinner indexSpinner = getIndexSpinner();
        if (indexSpinner != null) {
            indexSpinner.setData(indexSpinner.getSelection());
        }

        try {

            ioNamesComponent.save(module);
            saveUserPrmData();

            // Document
            if (getDocumentationManageView() != null) {
                module.setDocuments(getDocumentationManageView().getDocuments());
            }

            save();

            updateModulConfigData();
            makeCurrentUserParamData(gridComposite);
            ioNamesComponent.updateIONamesText(module);
            
            moduleSelectionListBox.setEditable(false);
            getNameWidget().setEnabled(true);
            
            Repository.refresh(getNode().getParent());

        } catch (final PersistenceException e) {
            LOG.error("Can't save Module! Database error.", e);
            DeviceDatabaseErrorDialog.open(null, "Can't save Module! Database error.", e);
        } catch (final IOException e2) {
            DeviceDatabaseErrorDialog.open(null, "Can't save Slave.GSD File read error", e2);
            LOG.error("Can't save Slave.GSD File read error", e2);
        }
    }

    /**
     * Cancel all change value.
     */
    @Override
    public final void cancel() {

        super.cancel();
        cancelNameWidget();
        cancelIndexSpinner();
        cancelGsdModuleModel();
        ioNamesComponent.undo();
        currentUserParamDataComponent.undo();

        save();

    }

    public void cancelNameWidget() {
        final Text nameWidget = getNameWidget();
        if (nameWidget != null) {
            nameWidget.setText((String) nameWidget.getData());
        }
    }

    public void cancelIndexSpinner() {
        final Spinner indexSpinner = getIndexSpinner();
        if (indexSpinner != null) {
            if (indexSpinner.getData() instanceof Integer) {
                indexSpinner.setSelection((Integer) indexSpinner.getData());
            } else {
                indexSpinner.setSelection((Short) indexSpinner.getData());
            }
        }
    }

    public void cancelGsdModuleModel() {
        try {
            final GSDFileDBO gsdFile = module.getGSDFile();
            if (gsdFile != null) {
                Optional<ModuleNumber> moduleNumber = moduleSelectionListBox.getModuleNumber();
                if (moduleNumber.isPresent()) {
                    final GsdModuleModel2 gsdModuleModel = gsdFile.getParsedGsdFileModel().getModule(
                            moduleNumber.get().getValue());
                    if (gsdModuleModel != null) {
                        moduleSelectionListBox.select(gsdModuleModel);
                    }
                }
            }
        } catch (final NullPointerException e) {
            moduleSelectionListBox.selectFirstRow();
        }
    }


    /** {@inheritDoc} */
    @Override
    public final void fill(@Nullable final GSDFileDBO gsdFile) {
        return;
    }

    /** {@inheritDoc} */
    @Override
    @CheckForNull
    public final GSDFileDBO getGsdFile() {
        return module.getSlave().getGSDFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGsdFile(@CheckForNull final GSDFileDBO gsdFile) {
        module.getSlave().setGSDFile(gsdFile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    GsdModuleModel2 getGsdPropertyModel() throws IOException {
        return module.getGsdModuleModel2();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    List<Integer> getPrmUserDataList() {
        return module.getConfigurationDataList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setPrmUserData(@Nonnull final Integer index, @Nonnull final Integer value) {
        module.setConfigurationDataByte(index, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    Integer getPrmUserData(@Nonnull final Integer index) {
        if (module.getConfigurationDataList().size() > index) {
            return module.getConfigurationDataList().get(index);
        }
        return null;
    }
}
