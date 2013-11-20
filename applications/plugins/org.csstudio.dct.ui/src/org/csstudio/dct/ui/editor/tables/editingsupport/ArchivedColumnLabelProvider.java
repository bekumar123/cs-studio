package org.csstudio.dct.ui.editor.tables.editingsupport;

import org.csstudio.dct.ui.Activator;
import org.csstudio.dct.ui.editor.RecordFieldTableRowAdapter;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

public class ArchivedColumnLabelProvider extends ColumnLabelProvider {

    @Override
    public String getText(Object element) {
        return null;
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof RecordFieldTableRowAdapter) {
            RecordFieldTableRowAdapter adapter = (RecordFieldTableRowAdapter) element;
            if (adapter.isArchivable()) {
                String displayValue = adapter.getEditingValue(2);
                if ((displayValue != null) && displayValue.equals(ArchivedEditingSupport.CHECKED)) {
                    return CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/ok.png");
                } else {
                    return CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/no.png");
                }
            } else {
                return null;
            }
        } else {
            throw new IllegalStateException();
        }
    }

}
