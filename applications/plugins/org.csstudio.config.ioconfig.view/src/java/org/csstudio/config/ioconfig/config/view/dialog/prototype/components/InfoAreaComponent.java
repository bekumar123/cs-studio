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

    private List<SlaveCfgData> slaveCfgDataList;

    private Composite info;   
    private TabFolder tabFolder;
    
    public InfoAreaComponent(Composite infoDialogArea, List<SlaveCfgData> slaveCfgDataList) {
        super();
        this.infoDialogArea = infoDialogArea;
        this.slaveCfgDataList = slaveCfgDataList;
    }

    public Composite getInfoDialogArea() {
        return infoDialogArea;
    }
    
    @Override
    public void buildComponent() {

        info = new Composite(infoDialogArea, SWT.NONE);
        info.setLayout(new GridLayout(4, true));
        info.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        refresh(slaveCfgDataList);
    }
    
    public void refresh(List<SlaveCfgData> slaveCfgDataList) {

        this.slaveCfgDataList = slaveCfgDataList;
          
        if (tabFolder != null) {
            tabFolder.dispose();
        }

        tabFolder = new TabFolder(info, SWT.TOP);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
        
        for (final SlaveCfgData slaveCfgData : slaveCfgDataList) {
            
            final TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
            tabItem.setText("Module " + slaveCfgData.getParameterAsHexString());
            final Composite box = new Composite(tabFolder, SWT.NONE);
            box.setLayout(new GridLayout(4, true));
            box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

            String dataFormat;
            if (slaveCfgData.isWordSize()) {
                dataFormat = "Word's: "; //$NON-NLS-1$
            } else {
                dataFormat = "Byte's: "; //$NON-NLS-1$
            }

            new Label(box, SWT.NONE)
                    .setText(Messages.ChannelConfigDialog_Count + dataFormat + slaveCfgData.getNumber());

            new Label(box, SWT.NONE).setText(Messages.ChannelConfigDialog_Input_ + slaveCfgData.isInput());

            new Label(box, SWT.NONE).setText(Messages.ChannelConfigDialog_Output_ + slaveCfgData.isOutput());
            new Label(box, SWT.NONE).setText(Messages.ChannelConfigDialog_Parameter_
                    + slaveCfgData.getParameterAsHexString());

            tabItem.setControl(box);
        }
        
        info.layout();
        infoDialogArea.layout();
    }
    
}
