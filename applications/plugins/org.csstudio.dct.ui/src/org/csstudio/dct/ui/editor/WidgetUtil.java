package org.csstudio.dct.ui.editor;

import org.csstudio.dct.ui.editor.tables.ColumnConfig;
import org.csstudio.dct.ui.editor.tables.ConvenienceTableWrapper;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Utility class that helps to create standard widgets used in the editing forms
 * of CSS-DCT.
 * 
 * @author Sven Wende
 * 
 */
public final class WidgetUtil {
    /**
     * Private constructor which prevents instantiation of this class.
     */
    private WidgetUtil() {
    }

    /**
     * Creates and returns a new standard table with 3 columns (for key, value,
     * error).
     * 
     * @param parent
     *            the parent composite
     * @param commandStack
     *            a command stack
     * @return a new standard table with 3 columns (for key, value, error)
     */
    public static ConvenienceTableWrapper create3ColumnTable(Composite parent, CommandStack commandStack) {
        ColumnConfig[] cc = new ColumnConfig[3];
        cc[0] = new ColumnConfig("KEY", "Description", 200);
        cc[1] = new ColumnConfig("VALUE", "Value", 300);
        cc[2] = new ColumnConfig("ERROR", "Error", 300);
        return new ConvenienceTableWrapper(parent, SWT.NONE, commandStack, cc);
    }

    public static ConvenienceTableWrapper create3ColumnTableWithDescription(Composite parent, CommandStack commandStack) {
        ColumnConfig[] cc = new ColumnConfig[3];
        cc[0] = new ColumnConfig("KEY", "Name", 170);
        cc[1] = new ColumnConfig("VALUE", "Value", 200);
        cc[2] = new ColumnConfig("Description", "Description", 450);
        return new ConvenienceTableWrapper(parent, SWT.NONE, commandStack, cc);
    }

    public static ConvenienceTableWrapper create4ColumnTable(Composite parent, CommandStack commandStack) {
        ColumnConfig[] cc = new ColumnConfig[4];
        cc[0] = new ColumnConfig("KEY", "Description", 200);
        cc[1] = new ColumnConfig("VALUE", "Value", 250);
        cc[2] = new ColumnConfig("CHECK", "Archived", 70, true);
        cc[3] = new ColumnConfig("ERROR", "Error", 250);
        return new ConvenienceTableWrapper(parent, SWT.NONE, commandStack, cc);
    }
}
