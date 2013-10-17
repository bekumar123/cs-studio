package org.csstudio.config.ioconfig.config.view.dialog.prototype;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.config.component.IRefreshable;
import org.csstudio.config.ioconfig.editorparts.AbstractNodeEditor;
import org.csstudio.config.ioconfig.model.pbmodel.DataType;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

final class AddChannelPrototypeModelSelectionListener implements SelectionListener {
    
    private final ChannelConfigDialog channelConfigDialog;
    private final ArrayList<ModuleChannelPrototypeDBO> outChannelPrototypeModelList;
    private final ArrayList<ModuleChannelPrototypeDBO> inChannelPrototypeModelList;
    private final GSDModuleDBO gsdMod;
    private final IRefreshable outputTable;
    private final IRefreshable inputTable;

    //@formatter:off
    public AddChannelPrototypeModelSelectionListener(
            @Nonnull final ChannelConfigDialog channelConfigDialog,
            @Nonnull final GSDModuleDBO gsdModule, 
            @Nonnull final ArrayList<ModuleChannelPrototypeDBO> outputList,
            @Nonnull final IRefreshable outputTable, 
            @Nonnull final ArrayList<ModuleChannelPrototypeDBO> inputList,
            @Nonnull final IRefreshable inputTable) {
            //@formatter:on
        this.channelConfigDialog = channelConfigDialog;
        this.gsdMod = gsdModule;
        this.outChannelPrototypeModelList = outputList;
        this.inChannelPrototypeModelList = inputList;
        this.outputTable = outputTable;
        this.inputTable = inputTable;
    }

    @Override
    public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
        addItem();
    }

    @Override
    public void widgetSelected(@Nonnull final SelectionEvent e) {
        addItem();
    }

    private void addItem() {
        final Button button = channelConfigDialog.getOkButton();
        button.setEnabled(true);
        DataType type;
        if (channelConfigDialog.isWord()) {
            type = DataType.UINT16;
        } else {
            type = DataType.UINT8;
        }
        final ModuleChannelPrototypeDBO moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        final String user = AbstractNodeEditor.getUserName();
        final Date date = new Date();
        moduleChannelPrototype.setCreationData(user, date);
        moduleChannelPrototype.setName(""); //$NON-NLS-1$

        moduleChannelPrototype.setGSDModule(gsdMod);
        if (channelConfigDialog.isInputSelected()) {
            add2InputTab(type, moduleChannelPrototype);
        } else {
            add2OutputTab(type, moduleChannelPrototype);
        }
    }

    /**
     * @param type
     * @param moduleChannelPrototype
     */
    protected void add2InputTab(@Nonnull final DataType type,
            @Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype) {
        int offset = 0;
        DataType tmpType = type;
        ModuleChannelPrototypeDBO lastModuleChannelPrototypeModel;
        if (!inChannelPrototypeModelList.isEmpty()) {
            lastModuleChannelPrototypeModel = inChannelPrototypeModelList.get(inChannelPrototypeModelList.size() - 1);
            offset = lastModuleChannelPrototypeModel.getOffset();
            offset += lastModuleChannelPrototypeModel.getSize();
            tmpType = lastModuleChannelPrototypeModel.getType();
        }
        moduleChannelPrototype.setOffset(offset);
        moduleChannelPrototype.setType(tmpType);
        moduleChannelPrototype.setInput(true);
        moduleChannelPrototype.setGSDModule(gsdMod);
        gsdMod.addModuleChannelPrototype(moduleChannelPrototype);
        inChannelPrototypeModelList.add(moduleChannelPrototype);
        inputTable.refresh();
    }

    /**
     * @param type
     * @param moduleChannelPrototype
     */
    protected void add2OutputTab(@Nonnull final DataType type,
            @Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype) {
        int offset = 0;
        DataType tmpType = type;
        ModuleChannelPrototypeDBO lastModuleChannelPrototypeModel;
        if (!outChannelPrototypeModelList.isEmpty()) {
            lastModuleChannelPrototypeModel = outChannelPrototypeModelList
                    .get(outChannelPrototypeModelList.size() - 1);
            offset = lastModuleChannelPrototypeModel.getOffset();
            offset += lastModuleChannelPrototypeModel.getSize();
            tmpType = lastModuleChannelPrototypeModel.getType();
        }
        moduleChannelPrototype.setOffset(offset);
        moduleChannelPrototype.setType(tmpType);
        moduleChannelPrototype.setInput(false);
        gsdMod.addModuleChannelPrototype(moduleChannelPrototype);
        outChannelPrototypeModelList.add(moduleChannelPrototype);
        outputTable.refresh();
    }

}
