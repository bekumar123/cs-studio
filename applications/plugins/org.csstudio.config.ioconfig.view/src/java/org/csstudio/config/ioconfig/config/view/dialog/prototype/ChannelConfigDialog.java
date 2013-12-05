/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.config.ioconfig.config.view.dialog.prototype;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.miginfocom.swt.MigLayout;

import org.csstudio.config.ioconfig.config.component.GridCompositeFactory;
import org.csstudio.config.ioconfig.config.component.ModuleSelectionListBox;
import org.csstudio.config.ioconfig.config.component.WatchableValue;
import org.csstudio.config.ioconfig.config.dialogs.LongRunningOperation;
import org.csstudio.config.ioconfig.config.view.IHasDocumentableObject;
import org.csstudio.config.ioconfig.config.view.dialog.prototype.components.ChannelConfigDialogDataModel;
import org.csstudio.config.ioconfig.config.view.dialog.prototype.components.InfoAreaComponent;
import org.csstudio.config.ioconfig.config.view.dialog.prototype.components.PrototypeVersionDialog;
import org.csstudio.config.ioconfig.config.view.dialog.prototype.components.table.ChannelTableComponent;
import org.csstudio.config.ioconfig.config.view.helper.DocumentationManageView;
import org.csstudio.config.ioconfig.model.IDocumentable;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.config.ioconfig.model.types.ModuleLabel;
import org.csstudio.config.ioconfig.model.types.ModuleNumber;
import org.csstudio.config.ioconfig.model.types.ModuleVersionInfo;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.csstudio.config.ioconfig.view.internal.localization.Messages;
import org.csstudio.dct.util.NotNull;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

public final class ChannelConfigDialog extends Dialog implements IHasDocumentableObject, ISelectedTab {

    protected static final Logger LOG = LoggerFactory.getLogger(ChannelConfigDialog.class);

    private final static int SAVE_BUTON_ID = 29123812;

    private WatchableValue<Boolean> dirty;

    private ChannelTableComponent inputTable;
    private ChannelTableComponent outputTable;
    private TabFolder ioTabFolder;
    private InfoAreaComponent infoAreaComponent;
    private DocumentationManageView documentationManageView;
    private ModuleSelectionListBox moduleSelectionListBox;

    private ChannelConfigDialogDataModel channelConfigDialogDataModel;
    private RemoveChannelPrototypeModelSelectionListener removeChannelPrototypeModelSelectionListener;

    private Button createNewVersionButton;
    private Button editVersionInfoButton;
    private Button addButton;
    private Button removeButton;
    private Button saveButton;
    private Button closeButton;

    private TabItem inputTabItem;
    private TabItem outputTabItem;

