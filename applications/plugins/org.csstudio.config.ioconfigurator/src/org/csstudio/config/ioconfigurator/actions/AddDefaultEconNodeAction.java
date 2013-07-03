package org.csstudio.config.ioconfigurator.actions;

import javax.naming.InvalidNameException;

import org.csstudio.config.ioconfigurator.annotation.Nonnull;
import org.csstudio.config.ioconfigurator.ldap.LdapControllerService;
import org.csstudio.config.ioconfigurator.property.ioc.Validators;
import org.csstudio.config.ioconfigurator.tree.model.IControllerNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPartSite;

import com.google.common.base.Optional;

public class AddDefaultEconNodeAction  extends Action {

    private final IWorkbenchPartSite _site;
    private final ReloadFromLdapAction _reloadLdap;
    final TreeViewer _viewer;

    // Obtained through getters/setters
    private IControllerNode _node;

    /**
     * Private constructor. Instance available through the static factory
     * method.
     * 
     * @param viewer
     *            {@code TreeViewer} plug-in tree viewer.
     * @param site
     *            {@code IWorkbenchPartSite} site of the plug-in view.
     */
    private AddDefaultEconNodeAction(final TreeViewer viewer, @Nonnull final IWorkbenchPartSite site,
            ReloadFromLdapAction reloadLdap) {
        _viewer = viewer;
        _site = site;
        _reloadLdap = reloadLdap;
    }

    public static AddDefaultEconNodeAction getAction(final TreeViewer viewer, @Nonnull final IWorkbenchPartSite site,
            final ReloadFromLdapAction reloadLdap) {
        return new AddDefaultEconNodeAction(viewer, site, reloadLdap);
    }

    public AddDefaultEconNodeAction setNode(final IControllerNode node) {
        _node = node;
        return this;
    }

    public IControllerNode getNode() {
        return _node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Add new IOC";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText() {
        return "Add new IOC";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getToolTipText() {
        return "Add new IOC";
    }

    public void run() {
        final Optional<String> newName = newNameInputDialog(_site);
        if (newName.isPresent()) {
            try {
                LdapControllerService.addNewIOC(_node.getLdapName(), newName.get());
                _reloadLdap.run(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            _viewer.expandToLevel(_node.getLdapName().getRdns().size() + 1);
                        } catch (InvalidNameException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                MessageDialog.openError(_site.getShell(), "Error", e.getMessage());
            }

        }
    }

    private static Optional<String> newNameInputDialog(@Nonnull final IWorkbenchPartSite site) {
        final InputDialog dialog = new InputDialog(site.getShell(), "Enter new IOC Name", "Please enter the new IOC name", "",
                Validators.UNIQUE_IOC_VALIDATOR.getValidator());
        if (Window.OK == dialog.open()) {
            return Optional.of(dialog.getValue());
        }
        return Optional.absent();
    }
}
