package org.csstudio.common.trendplotter;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/** 
 *  Dialog to select Pv that will be deleted from plot in shell.
 *  
 *  @author jhatje
 */
public class RemovePvDialog  extends TitleAreaDialog
{
    /** 
     * Pv names on plot that could be deleted
     */
    final private String[] _pvNames;
    
    private Combo _pvNameList;

    private String _pvToDelete;
    
    public RemovePvDialog(final Shell shell, final String pvNames[]) {
        super(shell);
        _pvNames = pvNames;
        setShellStyle(getShellStyle() | SWT.RESIZE);
        setHelpAvailable(false);
    }
    
    /** @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell) */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("PV Dialog");
    }
    
    /** @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite) */
    @Override
    protected Control createDialogArea(final Composite parent_widget) {
        final Composite parent_composite = (Composite) super.createDialogArea(parent_widget);

        // Title & Image
        setTitle("Delete PVs");
        setMessage("Delete selected PV from Trendplotter.");
        setTitleImage(Activator.getDefault().getImage("icons/config_image.png")); //$NON-NLS-1$

        // Create box for widgets we're about to add
        final Composite box = new Composite(parent_composite, 0);
        box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        box.setLayout(layout);
        
        if (_pvNames.length > 0) {
            Label l = new Label(box, 0);
            l.setText("PV List");
            l.setLayoutData(new GridData());
    
            _pvNameList = new Combo(box, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.SINGLE);
            _pvNameList.setToolTipText(Messages.AddPV_AxisTT);
            _pvNameList.setLayoutData(new GridData(SWT.FILL, 0, true, false));
            // First entry is 'new axis', rest actual axis names
            _pvNameList.add("");
            for (String name : _pvNames)
                _pvNameList.add(name);
            _pvNameList.select(0);

            // Empty label to fill last column
            l = new Label(box, 0);
            l.setLayoutData(new GridData());
        }
        
        return parent_composite;
    }
    
    /** Save user values
     *  @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed() {
        _pvToDelete = _pvNameList.getItem(_pvNameList.getSelectionIndex());
        super.okPressed();
    }

    /** @return Entered PV name */
    public String getNameToDelete() {
        return _pvToDelete;
    }
}
