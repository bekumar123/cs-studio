package org.csstudio.dct.ui.editor.outline.internal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.export.internal.AdvancedDbFileExporter;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.commands.GenericCommand;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.CompareUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

public final class ExportToDbFileAction extends AbstractOutlineAction {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command createCommand(final List<IElement> selection) {
        Command command = new GenericCommand(new Runnable() {

            public void run() {

                IInstance instance = (IInstance) selection.get(0);

                final FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
                String fileName;
                if (instance.getName() == null) {
                    fileName = instance.getParent().getName();
                } else {
                    fileName = instance.getName();
                }
                fileName = fileName.replaceAll(" ", "_");
                dialog.setFileName(fileName + ".db");

                final String path = dialog.open();
                AdvancedDbFileExporter advancedDbFileExporter = new AdvancedDbFileExporter();

                if (path != null) {
                    try {
                        File file = new File(path);

                        if (!file.exists()) {
                            file.createNewFile();
                        }

                        if (file.canWrite()) {
                            final FileWriter writer = new FileWriter(file);
                            writer.write(advancedDbFileExporter.export(getListOfRecords(selection)));
                            writer.close();
                        }

                        file = null;
                    } catch (final IOException e) {
                        MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Export not possible",
                                e.getMessage());
                    }
                }

            }
        });
        ;

        return command;
    }

    @Override
    protected void afterSelectionChanged(List<IElement> selection, IAction action) {
        super.afterSelectionChanged(selection, action);

        if (selection.size() <= 0) {
            action.setEnabled(false);
            return;
        }

        boolean containsOnlyInstances = CompareUtil.containsOnly(IInstance.class, selection);
        action.setEnabled(containsOnlyInstances);

    }

    private List<IRecord> getListOfRecords(List<IElement> selection) {
        List<IRecord> allRecords = new ArrayList<IRecord>();
        for (IElement element : selection) {
            if (element instanceof IInstance) {
                IInstance instance = (IInstance) element;
                List<IRecord> records = instance.getAllRecordsInHierarchy();
                for (IRecord record : records) {
                    Boolean disabled = AliasResolutionUtil.getPropertyViaHierarchy(record, "disabled");
                    if (disabled != null && !disabled) {
                        allRecords.add((IRecord) record);
                    }
                }
            }
        }
        return allRecords;
    }
}
