package org.csstudio.config.ioconfig.config.view.dialog.prototype.components;

import net.miginfocom.swt.MigLayout;

import org.csstudio.config.ioconfig.model.types.ModuleNumber;
import org.csstudio.config.ioconfig.model.types.ModuleVersionInfo;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class PrototypeVersionDialog extends Dialog {

    private ModuleNumber moduleNumber;
    private Optional<ModuleVersionInfo> moduleVersionInfo;
    private Text textTag;
    private Text textNote;
    private String title;
    
    private Button okButton;

    public PrototypeVersionDialog(final Shell parentShell, final ModuleNumber moduleNumber) {
        
        super(parentShell);
        
        Preconditions.checkArgument(!moduleNumber.isVersioned(), "ModuleNumber must not be versioned");
        
        title = "Create new version";
        this.moduleVersionInfo = Optional.absent();
        this.moduleNumber = moduleNumber;
    }

    public PrototypeVersionDialog(final Shell parentShell, final ModuleVersionInfo moduleVersionInfo) {
        
        super(parentShell);
        
        Preconditions.checkArgument(moduleVersionInfo.getModuleNumber().isVersioned(), "ModuleNumber must be versioned");
        Preconditions.checkNotNull(moduleVersionInfo.getVersionTag(), "versionTag must not be null");
        
        title = "Edit version info";
        this.moduleNumber = moduleVersionInfo.getModuleNumber();
        this.moduleVersionInfo = Optional.of(moduleVersionInfo);
    }

    protected Control createDialogArea(final Composite parent) {

        Composite content = (Composite) super.createDialogArea(parent);
        content.setLayout(new FillLayout());

        Composite container = new Composite(content, SWT.NONE);
        container.setLayout(new MigLayout("", "[][grow,fill]", "[][grow,fill]"));

        Label labelTag = new Label(container, SWT.NONE);
        labelTag.setText("Tag:");
        labelTag.setLayoutData("ay top");

        textTag = new Text(container, SWT.BORDER);
        textTag.setLayoutData("growx, wrap, ay top, h 23!");
        textTag.setTextLimit(25);

        textTag.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                okButton.setEnabled(!Strings.isNullOrEmpty(textTag.getText()));
            }
        });

        Label labelNote = new Label(container, SWT.NONE);
        labelNote.setText("Note:");
        labelNote.setLayoutData("ay top, growy, growx");

        textNote = new Text(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        textNote.setLayoutData("ay top, growy, growx");

        return parent;
    }

    protected void createButtonsForButtonBar(final Composite parent) {
        
        Display.getCurrent().asyncExec(new Runnable() {
            @Override
            public void run() {
                getShell().setText(title);
            }
        });

        okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        okButton.setEnabled(!Strings.isNullOrEmpty(textTag.getText()));
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);

        if (moduleVersionInfo.isPresent()) {
            textTag.setText(moduleVersionInfo.get().getVersionTag().getValue());
            textNote.setText(Strings.nullToEmpty(moduleVersionInfo.get().getVersionNote().getValue()));
        }

    }

    protected Point getInitialSize() {
        return new Point(500, 375);
    }

    protected void okPressed() {
        moduleVersionInfo = Optional.of(ModuleVersionInfo.buildNewVersion(moduleNumber, textTag.getText(), textNote.getText()));
        super.okPressed();
    }

    public Optional<ModuleVersionInfo> getModuleVersionInfo() {
        return moduleVersionInfo;
    }

}