package org.csstudio.config.ioconfig.config.view.dialog.prototype.components;

import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.config.component.IComponent;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveCfgData;
import org.csstudio.config.ioconfig.view.internal.localization.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class InfoAreaComponent implements IComponent {

    private final Composite infoDialogArea;
    private final List<SlaveCfgData> slaveCfgDataList;

    private boolean hasInputFields;
    private boolean hasOutputFields;
    private boolean isWordSize = true;

    public InfoAreaComponent(Composite infoDialogArea, List<SlaveCfgData> slaveCfgDataList) {
        super();
        this.infoDialogArea = infoDialogArea;
        this.slaveCfgDataList = slaveCfgDataList;
    }

    public boolean isHasInputFields() {
        return hasInputFields;
    }

    public boolean isHasOutputFields() {
        return hasOutputFields;
    }

    public boolean isWordSize() {
        return isWordSize;
    }

    @Override
    public void buildComponent() {
        final int size = 12;
        final int leftUperCorner = 0;

        final Composite info = new Composite(infoDialogArea, SWT.NONE);
        info.setLayout(new GridLayout(4, true));
        info.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        final TabFolder tabFolder = new TabFolder(info, SWT.TOP);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));

        hasInputFields = false;
        hasOutputFields = false;

        for (final SlaveCfgData slaveCfgData : slaveCfgDataList) {
            final TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
            tabItem.setText("Module " + slaveCfgData.getParameterAsHexString());
            final Composite box = new Composite(tabFolder, SWT.NONE);
            box.setLayout(new GridLayout(4, true));
            box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

            String dataFormat;
            if (slaveCfgData.isWordSize()) {
                isWordSize &= true;
                dataFormat = "Word's: "; //$NON-NLS-1$
            } else {
                isWordSize &= false;
                dataFormat = "Byte's: "; //$NON-NLS-1$
            }

            new Label(box, SWT.NONE)
                    .setText(Messages.ChannelConfigDialog_Count + dataFormat + slaveCfgData.getNumber());
            hasInputFields = hasInputFields || slaveCfgData.isInput();

            new Label(box, SWT.NONE).setText(Messages.ChannelConfigDialog_Input_ + slaveCfgData.isInput());
            hasOutputFields = hasOutputFields || slaveCfgData.isOutput();

            new Label(box, SWT.NONE).setText(Messages.ChannelConfigDialog_Output_ + slaveCfgData.isOutput());
            new Label(box, SWT.NONE).setText(Messages.ChannelConfigDialog_Parameter_
                    + slaveCfgData.getParameterAsHexString());

            createGraphicalDataStructurePresentation(size, leftUperCorner, slaveCfgData, box);
            tabItem.setControl(box);
        }

    }
    
    private static final class PaintListenerImplementation implements PaintListener {
        private final int _leftUperCorner;
        private final int _size;
        private final SlaveCfgData _slaveCfgData;

        PaintListenerImplementation(@Nonnull final SlaveCfgData slaveCfgData, final int leftUperCorner, final int size) {
            _slaveCfgData = slaveCfgData;
            _leftUperCorner = leftUperCorner;
            _size = size;
        }

        @Override
        public void paintControl(@Nonnull final PaintEvent e) {
            final int x0 = 0;
            final int x1 = _size * _slaveCfgData.getWordSize();
            e.gc.drawRectangle(x0, _leftUperCorner, x1, _size);
            e.gc.drawRectangle(x0, _leftUperCorner + _size, x1, _size);
            final String type = Messages.ChannelConfigDialog_AD;
            final Point stringExtent = e.gc.stringExtent(type);
            e.gc.drawString(type, (x1 - stringExtent.x) / 2, _leftUperCorner, true);
            for (int j = 1; j <= _slaveCfgData.getWordSize(); j++) {
                final int x2 = x0 + j * _size;
                e.gc.drawLine(x2, _leftUperCorner + _size, x2, _leftUperCorner + 2 * _size);
            }
        }
    }
    
    public void createGraphicalDataStructurePresentation(final int size, final int leftUperCorner,
            @Nonnull final SlaveCfgData slaveCfgData, @Nonnull final Composite box) {
        for (int i = 0; i < slaveCfgData.getNumber(); i++) {
            final Canvas canvas = new Canvas(box, SWT.NONE);
            final int horizSpan = slaveCfgData.getWordSize() / 8;
            final GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false, horizSpan, 1);
            gridData.widthHint = size * slaveCfgData.getWordSize() + 15;
            gridData.heightHint = 2 * size + 5;
            canvas.setLayoutData(gridData);
            final PaintListenerImplementation listener;
            listener = new PaintListenerImplementation(slaveCfgData, leftUperCorner, size);
            canvas.addPaintListener(listener);
        }
    }
}
