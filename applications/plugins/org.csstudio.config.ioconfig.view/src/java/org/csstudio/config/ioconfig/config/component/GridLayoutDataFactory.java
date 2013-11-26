package org.csstudio.config.ioconfig.config.component;

import org.eclipse.swt.layout.GridData;

public class GridLayoutDataFactory {

    private int horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    private int verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
    
    public GridLayoutDataFactory horizontalAlignment(int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        return this;
    }

    public GridLayoutDataFactory verticalAlignment(int verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
        return this;
    }
    
    public GridData build() {
        GridData layoutData = new GridData(horizontalAlignment, verticalAlignment);
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        return layoutData;
    }
}
