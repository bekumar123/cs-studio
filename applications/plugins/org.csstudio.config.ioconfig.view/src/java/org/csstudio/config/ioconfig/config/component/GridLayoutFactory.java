package org.csstudio.config.ioconfig.config.component;

import org.eclipse.swt.layout.GridLayout;

class GridLayoutFactory {

    private int numColumns = 0;
    private int marginLeft = 0;

    public GridLayoutFactory column(int columns) {
        this.numColumns = columns;
        return this;
    }

    public GridLayoutFactory marginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
        return this;
    }

    public GridLayout build() {
        GridLayout gridLayout = new GridLayout(numColumns, true);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginLeft = marginLeft;
        gridLayout.numColumns = numColumns;
        return gridLayout;
    }

}
