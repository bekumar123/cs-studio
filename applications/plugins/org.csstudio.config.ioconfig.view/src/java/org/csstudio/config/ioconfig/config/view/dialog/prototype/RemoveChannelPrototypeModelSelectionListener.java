package org.csstudio.config.ioconfig.config.view.dialog.prototype;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.config.component.ISelectableAndRefreshable;
import org.csstudio.config.ioconfig.config.view.dialog.prototype.datamodel.IChannelDataModel;
import org.csstudio.config.ioconfig.model.DBClass;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.csstudio.config.ioconfig.view.internal.localization.Messages;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

final class RemoveChannelPrototypeModelSelectionListener implements SelectionListener {

    private final ISelectedTab parentDialog;
    private final IChannelDataModel channelConfigDialogDataModel;
    private final ISelectableAndRefreshable inputTable;
    private final ISelectableAndRefreshable outputTable;
    private final List<DBClass> removedNodes = new ArrayList<DBClass>();

    //@formatter:off
    public RemoveChannelPrototypeModelSelectionListener(
            @Nonnull final ISelectedTab parentDialog,
            @Nonnull final IChannelDataModel channelConfigDialogDataModel,
            @Nonnull final ISelectableAndRefreshable outputTable, 
            @Nonnull final ISelectableAndRefreshable inputTable) {
            //@formatter:on
        this.parentDialog = parentDialog;
        this.channelConfigDialogDataModel = channelConfigDialogDataModel;
        this.inputTable = inputTable;
        this.outputTable = outputTable;
    }

    @Override
    public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
        removeItem(channelConfigDialogDataModel.getPrototypeModule());
    }

    @Override
    public void widgetSelected(@Nonnull final SelectionEvent e) {
        removeItem(channelConfigDialogDataModel.getPrototypeModule());
    }

    public void executeRemove() {
        for (DBClass node : removedNodes) {
            try {
                channelConfigDialogDataModel.removeModuleChannelPrototype((ModuleChannelPrototypeDBO)node);
                Repository.removeNode(node);
            } catch (final PersistenceException e) {
                DeviceDatabaseErrorDialog.open(null, Messages.ChannelConfigDialog_CantRemove, e);
                ChannelConfigDialog.LOG.error(Messages.ChannelConfigDialog_CantRemove, e);
            }
        }
        removedNodes.clear();
    }

    public void cancelRemove() {
        removedNodes.clear();
    }

    //@formatter:off
    private void remove(
            @Nonnull final ISelectableAndRefreshable uiComponent,
            @Nonnull final ArrayList<ModuleChannelPrototypeDBO> channelPrototypeModelList,
            @Nonnull final GSDModuleDBO prototypeModule) {
            //@formatter:on
        if (channelPrototypeModelList.size() == 0) {
            return;
        }
        if (parentDialog.isInputTabSelected()) {
            if (!channelConfigDialogDataModel.isHasInputFields()) {
                return;
            }
        } else {
            if (!channelConfigDialogDataModel.isHasOutputFields()) {
                return;
            }
        }
        IStructuredSelection selection = (IStructuredSelection) uiComponent.getSelection();
        if (selection.isEmpty()) {
            final ModuleChannelPrototypeDBO remove = channelPrototypeModelList
                    .remove(channelPrototypeModelList.size() - 1);
            removeNode(remove);
        } else {
            @SuppressWarnings("unchecked")
            final List<ModuleChannelPrototypeDBO> list = selection.toList();
            channelPrototypeModelList.removeAll(list);
            for (final Object object : list) {
                if (object instanceof DBClass) {
                    final DBClass dbClass = (DBClass) object;
                    removeNode(dbClass);
                }
            }
        }
        uiComponent.refresh();
    }

    private void removeItem(GSDModuleDBO prototypeModule) {
        if (parentDialog.isInputTabSelected()) {
            remove(inputTable, channelConfigDialogDataModel.getInputChannelPrototypeModelList(), prototypeModule);
        } else {
            remove(outputTable, channelConfigDialogDataModel.getOutputChannelPrototypeModelList(), prototypeModule);
        }
    }

    private void removeNode(@Nonnull final DBClass node) {
        removedNodes.add(node);
    }

}
