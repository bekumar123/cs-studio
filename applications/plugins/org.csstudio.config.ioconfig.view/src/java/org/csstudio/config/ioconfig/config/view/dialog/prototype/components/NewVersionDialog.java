package org.csstudio.config.ioconfig.config.view.dialog.prototype.components;

import net.miginfocom.swt.MigLayout;

import org.csstudio.config.ioconfig.config.component.GridCompositeFactory;
import org.csstudio.config.ioconfig.config.component.GridLayoutDataFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NewVersionDialog extends Dialog {

    private String label;
    private String note;
    private Text textLabel;
    private Text textNote;

    public NewVersionDialog(final Shell parentShell, final String label, final String note) {
        super(parentShell);
        this.label = label;
        this.note = note;
    }

    protected Control createDialogArea(final Composite parent) {
             
        Composite content = (Composite) super.createDialogArea(parent);
        content.setLayout(new FillLayout());
        
        Composite container = new Composite(content, SWT.NONE);
        container.setLayout(new MigLayout("","[][grow,fill]","[][grow,fill]"));
        
        Label labelLabel = new Label(container, SWT.NONE);
        labelLabel.setText("Label:");
        labelLabel.setLayoutData("ay top");
         
        textLabel = new Text(container, SWT.BORDER | SWT.WRAP);
        textLabel.setText(label);
        textLabel.setLayoutData("growx, wrap, ay top, h 25!");
  
        Label labelNote = new Label(container, SWT.NONE);
        labelNote.setText("Note:");
        labelNote.setLayoutData("ay top, growy, growx");
  
        textNote = new Text(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        textNote.setText(note);
        textNote.setLayoutData("ay top, growy, growx");
        
        return parent;
    }

    protected void createButtonsForButtonBar(final Composite parent) {
        Display.getCurrent().asyncExec(new Runnable() {         
            @Override
            public void run() {
                getShell().setText("Create new version");
            }
        });
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    protected Point getInitialSize() {
        return new Point(500, 375);
    }
    
    protected void okPressed() {
        this.note = textNote.getText();
        super.okPressed();
    }
    
    public String getNote() {
        return note;
    }
}