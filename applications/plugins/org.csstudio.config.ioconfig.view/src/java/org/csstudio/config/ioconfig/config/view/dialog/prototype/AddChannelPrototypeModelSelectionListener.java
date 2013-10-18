package org.csstudio.config.ioconfig.config.view.dialog.prototype;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.config.component.IRefreshable;
import org.csstudio.config.ioconfig.config.view.dialog.prototype.components.ChannelConfigDialogDataModel;
import org.csstudio.config.ioconfig.editorparts.AbstractNodeEditor;
import org.csstudio.config.ioconfig.model.pbmodel.DataType;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

final class AddChannelPrototypeModelSelectionListener implements SelectionListener {
    
    private final static boolean IS_INPUT = true;
    
    private final ChannelConfigDialog channelConfigDialog;
    private final ChannelConfigDialogDataModel channelConfigDialogDataModel;
    private final IRefreshable outputTable;
    private final IRefreshable inputTable;

    //@formatter:off
    public AddChannelPrototypeModelSelectionListener(
            @Nonnull final ChannelConfigDialog channelConfigDialog,
            @Nonnull ChannelConfigDialogDataModel channelConfigDialogDataModel,
            @Nonnull final IRefreshable outputTable, 
            @Nonnull final IRefreshable inputTable) {
            //@formatter:on
        this.channelConfigDialog = channelConfigDialog;
        this.channelConfigDialogDataModel = channelConfigDialogDataModel;
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
        DataType type;
        if (channelConfigDialogDataModel.isWordSize()) {
            type = DataType.UINT16;
        } else {
            type = DataType.UINT8;
        }
        final ModuleChannelPrototypeDBO moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        final String user = AbstractNodeEditor.getUserName();
        final Date date = new Date();
        moduleChannelPrototype.setCreationData(user, date);
        moduleChannelPrototype.setName(""); //$NON-NLS-1$
        moduleChannelPrototype.setGSDModule(channelConfigDialogDataModel.getPrototypeModule());
        if (channelConfigDialog.isInputSelected()) {
            //@formatter:off
            addRow(
                    type, 
                    IS_INPUT, 
                    moduleChannelPrototype, 
                    channelConfigDialogDataModel.getInputChannelPrototypeModelList(),
                    inputTable);
                    //@formatter:on
        } else {
            //@formatter:off
            addRow(
                    type, 
                    !IS_INPUT, 
                    moduleChannelPrototype, 
                    channelConfigDialogDataModel.getOutputChannelPrototypeModelList(),
                    outputTable);
                    //@formatter:on
        }
    }

    protected void addRow (
            @Nonnull final DataType type,
            @Nonnull final boolean input,
            @Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype,
            @Nonnull final ArrayList<ModuleChannelPrototypeDBO> data,
            @Nonnull final IRefreshable uiComponent) {
        
        int offset = 0;
        DataType tmpType = type;
        ModuleChannelPrototypeDBO lastModuleChannelPrototypeModel;
        if (!data.isEmpty()) {
            lastModuleChannelPrototypeModel = data.get(data.size() - 1);
            offset = lastModuleChannelPrototypeModel.getOffset();
            offset += lastModuleChannelPrototypeModel.getSize();
            tmpType = lastModuleChannelPrototypeModel.getType();
        }
        moduleChannelPrototype.setOffset(offset);
        moduleChannelPrototype.setType(tmpType);
        moduleChannelPrototype.setInput(input);
        moduleChannelPrototype.setGSDModule( channelConfigDialogDataModel.getPrototypeModule());
        channelConfigDialogDataModel.getPrototypeModule().addModuleChannelPrototype(moduleChannelPrototype);
        data.add(moduleChannelPrototype);
        inputTable.refresh();
    }
    
}
