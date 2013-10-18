package org.csstudio.config.ioconfig.config.view.dialog.prototype;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.config.component.IRefreshable;
import org.csstudio.config.ioconfig.config.component.ISelectableAndRefreshable;
import org.csstudio.config.ioconfig.config.view.dialog.prototype.components.ChannelConfigDialogDataModel;
import org.csstudio.config.ioconfig.model.DBClass;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.csstudio.config.ioconfig.view.internal.localization.Messages;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TabFolder;

final class RemoveChannelPrototypeModelSelectionListener implements SelectionListener {

    private final TabFolder rslIoTabFolder;
    final ChannelConfigDialogDataModel channelConfigDialogDataModel;
    private final ISelectableAndRefreshable inputTable;
    private final ISelectableAndRefreshable outputTable;

    //@formatter:off
    public RemoveChannelPrototypeModelSelectionListener(
            @Nonnull final TabFolder ioTabFolder,
            @Nonnull final ChannelConfigDialogDataModel channelConfigDialogDataModel,
            @Nonnull final ISelectableAndRefreshable outputTable, 
            @Nonnull final ISelectableAndRefreshable inputTable) {
            //@formatter:on
        this.rslIoTabFolder = ioTabFolder;
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

    //@formatter:off
    private void remove(
            @Nonnull final ISelectableAndRefreshable tableViewer,
            @Nonnull final ArrayList<ModuleChannelPrototypeDBO> channelPrototypeModelList,
            @Nonnull final GSDModuleDBO protypeModule) {
            //@formatter:on
        IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        if (selection.isEmpty()) {
            final ModuleChannelPrototypeDBO remove = channelPrototypeModelList.remove(channelPrototypeModelList.size() - 1);
            removeNode(remove);            
        } else {
            @SuppressWarnings("unchecked")
            final List<ModuleChannelPrototypeDBO> list = selection.toList();
            channelPrototypeModelList.removeAll(list);
            protypeModule.removeModuleChannelPrototype(list);
            for (final Object object : list) {
                if (object instanceof DBClass) {
                    final DBClass dbClass = (DBClass) object;
                    removeNode(dbClass);
                }
            }
        }
        tableViewer.refresh();
    }

    private void removeItem(GSDModuleDBO protypeModule) {
        if (rslIoTabFolder.getSelection()[0].getText().equals(Messages.ChannelConfigDialog_Input)) {
            remove(inputTable, channelConfigDialogDataModel.getInputChannelPrototypeModelList(), protypeModule);
        } else {
            remove(outputTable, channelConfigDialogDataModel.getOutputChannelPrototypeModelList(), protypeModule);
        }
    }

    /**
     * @param node
     */
    private void removeNode(@Nonnull final DBClass node) {
        try {
            Repository.removeNode(node);
        } catch (final PersistenceException e) {
            DeviceDatabaseErrorDialog.open(null, Messages.ChannelConfigDialog_CantRemove, e);
            ChannelConfigDialog.LOG.error(Messages.ChannelConfigDialog_CantRemove, e);
        }
    }
}