    // @formatter:off
    public ChannelConfigDialog(
            @Nullable final Shell parentShell, 
            @NotNull final ChannelConfigDialogDataModel channelConfigDialogDataModel) {
            //@formatter:on    
        
        super(parentShell);
        setShellStyle(SWT.APPLICATION_MODAL | SWT.TITLE | SWT.BORDER);
        
        this.channelConfigDialogDataModel = channelConfigDialogDataModel;                         
        dirty = new WatchableValue<Boolean>();
        
        dirty.addListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                if (Display.getCurrent() == null) {
                    return;
                }
                final Boolean dirty = (Boolean) evt.getNewValue();
                updateShellTitle(dirty);                
                Display.getCurrent().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        if (getSaveButton() != null) {
                            getSaveButton().setEnabled(dirty);
                        }
                    }
                });
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public IDocumentable getDocumentableObject() {
        return channelConfigDialogDataModel.getPrototypeModule();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSavebuttonEnabled(@Nullable final String event, final boolean enabled) {
        dirty.setValue(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSaveButtonSaved() {
        dirty.setValue(true);
    }

    private void buildDocumetView(@Nonnull final TabItem item) {
        final String head = Messages.ChannelConfigDialog_Documents;
        item.setText(head);
        documentationManageView = new DocumentationManageView(ioTabFolder, SWT.NONE, this);
        item.setControl(documentationManageView);
        ioTabFolder.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
                docTabSelectionAction(e);
            }

            @Override
            public void widgetSelected(@Nonnull final SelectionEvent e) {
                docTabSelectionAction(e);
            }

            private void docTabSelectionAction(@Nonnull final SelectionEvent e) {
                if (e.item.equals(item)) {
                    documentationManageView.onActivate();
                }
            }
        });
    }

    private void buildInfo(@Nonnull final Composite infoDialogArea) {
        infoAreaComponent = new InfoAreaComponent(infoDialogArea, channelConfigDialogDataModel.getSlaveCfgDataList());
        infoAreaComponent.buildComponent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void createButtonsForButtonBar(@Nonnull final Composite parent) {

        ((GridLayout) parent.getLayout()).numColumns = 2;
        ((GridData) parent.getLayoutData()).horizontalAlignment = SWT.FILL;

        final Composite outerComposite = new GridCompositeFactory().parent(parent).column(2).build();

        final Composite farLeft = new Composite(outerComposite, SWT.NONE);
        farLeft.setLayout(new MigLayout("inset 0", "[][]", "[]"));

        final Composite left = new GridCompositeFactory().parent(outerComposite).marginLeft(60).build();

        createNewVersionButton = new Button(farLeft, SWT.PUSH);
        createNewVersionButton.setText("New Version");
        createNewVersionButton.setEnabled(true);

        createNewVersionButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createNewVersionOnCLick();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        editVersionInfoButton = new Button(farLeft, SWT.PUSH);
        editVersionInfoButton.setText("Edit Version Info");
        editVersionInfoButton.setEnabled(false);

        editVersionInfoButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                editVersionInfoOnClick();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        addButton = createButton(left, IDialogConstants.NEXT_ID, Messages.ChannelConfigDialog_Add, false);

        //@formatter:off
        addButton.addSelectionListener(new AddChannelPrototypeModelSelectionListener(
                this, 
                channelConfigDialogDataModel,
                outputTable,
                inputTable));
                //@formatter:on

        removeButton = createButton(left, IDialogConstants.BACK_ID, Messages.ChannelConfigDialog_Remove, false);

        //@formatter:off        
        removeChannelPrototypeModelSelectionListener = new RemoveChannelPrototypeModelSelectionListener(
                this,
                channelConfigDialogDataModel,
                outputTable, 
                inputTable);
                //@formatter:on

        removeButton.addSelectionListener(removeChannelPrototypeModelSelectionListener);

        SelectionListener setDirtySelectionListenr = new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dirty.setValue(true);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }

        };

        addButton.addSelectionListener(setDirtySelectionListenr);
        removeButton.addSelectionListener(setDirtySelectionListenr);

        updateGuiStateFromDataModel();

        // Button right side
        //@formatter:off
        final Composite right = new GridCompositeFactory()
            .parent(parent)
            .horizontalAlignment(GridData.HORIZONTAL_ALIGN_END)
            .build();
            //@formatter:on

        saveButton = createButton(right, SAVE_BUTON_ID, "Save", false);
        saveButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                executeSave();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        closeButton = createButton(right, IDialogConstants.CLOSE_ID, "Close", false);
        closeButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (dirty.getValue()) {
                    askForSave();
                }                
                ChannelConfigDialog.this.close();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        dirty.setValue(false);

    }

    private GSDModuleDBO newlyCreatedVersionedPrototype;
    
    private void createNewVersionOnCLick() {

        newlyCreatedVersionedPrototype = null;
        
        //@formatter:off
        final PrototypeVersionDialog newVersionDialog = new PrototypeVersionDialog(
                Display.getCurrent().getActiveShell(),
                channelConfigDialogDataModel.getCurrentModuleNumber());
                //@formatter:on

        if (newVersionDialog.open() == Dialog.OK) {

            if (!newVersionDialog.getModuleVersionInfo().isPresent()) {
                throw new IllegalStateException("No VersionInfo available.");
            }
            
            Runnable runInNewThread = new Runnable() {
                @Override
                public void run() {
                    try {                        
                        //@formatter:off
                        newlyCreatedVersionedPrototype = channelConfigDialogDataModel.createNewVersion(
                                newVersionDialog.getModuleVersionInfo().get());
                                //@formatter:on
                        channelConfigDialogDataModel.refreshFromRepository();
                    } catch (PersistenceException e) {
                        e.printStackTrace();
                    }
                }
            };

            Runnable runInUiThread = new Runnable() {
                @Override
                public void run() {
                    moduleSelectionListBox.refresh();
                    if (newlyCreatedVersionedPrototype != null) {
                        moduleSelectionListBox.select(newlyCreatedVersionedPrototype.getModuleNumber());
                        newlyCreatedVersionedPrototype = null;
                    }
                }
            };

            LongRunningOperation.run(runInNewThread, Optional.of(runInUiThread));

        }
        
    }
    
    private void editVersionInfoOnClick() {
        
        ModuleVersionInfo moduleVersionInfo = channelConfigDialogDataModel.getModuleVersionInfo();
        
         //@formatter:off
        final PrototypeVersionDialog editVersionDialog = new PrototypeVersionDialog(
                Display.getCurrent().getActiveShell(),
                moduleVersionInfo);
                //@formatter:on

        if (editVersionDialog.open() == Dialog.OK) {

            final Optional<ModuleVersionInfo> updatedModuleVersionInfo = editVersionDialog.getModuleVersionInfo();
            
            if (!editVersionDialog.getModuleVersionInfo().isPresent()) {
                throw new IllegalStateException("No VersionInfo available.");
            }

            channelConfigDialogDataModel.updateModuleVersionInfo(updatedModuleVersionInfo.get());
            moduleSelectionListBox.refresh();
           
            dirty.setValue(true);

        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected final Control createDialogArea(@Nonnull final Composite parent) {

        if (!(parent.getLayout() instanceof GridLayout)) {
            throw new IllegalStateException("Expecting GridLayout but got " + parent.getLayout().getClass());
        }

        GridData gridDataParent = (GridData) parent.getLayoutData();
        gridDataParent.heightHint = 600;
        gridDataParent.widthHint = 1200;
        gridDataParent.grabExcessHorizontalSpace = true;
        gridDataParent.grabExcessVerticalSpace = false;

        final Composite gridComposite = new Composite(parent, SWT.NONE);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridComposite.setLayout(new GridLayout(3, false));
        gridComposite.setLayoutData(gridData);

        try {
            buildModuleTypList(parent, gridComposite);
        } catch (PersistenceException e) {
            e.printStackTrace();
            return null;
        }

        final Composite dialogAreaComposite = (Composite) super.createDialogArea(gridComposite);
        buildInfo(dialogAreaComposite);

        ioTabFolder = new TabFolder(dialogAreaComposite, SWT.TOP);
        ioTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        inputTabItem = new TabItem(ioTabFolder, SWT.NONE);
        inputTabItem.setText(Messages.ChannelConfigDialog_Input);
        inputTable = createChannelTable(ioTabFolder, channelConfigDialogDataModel.getInputChannelPrototypeModelList());
        inputTable.assignToTablItem(inputTabItem);

        outputTabItem = new TabItem(ioTabFolder, SWT.NONE);
        outputTabItem.setText(Messages.ChannelConfigDialog_Output);
        outputTable = createChannelTable(ioTabFolder, channelConfigDialogDataModel.getOutputChannelPrototypeModelList());
        outputTable.assignToTablItem(outputTabItem);

        buildDocumetView(new TabItem(ioTabFolder, SWT.NONE));
        parent.layout();

        updateGuiStateFromDataModel();

        return dialogAreaComposite;
    }

    //@formatter:off
    private void buildModuleTypList(
            @Nonnull final Composite comp,
            @Nonnull final Composite topGroup) throws PersistenceException {
            //@formatter:on

        final Composite gridComposite = new Composite(topGroup, SWT.NONE);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        layoutData.minimumWidth = 340;
        gridComposite.setLayoutData(layoutData);
        gridComposite.setLayout(new GridLayout(2, false));

        //@formatter:off
        moduleSelectionListBox = new ModuleSelectionListBox(
                gridComposite, 
                channelConfigDialogDataModel.getModulelist(),
                channelConfigDialogDataModel.getParsedModuleInfo(),
                ModuleNumber.moduleNumber(channelConfigDialogDataModel.getCurrentModuleNumber().getValue()));
                //@formatter:on

        moduleSelectionListBox.buildComponent();

        moduleSelectionListBox.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {

                if (dirty.getValue()) {
                    askForSave();
                }

                if (!(event.getSelection() instanceof IStructuredSelection)) {
                    throw new IllegalStateException("selection must be IStrucutredSeleciton");
                }

                if (inputTable == null) {
                    throw new IllegalStateException("inputTable must not be null");
                }

                if (outputTable == null) {
                    throw new IllegalStateException("outputTable must not be null");
                }

                if (channelConfigDialogDataModel == null) {
                    throw new IllegalStateException("channelConfigDialogDataModel must not be null");
                }

                Optional<ModuleNumber> selectedModuleNumber = getSelectedModuleNumber(moduleSelectionListBox, event);

                if (selectedModuleNumber.isPresent()) {
                    ModuleNumber moduleNumber = selectedModuleNumber.get();
                    createNewVersionButton.setEnabled(!moduleNumber.isVersioned());
                    editVersionInfoButton.setEnabled(moduleNumber.isVersioned());
                } else {
                    createNewVersionButton.setEnabled(false);
                    editVersionInfoButton.setEnabled(false);
                }

                channelConfigDialogDataModel.refresh(selectedModuleNumber);
                inputTable.setData(channelConfigDialogDataModel.getInputChannelPrototypeModelList());
                outputTable.setData(channelConfigDialogDataModel.getOutputChannelPrototypeModelList());

                infoAreaComponent.refresh(channelConfigDialogDataModel.getSlaveCfgDataList());
                documentationManageView.refresh();
                
                updateGuiStateFromDataModel();

            }

        });

        moduleSelectionListBox.selectFirstRow();

    }

    private Optional<ModuleNumber> getSelectedModuleNumber(ModuleSelectionListBox moduleSelectionListBox,
            SelectionChangedEvent event) {

        IStructuredSelection structuredSelection = (IStructuredSelection) event.getSelection();
        GSDModuleDBO selectedModule = (GSDModuleDBO) (structuredSelection.getFirstElement());

        if (selectedModule != null) {
            return ModuleNumber.moduleNumber(selectedModule.getModuleId());
        }

        // no module selected => select firsts row
        moduleSelectionListBox.selectFirstRow();
        structuredSelection = moduleSelectionListBox.getStructuredSelection();
        selectedModule = (GSDModuleDBO) (structuredSelection.getFirstElement());

        if (selectedModule != null) {
            return ModuleNumber.moduleNumber(selectedModule.getModuleId());
        }

        // no modules found -> maybe because of filter expression
        return Optional.absent();
    }

    @Nonnull
    //@formatter:off
    private ChannelTableComponent createChannelTable(
            @Nonnull final Composite tableParent,
            @Nullable final ArrayList<ModuleChannelPrototypeDBO> channelPrototypeModelList) {
            //@formatter:on

        ChannelTableComponent channelTableComponent = new ChannelTableComponent(tableParent, channelPrototypeModelList);
        channelTableComponent.buildComponent();

        channelTableComponent.assignPropertChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(@Nonnull final PropertyChangeEvent event) {
                dirty.setValue(true);
            }
        });

        return channelTableComponent;

    }

    private void updateGuiStateFromDataModel() {

        if (addButton != null) {
            addButton.setEnabled(channelConfigDialogDataModel.hasData());
        }

        if (removeButton != null) {
            removeButton.setEnabled(channelConfigDialogDataModel.hasData());
        }

        if (inputTabItem != null) {
            inputTabItem.getControl().setEnabled(channelConfigDialogDataModel.isHasInputFields());
        }

        if (outputTabItem != null) {
            outputTabItem.getControl().setEnabled(channelConfigDialogDataModel.isHasOutputFields());
        }

        updateShellTitle(dirty.getValue());

    }
    
    private void executeSave() {
        inputTable.closeAllCellEditors();
        outputTable.closeAllCellEditors();
        removeChannelPrototypeModelSelectionListener.executeRemove();
        channelConfigDialogDataModel.getPrototypeModule().setDocuments(documentationManageView.getDocuments());
        try {
            channelConfigDialogDataModel.save();
            dirty.setValue(false);
        } catch (final PersistenceException e) {
            e.printStackTrace();
            DeviceDatabaseErrorDialog.open(null, "The Settings were not saved!\n\nDataBase Failure:", e);
        }
    }

    private void askForSave() {
        MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_QUESTION | SWT.YES
                | SWT.NO);
        messageBox.setMessage("There are unsaved changes. Save Prototype?");
        messageBox.setText("Save Protype");
        int response = messageBox.open();
        if (response == SWT.YES) {
            executeSave();
        } else {
            inputTable.closeAllCellEditors();
            outputTable.closeAllCellEditors();
            try {
                removeChannelPrototypeModelSelectionListener.cancelRemove();
                channelConfigDialogDataModel.undo();
                moduleSelectionListBox.refresh();
            } catch (PersistenceException e) {
                DeviceDatabaseErrorDialog.open(null, "Undo not possible!\n\nDataBase Failure:", e);
            }
        }
        dirty.setValue(false);
    }

    private void updateShellTitle(final Boolean isDirty) {
        Display.getCurrent().asyncExec(new Runnable() {
            @Override
            public void run() {
                ModuleLabel moduleLabel = channelConfigDialogDataModel.getModuleLabel();
                String title =  Messages.ChannelConfigDialog_Module + moduleLabel.buildLabelWithoutModuleNumber();
                if (getShell() != null) {
                    if ((isDirty != null) && isDirty) {
                        getShell().setText(title + "*");
                    } else {
                        getShell().setText(title);
                    }
                }
            }
        });
    }

    @Nonnull
    private Button getSaveButton() {
        return getButton(SAVE_BUTON_ID);
    }

    public boolean isInputTabSelected() {
        return ioTabFolder.getSelection()[0].getText().equals(Messages.ChannelConfigDialog_Input);
    }
}
