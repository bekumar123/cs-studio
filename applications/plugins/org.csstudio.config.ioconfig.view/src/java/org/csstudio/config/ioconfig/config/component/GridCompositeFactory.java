package org.csstudio.config.ioconfig.config.component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class GridCompositeFactory {

    private Composite parent;

    private int numColumns = 0;
    private int marginLeft = 0;
    private int horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    private int verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
    private boolean assignLayoutData = false;

    public GridCompositeFactory parent(Composite parent) {
        this.parent = parent;
        return this;
    }

    public GridCompositeFactory horizontalAlignment(int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        return this;
    }

    public GridCompositeFactory verticalAlignment(int verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
        return this;
    }

    public GridCompositeFactory column(int columns) {
        this.numColumns = columns;
        return this;
    }

    public GridCompositeFactory marginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
        return this;
    }

    public GridCompositeFactory assignLayoutData() {
        assignLayoutData = true;
        return this;
    }

    public Composite build() {
        final Composite composite = new Composite(parent, SWT.NONE);

        if (assignLayoutData) {
            GridData layoutData = new GridData(horizontalAlignment | verticalAlignment);
            layoutData.grabExcessHorizontalSpace = true;
            layoutData.grabExcessVerticalSpace = true;
            layoutData.horizontalIndent = 0;
            composite.setLayoutData(layoutData);
        }
        
        GridLayout gridLayout = new GridLayoutFactory().column(numColumns).marginLeft(marginLeft).build();
        composite.setLayout(gridLayout);

        return composite;
    }

}
