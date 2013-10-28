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
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.component.ModuleSelectionListBox;
import org.csstudio.config.ioconfig.config.component.WatchableValue;
import org.csstudio.config.ioconfig.config.view.IHasDocumentableObject;
import org.csstudio.config.ioconfig.config.view.dialog.prototype.components.ChannelConfigDialogDataModel;
import org.csstudio.config.ioconfig.config.view.dialog.prototype.components.InfoAreaComponent;
import org.csstudio.config.ioconfig.config.view.dialog.prototype.components.table.ChannelTableComponent;
import org.csstudio.config.ioconfig.config.view.helper.DocumentationManageView;
import org.csstudio.config.ioconfig.model.IDocumentable;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel2;
import org.csstudio.config.ioconfig.model.types.ModuleNumber;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.csstudio.config.ioconfig.view.internal.localization.Messages;
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

public final class ChannelConfigDialog extends Dialog implements IHasDocumentableObject, ISelectedTab {

    protected static final Logger LOG = LoggerFactory.getLogger(ChannelConfigDialog.class);

    private final static int SAVE_BUTON_ID = 29123812;

    private WatchableValue<Boolean> dirty;

    private ChannelTableComponent inputTable;
    private ChannelTableComponent outputTable;
    private TabFolder ioTabFolder;
    private InfoAreaComponent infoAreaComponent;
    private DocumentationManageView documentationManageView;

    private ChannelConfigDialogDataModel channelConfigDialogDataModel;
    private RemoveChannelPrototypeModelSelectionListener removeChannelPrototypeModelSelectionListener;

    private Button addButton;
    private Button removeButton;
    private Button saveButton;
    private Button closeButton;

    private TabItem inputTabItem;
    private TabItem outputTabItem;

    // @formatter:on
    public ChannelConfigDialog(@Nullable final Shell parentShell, SlaveDBO selectedSlave) {
        //@formatter:off                
        super(parentShell);        
        channelConfigDialogDataModel = new ChannelConfigDialogDataModel(selectedSlave);        
        setShellStyle(SWT.APPLICATION_MODAL | SWT.TITLE | SWT.BORDER);      
        dirty = new WatchableValue<Boolean>();
        dirty.addListener(new PropertyChangeListener() {            
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                Boolean dirty = (Boolean)evt.getNewValue();
                 updateShellTitle(dirty);
                 getSaveButton().setEnabled(dirty);
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

    @Nonnull
    public GSDModuleDBO getGsdModule() {
        return channelConfigDialogDataModel.getPrototypeModule();
    }

    /**
     *
     */
    public void setEmptyChannelPrototypeName2Unused() {
        final Set<ModuleChannelPrototypeDBO> moduleChannelPrototype;
        moduleChannelPrototype = channelConfigDialogDataModel.getPrototypeModule().getModuleChannelPrototype();
        if (moduleChannelPrototype != null) {
            for (final ModuleChannelPrototypeDBO moduleChannelPrototypeDBO : moduleChannelPrototype) {
                String name = moduleChannelPrototypeDBO.getName();
                if (name == null || name.isEmpty()) {
                    name = "unused"; //$NON-NLS-1$
                    moduleChannelPrototypeDBO.setName(name);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSavebuttonEnabled(@Nullable final String event, final boolean enabled) {
        System.out.println(";;");
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
        infoAreaComponent = new InfoAreaComponent(infoDialogArea,  channelConfigDialogDataModel.getSlaveCfgDataList());     
        infoAreaComponent.buildComponent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void createButtonsForButtonBar(@Nonnull final Composite parent) {
                        
        ((GridLayout) parent.getLayout()).numColumns = 2;
        ((GridData) parent.getLayoutData()).horizontalAlignment = SWT.FILL;
        GridData layoutData;
        GridLayout gridLayout;

        // Button Left side
        final Composite left = new Composite(parent, SWT.NONE);
        layoutData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER);
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.horizontalIndent = 350;
        left.setLayoutData(layoutData);
        gridLayout = new GridLayout(0, true);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        left.setLayout(gridLayout);
        
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

        // Button Left side
        final Composite right = new Composite(parent, SWT.NONE);
        layoutData = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
        right.setLayoutData(layoutData);
        gridLayout = new GridLayout(0, true);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        right.setLayout(gridLayout);

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
        gridDataParent.heightHint = 700;
        gridDataParent.widthHint = 1200;

        final Composite gridComposite = new Composite(parent, SWT.NONE);
        GridData gridData = new GridData(SWT.NONE, SWT.NONE, true, true);
        gridComposite.setLayout(new GridLayout(3, false));
        gridComposite.setLayoutData(gridData);

        buildModuleTypList(parent, gridComposite);

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
            @Nonnull final Composite topGroup) {
            //@formatter:on

        final Composite gridComposite = new Composite(topGroup, SWT.NONE);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        layoutData.minimumWidth = 340;
        gridComposite.setLayoutData(layoutData);
        gridComposite.setLayout(new GridLayout(2, false));

        //@formatter:off
        ModuleSelectionListBox moduleSelectionListBox = new ModuleSelectionListBox(
                gridComposite, 
                channelConfigDialogDataModel.getGsdFileDBO(),
                channelConfigDialogDataModel.getCurrentModuleNumber());
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

                IStructuredSelection structuredSelection = (IStructuredSelection) event.getSelection();
                final GsdModuleModel2 selectedModule = (GsdModuleModel2) (structuredSelection.getFirstElement());

                channelConfigDialogDataModel.refreshDataModel(ModuleNumber.moduleNumber(
                        selectedModule.getModuleNumber()).get());

                inputTable.setData(channelConfigDialogDataModel.getInputChannelPrototypeModelList());
                outputTable.setData(channelConfigDialogDataModel.getOutputChannelPrototypeModelList());

                updateGuiStateFromDataModel();

                Display.getCurrent().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        infoAreaComponent.refresh(channelConfigDialogDataModel.getSlaveCfgDataList());
                    }
                });

            }

        });

        moduleSelectionListBox.selectFirstRow();

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
            addButton.setEnabled(channelConfigDialogDataModel.hasData() || channelConfigDialogDataModel.isNew());
        }

        if (removeButton != null) {
            removeButton.setEnabled(channelConfigDialogDataModel.hasData() || channelConfigDialogDataModel.isNew());
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
        setEmptyChannelPrototypeName2Unused();
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
                if (getShell() != null) {
                    if ((isDirty != null) && isDirty) {
                        getShell().setText(
                                Messages.ChannelConfigDialog_Module + channelConfigDialogDataModel.getModuleName()
                                        + "*");
                    } else {
                        getShell().setText(
                                Messages.ChannelConfigDialog_Module + channelConfigDialogDataModel.getModuleName());
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
