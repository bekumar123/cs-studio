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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.component.ModuleSelectionListBox;
import org.csstudio.config.ioconfig.config.view.IHasDocumentableObject;
import org.csstudio.config.ioconfig.config.view.dialog.prototype.components.InfoAreaComponent;
import org.csstudio.config.ioconfig.config.view.dialog.prototype.components.table.ChannelTableComponent;
import org.csstudio.config.ioconfig.config.view.helper.DocumentationManageView;
import org.csstudio.config.ioconfig.model.IDocumentable;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveCfgDataBuilder;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel2;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.ParsedGsdFileModel;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

public class ChannelConfigDialog extends Dialog implements IHasDocumentableObject {

    protected static final Logger LOG = LoggerFactory.getLogger(ChannelConfigDialog.class);
    private static int _DIRTY;

    private ChannelTableComponent inputTable;
    private ChannelTableComponent outputTable;
    private TabFolder ioTabFolder;
    private InfoAreaComponent infoAreaComponent;    
    private DocumentationManageView _documentationManageView;

    private ArrayList<ModuleChannelPrototypeDBO> inputChannelPrototypeModelList;
    private ArrayList<ModuleChannelPrototypeDBO> outputChannelPrototypeModelList;
    private GsdModuleModel2 moduleModel;
    private GSDModuleDBO gsdModule;
    private ParsedGsdFileModel parsedGsdFileModel;
    private Map<Integer, GsdModuleModel2> moduleMap;
    private List<Integer> moduleNumbers;

