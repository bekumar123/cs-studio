package org.csstudio.config.ioconfig.editorparts;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.component.IModuleNumberProvider;
import org.csstudio.config.ioconfig.config.component.ModifiedCallback;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel2;
import org.csstudio.config.ioconfig.model.types.ModuleNumber;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * 
 * If the selection changes the old Channels will be deleted and the new Channel
 * created for the new Module. Have the Module no Prototype the Dialog to
 * generate Prototype is opened.
 * 
 */
public final class ModuleEditorSelectionChangedListenerForModuleTypeList implements ISelectionChangedListener {

    protected static final Logger LOG = LoggerFactory
            .getLogger(ModuleEditorSelectionChangedListenerForModuleTypeList.class);

    private final ModuleEditor moduleEditor;
    private final Group topGroup;
    private final IModuleNumberProvider dataProvider;
    private final Optional<ModifiedCallback> moduleUpdatedCallback;
    
    //@formatter:off
    ModuleEditorSelectionChangedListenerForModuleTypeList(
            @Nonnull final ModuleEditor moduleEditor,
            @Nonnull final Group topGroup, 
            @Nonnull final IModuleNumberProvider dataProvider,
            @Nonnull final Optional<ModifiedCallback> moduleUpdatedCallback) {
            //@formatter:on
        
        Preconditions.checkNotNull(moduleEditor, "moduleEditor must not be null");
        Preconditions.checkNotNull(topGroup, "topGroup must not be null");
        Preconditions.checkNotNull(dataProvider, "datProvider must not be null");
        
        this.moduleEditor = moduleEditor;
        this.topGroup = topGroup;
        this.dataProvider = dataProvider;
        this.moduleUpdatedCallback = moduleUpdatedCallback;
    }

    @Override
    public void selectionChanged(@Nonnull final SelectionChangedEvent event) {

        if (!(event.getSelection() instanceof IStructuredSelection)) {
            throw new IllegalStateException("selection must be IStrucutredSeleciton");
        }
        
        IStructuredSelection structuredSelection = (IStructuredSelection)event.getSelection();
        final GsdModuleModel2 selectedModule = (GsdModuleModel2)(structuredSelection.getFirstElement());
        
        if (ifSameModule(selectedModule)) {
            return;
        }

        final int selectedModuleNo = selectedModule.getModuleNumber();
        final ModuleDBO module = moduleEditor.getNode();
        
        updateSaveButton(selectedModuleNo, dataProvider);
        
        try {

            final String createdBy = AbstractNodeEditor.getUserName();
            GSDModuleDBO gsdModule;

            try {

                module.setNewModel(selectedModuleNo, createdBy);

                gsdModule = module.getGSDModule();
                module.setName(gsdModule.getName());

                if (moduleUpdatedCallback.isPresent()) {
                    moduleUpdatedCallback.get().modified("moduleSelected", true);
                }
                
                final Text nameWidget = moduleEditor.getNameWidget();
                if (nameWidget != null) {
                    nameWidget.setText(gsdModule.getName());
                }

                moduleEditor.updateModulConfigData();

            } catch (final IllegalArgumentException e1) {

                moduleEditor.openErrorDialog(e1, moduleEditor.getProfiBusTreeView());
                LOG.error("No prototype!", e1);

            }

        } catch (final PersistenceException e1) {
            moduleEditor.openErrorDialog(e1, moduleEditor.getProfiBusTreeView());
            LOG.error("Database error!", e1);
        }
                
        try {
            moduleEditor.makeCurrentUserParamData(topGroup);
        } catch (final IOException e) {
            ModuleEditor.LOG.error("File read error!", e);
            DeviceDatabaseErrorDialog.open(null, "File read error!", e);
        }
        
        moduleEditor.getProfiBusTreeView().refresh(module.getParent());
        
    }

    private void updateSaveButton(int selectedModuleNo, IModuleNumberProvider dataProvider2) {
        boolean hasChanged;
        
        Optional<ModuleNumber> savedModuleNo = dataProvider.getModuleNumber();
        if (savedModuleNo.isPresent()) {
            hasChanged = savedModuleNo.get().getValue() != selectedModuleNo;
        } else {
            hasChanged = true;
        }

        moduleEditor.setSavebuttonEnabled("ModuleTyp", hasChanged);

    }

    private boolean ifSameModule(@Nullable final GsdModuleModel2 selectedModule) {
        final ModuleDBO module = moduleEditor.getNode();
        return selectedModule == null || module == null || module.getGSDModule() != null
                && module.getGSDModule().getModuleId() == selectedModule.getModuleNumber();
    }
}
