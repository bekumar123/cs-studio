package org.csstudio.config.ioconfig.view.treeview;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.tools.NodeMap;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class InfoDialog extends Dialog {

    public InfoDialog(@Nonnull final Shell parentShell) {
        super(parentShell);
    }

    @Override
    @Nonnull
    protected Control createDialogArea(@Nonnull final Composite parent) {
        final Composite createDialogArea = (Composite) super.createDialogArea(parent);
        createDialogArea.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        createDialogArea.setLayout(GridLayoutFactory.swtDefaults().equalWidth(true).numColumns(3).create());
        Label label = new Label(createDialogArea, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        label.setText("Nodes: " + NodeMap.getNumberOfNodes());

        label = new Label(createDialogArea, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

        label = new Label(createDialogArea, SWT.NONE);

        label = new Label(createDialogArea, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        label.setText("Assemble: " + NodeMap.getCountAssembleEpicsAddressString());

        label = new Label(createDialogArea, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        label.setText("LocalUpdate: " + NodeMap.getLocalUpdate());

        label = new Label(createDialogArea, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        label.setText("ChannelConfig: " + NodeMap.getChannelConfigComposite());

        final Text text = new Text(createDialogArea, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));

        label = new Label(createDialogArea, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        createDialogArea.pack();
        return createDialogArea;
    }
    
}
