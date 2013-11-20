package org.csstudio.dct.ui.editor.tables.editingsupport;

import org.csstudio.dct.ui.editor.tables.ITableRow;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

/**
 * Label provider implementation.
 * 
 * @author Sven Wende
 */
public final class LabelProvider extends ColumnLabelProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final ViewerCell cell) {
        ITableRow row = (ITableRow) cell.getElement();

        int index = cell.getColumnIndex();

        // set the text
        cell.setText(getText(row, index));

        // image
        Image img = row.getImage(index);

        if (img != null) {
            cell.setImage(img);
        }

        // background color
        RGB bgColor = row.getBackgroundColor(index);

        if (bgColor != null) {
            cell.setBackground(CustomMediaFactory.getInstance().getColor(bgColor));
        }

        // foreground color
        RGB fgColor = row.getForegroundColor(index);

        if (fgColor != null) {
            cell.setForeground(CustomMediaFactory.getInstance().getColor(fgColor));
        }

        // font
        Font font = row.getFont(index);

        if (font != null) {
            cell.setFont(font);
        }
    }

    /**
     * Returns the text to display.
     * 
     * @param element
     *            the current element
     * @param column
     *            the current column index
     * @return The text to display in the viewer
     */
    private String getText(final Object element, final int column) {
        ITableRow row = (ITableRow) element;
        String result = row.getDisplayValue(column);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText(final Object element) {
        ITableRow row = (ITableRow) element;
        return row.getTooltip();
    }

    /**
     * {@inheritDoc}
     */
    public Point getToolTipShift(final Object object) {
        return new Point(5, 5);
    }

    /**
     * {@inheritDoc}
     */
    public int getToolTipDisplayDelayTime(final Object object) {
        return 100;
    }

    /**
     * {@inheritDoc}
     */
    public int getToolTipTimeDisplayed(final Object object) {
        return 10000;
    }

}