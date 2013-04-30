package org.csstudio.config.ioconfigurator.view;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfigurator.actions.ControllerActionCache;
import org.csstudio.config.ioconfigurator.ldap.LdapControllerService;
import org.csstudio.config.ioconfigurator.tree.model.IControllerNode;
import org.csstudio.config.ioconfigurator.tree.model.IControllerSubtreeNode;
import org.csstudio.config.ioconfigurator.tree.model.impl.ControllerSubtreeNode;
import org.csstudio.config.ioconfigurator.ui.ControllerTreeViewer;
import org.csstudio.utility.ldap.model.LdapEpicsControlsConfiguration;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.ViewPart;

/**
 * This plug-in main view.
 *
 * @author tslamic
 * @author $Author: tslamic $
 * @version $Revision: 1.1 $
 * @since 01.09.2010
 */
public class ConfiguratorView extends ViewPart {

    /** This class ID */
    public static final String ID = "org.csstudio.config.ioconfigurator.ioconfiguratorView";

    /*
     * Root node.
     *
     * Instantiated in the constructor but populated
     * from the LDAP server asynchronously when createPartControl
     * method is invoked.
     */
    private final IControllerSubtreeNode _root;

    /*
     * TreeViewer shown in this View.
     *
     * Instantiated asynchronously just after the
     * root node has been populated.
     */
    private TreeViewer _viewer;

    /*
     * Contains the actions for this View.
     * Created when the root node is populated.
     */
    private ControllerActionCache _actionsCache;

    /**
     * Constructor.
     */
    public ConfiguratorView() {
        _root = new ControllerSubtreeNode(LdapEpicsControlsConfiguration.ROOT.getRootTypeValue(),
                                          null,
                                          LdapEpicsControlsConfiguration.ROOT);
    }

    @Override
    public void createPartControl(@Nonnull final Composite parent) {
        parent.setLayout(new FillLayout());

        /*
         * TODO: the following label is shown until the
         *       proper tree view is not created. I believe
         *       it could be done better.
         */
        final Label temporaryLabel = new Label(parent, SWT.CENTER);
        temporaryLabel.setText("Pending...");

        createView(parent, temporaryLabel);
    }

    /**
     * Returns the root node of this view.
     * @return the root node of this view.
     */
    public IControllerSubtreeNode getRoot() {
        return _root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        _viewer.getControl().setFocus();
    }

    /*
     * Asynchronously populates this class root and
     * builds the TreeViewer.
     */
    private void createView(@Nonnull final Composite parent,
                            @Nonnull final Label pendingMessage) {

        getSite().getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                try {
                    LdapControllerService.loadContent(_root);
                    _viewer = ControllerTreeViewer.getViewer(parent,
                                                             _root,
                                                             getSite());
                    _actionsCache = new ControllerActionCache(_root,
                                                              _viewer,
                                                              getSite());
                    initializeContextMenu();
                    pendingMessage.dispose();
                    parent.layout(true);
                } catch (CreateContentModelException e) {
                    MessageDialog.openError(getSite().getShell(),
                                            "Error",
                                            "Could not load the content from LDAP.");
                    closeView();
                }
            }
        });
    }

    /*
     * Create the Menu
     */
    private void initializeContextMenu() {
        MenuManager menuManager = new MenuManager("#PopupMenu");
        menuManager.setRemoveAllWhenShown(true);

        menuManager.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(@Nullable final IMenuManager manager) {
                _actionsCache.fillContextMenu(getSelectedNode(), manager);
            }
        });

        Tree viewerTree = _viewer.getTree();
        Menu contextMenu = menuManager.createContextMenu(viewerTree);
        viewerTree.setMenu(contextMenu);

        // register the context menu for extension by other plug-ins
        getSite().registerContextMenu(menuManager, _viewer);
    }

    /*
     * Returns the IControllerNode currently selected in the
     * TreeViewer.
     */
    private IControllerNode getSelectedNode() {
        IStructuredSelection selection = (IStructuredSelection) _viewer
                .getSelection();
        return (IControllerNode) selection.getFirstElement();
    }

    /*
     * Closes this view.
     */
    private void closeView() {
        // Get Active Page
        IWorkbenchPage activePage = getSite().getWorkbenchWindow()
                .getActivePage();
        assert activePage != null;

        // Find this View
        IViewPart view = activePage.findView(ID);
        assert view != null;

        // Close the view
        activePage.hideView(view);
    }
}