    // @formatter:on
    public ChannelConfigDialog(@Nullable final Shell parentShell, ParsedGsdFileModel parsedGsdFileModel) {
        //@formatter:off
                
        super(parentShell);

        this.parsedGsdFileModel = parsedGsdFileModel;
        this.moduleMap = parsedGsdFileModel.getModuleMap();
        
        setShellStyle(SWT.APPLICATION_MODAL | SWT.CLOSE | SWT.MAX | SWT.TITLE | SWT.BORDER | SWT.RESIZE);

        moduleNumbers = new ArrayList<Integer>();        
        moduleNumbers.addAll(moduleMap.keySet());
     
        updateChannelData(ModuleNumber.moduleNumber(moduleNumbers.get(0)));
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public IDocumentable getDocumentableObject() {
        return gsdModule;
    }

    @Nonnull
    public GSDModuleDBO getGsdModule() {
        return gsdModule;
    }

    /**
     *
     */
    public void setEmptyChannelPrototypeName2Unused() {
        final Set<ModuleChannelPrototypeDBO> moduleChannelPrototype;
        moduleChannelPrototype = gsdModule.getModuleChannelPrototype();
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
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSaveButtonSaved() {
        // nothing to do
    }

    private void buildDocumetView(@Nonnull final TabItem item) {
        final String head = Messages.ChannelConfigDialog_Documents;
        item.setText(head);
        _documentationManageView = new DocumentationManageView(ioTabFolder, SWT.NONE, this);
        item.setControl(_documentationManageView);
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
                    _documentationManageView.onActivate();
                }
            }

        });
    }

    private void buildInfo(@Nonnull final Composite infoDialogArea) {        
        infoAreaComponent = new InfoAreaComponent(infoDialogArea, new SlaveCfgDataBuilder(moduleModel.getValue()).getSlaveCfgDataList());     
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
        
        final Button addButton = createButton(left, IDialogConstants.NEXT_ID, Messages.ChannelConfigDialog_Add, false);
        
        //@formatter:off
        addButton.addSelectionListener(new AddChannelPrototypeModelSelectionListener(
                this, 
                gsdModule,
                outputChannelPrototypeModelList, 
                outputTable,
                inputChannelPrototypeModelList,
                inputTable));
                //@formatter:on
        
        final Button removeButton = createButton(left, IDialogConstants.BACK_ID, Messages.ChannelConfigDialog_Remove,
                false);
        final RemoveChannelPrototypeModelSelectionListener rsListener;
       
        //@formatter:off
        rsListener = new RemoveChannelPrototypeModelSelectionListener(
                gsdModule, 
                outputChannelPrototypeModelList,
                outputTable, 
                inputChannelPrototypeModelList, 
                inputTable, 
                ioTabFolder);
                //@formatter:on
        
        removeButton.addSelectionListener(rsListener);

        addButton.setEnabled(infoAreaComponent.isHasInputFields() || infoAreaComponent.isHasOutputFields());
        removeButton.setEnabled(addButton.isEnabled());
        
        // Button Left side
        final Composite right = new Composite(parent, SWT.NONE);
        layoutData = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
        right.setLayoutData(layoutData);
        gridLayout = new GridLayout(0, true);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        right.setLayout(gridLayout);
        super.createButtonsForButtonBar(right);
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
        
        GridData gridDataParent = (GridData)parent.getLayoutData();
        gridDataParent.heightHint = 700;
        gridDataParent.widthHint = 1200;
        
        getShell().setText(Messages.ChannelConfigDialog_Module + moduleModel.getName());
        
        final Composite gridComposite = new Composite(parent, SWT.NONE);
        GridData gridData = new GridData(SWT.NONE , SWT.NONE, true, true);
        gridComposite.setLayout(new GridLayout(3, false));
        gridComposite.setLayoutData(gridData);
        
        buildModuleTypList(parent, gridComposite);

        final Composite dialogAreaComposite = (Composite) super.createDialogArea(gridComposite);
        buildInfo(dialogAreaComposite);
                        
        ioTabFolder = new TabFolder(dialogAreaComposite, SWT.TOP);
        ioTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        if (infoAreaComponent.isHasInputFields()) {                        
            final TabItem inputTabItem = new TabItem(ioTabFolder, SWT.NONE);
            inputTabItem.setText(Messages.ChannelConfigDialog_Input);
            inputTable = createChannelTable(ioTabFolder, inputChannelPrototypeModelList);
            inputTable.assignToTablItem(inputTabItem);
        }
        
        if (infoAreaComponent.isHasOutputFields()) {            
            final TabItem outputTabItem = new TabItem(ioTabFolder, SWT.NONE);
            outputTabItem.setText(Messages.ChannelConfigDialog_Output);
            outputTable = createChannelTable(ioTabFolder, outputChannelPrototypeModelList);
            outputTable.assignToTablItem(outputTabItem);
        }

        buildDocumetView(new TabItem(ioTabFolder, SWT.NONE));
        parent.layout();
        
        
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

        ModuleSelectionListBox moduleSelectionListBox = new ModuleSelectionListBox(gridComposite, parsedGsdFileModel.getGsdFileDBO(),
                ModuleNumber.moduleNumberAbsent());

        moduleSelectionListBox.buildComponent();
                        
        moduleSelectionListBox.addSelectionChangedListener(new ISelectionChangedListener() {
            
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                
                if (!(event.getSelection() instanceof IStructuredSelection)) {
                    throw new IllegalStateException("selection must be IStrucutredSeleciton");
                }
                
                IStructuredSelection structuredSelection = (IStructuredSelection)event.getSelection();
                final GsdModuleModel2 selectedModule = (GsdModuleModel2)(structuredSelection.getFirstElement());
                
                updateChannelData(ModuleNumber.moduleNumber(406));
                
                inputTable.refresh();
                
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void okPressed() {
        inputTable.closeAllCellEditors();
        outputTable.closeAllCellEditors();
        setEmptyChannelPrototypeName2Unused();
        gsdModule.setDocuments(_documentationManageView.getDocuments());
        try {
            gsdModule.save();
        } catch (final PersistenceException e) {
            e.printStackTrace();
            DeviceDatabaseErrorDialog.open(null, "The Settings not saved!\n\nDataBase Failure:", e);
        }
        super.okPressed();
    }

    @Nonnull
    //@formatter:off
    private static ChannelTableComponent createChannelTable(
            @Nonnull final Composite tableParent,
            @Nullable final ArrayList<ModuleChannelPrototypeDBO> channelPrototypeModelList) {
            //@formatter:on
        
        System.out.println(channelPrototypeModelList.size());
        
        ChannelTableComponent channelTableComponent = new ChannelTableComponent(tableParent, channelPrototypeModelList);
        channelTableComponent.buildComponent();
        
        channelTableComponent.assignPropertChangeListener(new IPropertyChangeListener() {
            
            @Override
            public void propertyChange(@Nonnull final PropertyChangeEvent event) {
                final String oldValue = (String) event.getOldValue();
                final String newValue = (String) event.getNewValue();
                if (isNoOldValueAndNewValue(oldValue, newValue)) {
                    ChannelConfigDialog.dirtyPlus();
                } else if (isOldValueAndNoNewValue(oldValue, newValue)) {
                    ChannelConfigDialog.dirtyMinus();
                }
            }
            
            private boolean isOldValueAndNoNewValue(@Nonnull final String oldValue, @Nonnull final String newValue) {
                return oldValue != null && oldValue.length() > 0 && (newValue == null || newValue.length() < 1);
            }

            private boolean isNoOldValueAndNewValue(@CheckForNull final String oldValue,
                    @CheckForNull final String newValue) {
                return (oldValue == null || oldValue.length() == 0) && newValue != null && newValue.length() > 0;
            }
        });
        
        return channelTableComponent;

    }

    private void updateChannelData(Optional<ModuleNumber> moduleNumber1) {
        
        Integer moduleNumber =  406;
        
        inputChannelPrototypeModelList = new ArrayList<ModuleChannelPrototypeDBO>();
        outputChannelPrototypeModelList = new ArrayList<ModuleChannelPrototypeDBO>();

        GsdModuleModel2 mm2 = moduleMap.get(moduleNumber);
        
        moduleModel = mm2;
        gsdModule = new GSDModuleDBO(mm2.getName());
        gsdModule.setModuleId(moduleNumber);
        gsdModule.setGSDFile(parsedGsdFileModel.getGsdFileDBO());
        
        System.out.println(moduleModel);
        
        for (final ModuleChannelPrototypeDBO moduleChannelPrototype : gsdModule.getModuleChannelPrototypeNH()) {
            System.out.println(moduleChannelPrototype);
            if (moduleChannelPrototype.isInput()) {
                inputChannelPrototypeModelList.add(moduleChannelPrototype);
            } else {
                outputChannelPrototypeModelList.add(moduleChannelPrototype);
            }
        }

    }
    
    protected static void dirtyMinus() {
        ChannelConfigDialog._DIRTY++;
    }

    protected static void dirtyPlus() {
        ChannelConfigDialog._DIRTY--;
    }

    @Nonnull
    protected Button getOkButton() {
        return getButton(IDialogConstants.OK_ID);
    }

    public boolean isWord() {
        return infoAreaComponent.isWordSize();
    }

    public boolean isInputSelected() {
        return ioTabFolder.getSelection()[0].getText().equals(Messages.ChannelConfigDialog_Input);
    }
}
