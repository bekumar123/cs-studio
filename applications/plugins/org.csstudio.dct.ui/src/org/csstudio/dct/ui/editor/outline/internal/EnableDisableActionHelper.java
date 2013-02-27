package org.csstudio.dct.ui.editor.outline.internal;

import java.util.List;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.commands.ChangeBeanPropertyCommand;
import org.csstudio.dct.model.internal.Instance;
import org.csstudio.dct.util.CompareUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

public class EnableDisableActionHelper {

    public static Command createEnableDisableCommand(List<IElement> selection, boolean value) {
        if (CompareUtil.containsOnly(IRecord.class, selection)) {
            CompoundCommand chain = new CompoundCommand();
            for (IElement singleSelection: selection) {
                IRecord record = (IRecord) singleSelection;
                Command command = new ChangeBeanPropertyCommand(record, "disabled", value);
                chain.add(command);
            }
            return chain;
        } else {
            if (selection.get(0) instanceof Instance) {
                Instance instance = (Instance) selection.get(0);
                CompoundCommand chain = new CompoundCommand();
                for (IRecord record : instance.getAllRecordsInHierarchy()) {
                    Command command = new ChangeBeanPropertyCommand(record, "disabled", value);
                    chain.add(command);
                }
                return chain;
            }
        }
        return null;
    }

    public static boolean isValidSelectionForEnable(List<IElement> selection) {
        return EnableDisableActionHelper.isValidSelection(selection, false);
    }

    public static boolean isValidSelectionForDisable(List<IElement> selection) {
        return EnableDisableActionHelper.isValidSelection(selection, true);
    }

    private static boolean isValidSelection(List<IElement> selection, boolean expectedValue) {
        if (selection.size() == 0) {
            return false;
        }
        if (CompareUtil.containsInstancesFolder(selection)) {
            return false;
        }
        if (CompareUtil.containsLibraryFolder(selection)) {
            return false;
        }
        if (CompareUtil.containsPrototypesFolder(selection)) {
            return false;
        }
        
        boolean isChildOfAllowedFolders = CompareUtil.childOfInstancesFolder(selection) || 
                CompareUtil.childOfPrototypesFolder(selection) ;
        
        if (selection.size() > 1) {
            if (CompareUtil.containsOnly(IRecord.class, selection)) {
                return isChildOfAllowedFolders;
            } else {
                return false;
            }
        }
        
        if (isChildOfAllowedFolders) {
            if (selection.get(0) instanceof IRecord) {
                IRecord record = (IRecord) selection.get(0);
                if (record.getDisabled() == null) {
                    return expectedValue == true;
                }
                return ((!record.getDisabled().booleanValue()) == expectedValue);
            }
        }
        return isChildOfAllowedFolders && selection.get(0) instanceof IInstance;
    }
}
