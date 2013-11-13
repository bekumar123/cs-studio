/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
/*
 * $Id: ProfiBusTreeView.java,v 1.26 2010/08/20 13:33:03 hrickens Exp $
 */
package org.csstudio.config.ioconfig.view;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.commands.CallEditor;
import org.csstudio.config.ioconfig.commands.CallNewChildrenNodeEditor;
import org.csstudio.config.ioconfig.commands.CallNewFacilityEditor;
import org.csstudio.config.ioconfig.commands.CallNewSiblingNodeEditor;
import org.csstudio.config.ioconfig.config.view.dialog.prototype.ChannelConfigDialog;
import org.csstudio.config.ioconfig.config.view.helper.ProfibusHelper;
import org.csstudio.config.ioconfig.editorparts.AbstractNodeEditor;
import org.csstudio.config.ioconfig.model.AbstractNodeSharedImpl;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.IOConfigActivator;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.view.actions.CreateStatisticAction;
import org.csstudio.config.ioconfig.view.actions.CreateWinModAction;
import org.csstudio.config.ioconfig.view.actions.CreateXMLConfigAction;
import org.csstudio.config.ioconfig.view.actions.DeleteNodeAction;
import org.csstudio.config.ioconfig.view.actions.PasteNodeAction;
import org.csstudio.config.ioconfig.view.actions.RenameNodeAction;
import org.csstudio.config.ioconfig.view.searchview.SearchDialog;
import org.csstudio.config.ioconfig.view.treeview.DBLoaderJob;
import org.csstudio.config.ioconfig.view.treeview.HibernateDBPreferenceChangeListener;
import org.csstudio.config.ioconfig.view.treeview.ILoader;
import org.csstudio.config.ioconfig.view.treeview.InfoDialog;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.DrillDownAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @since 19.06.2007
 */
public class ProfiBusTreeView extends Composite implements ILoader {

    /**
     * The ID of the View.
     */
    public static final String ID = ProfiBusTreeView.class.getName();
    public static final String PARENT_NODE_ID = "org.csstudio.config.ioconfig.parent.node";

    protected static final Logger LOG = LoggerFactory.getLogger(ProfiBusTreeView.class);

    private final IViewSite _site;
    /**
     * The ProfiBus Tree View.
     */
    private final TreeViewer _viewer;
    /**
     * The parent Composite for the Node Config Composite.
     */
    private final DrillDownAdapter _drillDownAdapter;
    /**
     * the Selected Node.
     */
    private StructuredSelection _selectedNode;
    /**
     * A Copy from a Node.
     */
    private List<AbstractNodeSharedImpl<?, ?>> _copiedNodesReferenceList;
    /**
     * Select _copiedNodesReferenceList Nodes a Copied or moved
     */
    private boolean _move;
    /**
     * This action open an Empty Node. Type of new node dependent on Parent.
     */
    private IAction _newChildrenNodeAction;
    /**
     * This action open an Empty Node. Type of new node dependent on Parent.
     */
    private IAction _openManageProtypeEditorAction;
    /**
     * This action open an selected Node. Type of new node dependent on Parent.
     */
    private IAction _editNodeAction;
    /**
     * This Action open a new empty Node. (No Facility!)
     */
    private Action _newNodeAction;
    /**
     * This action open an selected Node. Type of new node dependent on Parent.
     */
    private IAction _doubleClickAction;
    /**
     * A Action to delete the selected Node.
     */
    private IAction _deletNodeAction;

    /**
     * The action to create the XML config file.
     */
    private IAction _createNewXMLConfigFile;

    /**
     * The action to Copy a Node.
     */
    private IAction _copyNodeAction;

    /**
     * The action to Cut a Node.
     */
    private Action _cutNodeAction;

    /**
     * The action to paste the copied Node.
     */
    private IAction _pasteNodeAction;

    /**
     * The Action to refresh the TreeView.
     */
    private IAction _refreshAction;

    /**
     * The Action to open the Search-Dialog.
     */
    private IAction _searchAction;

    /**
     * The Action to reassemble the EPICS Address String.
     */
    private IAction _assembleEpicsAddressStringAction;

    /**
     * A List of all loaded {@link FacilityDBO}'s
     */
    private List<FacilityDBO> _load;

    /**
     * The actual open Node Config Editor.
     */
    private AbstractNodeEditor<?> _openNodeEditor;

    private Action _createNewSiemensConfigFile;

    private CreateStatisticAction _createNewStatisticFile;

    /**
     * @param parent
     *            The Parent Composit.
     * @param style
     *            The Style of the Composite
     * @param site
     *            The Controll Site
     * @param configComposite
     */
    public ProfiBusTreeView(@Nonnull final Composite parent, final int style, @Nonnull final IViewSite site) {

        super(parent, style);
        new InstanceScope().getNode(IOConfigActivator.PLUGIN_ID).addPreferenceChangeListener(
                new HibernateDBPreferenceChangeListener(getViewer(), this));
        _site = site;

        final GridLayout layout = GridLayoutFactory.fillDefaults().equalWidth(true).create();
        this.setLayout(layout);
        this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        _viewer = new TreeViewer(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        final ColumnViewerEditorActivationStrategy editorActivationStrategy = new ColumnViewerEditorActivationStrategy(
                _viewer);
        TreeViewerEditor.create(_viewer, editorActivationStrategy, ColumnViewerEditor.DEFAULT);
        _drillDownAdapter = new DrillDownAdapter(_viewer);
        _viewer.setContentProvider(new ProfibusTreeContentProvider());

        _viewer.setLabelProvider(new ProfiBusViewLabelProvider());

        class NameSorter extends ViewerSorter {

            @Override
            public int category(@Nullable final Object element) {
                return super.category(element);
            }

            @Override
            public int compare(@Nonnull final Viewer viewer, @Nullable final Object e1, @Nullable final Object e2) {
                if (e1 instanceof NamedDBClass && e2 instanceof NamedDBClass) {
                    final NamedDBClass node1 = (NamedDBClass) e1;
                    final NamedDBClass node2 = (NamedDBClass) e2;
                    if (node1.getSortIndex() == null || node2.getSortIndex() == null) {
                        return -1;
                    }
                    int sortIndex = node1.getSortIndex() - node2.getSortIndex().shortValue();
                    if (sortIndex == 0) {
                        sortIndex = node1.getId() - node2.getId();
                    }
                    return sortIndex;
                }
                return 0;
            }
        }

        _viewer.setSorter(new NameSorter());
        _viewer.getTree().setHeaderVisible(false);
        _viewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        _site.setSelectionProvider(_viewer);
        ColumnViewerToolTipSupport.enableFor(_viewer);

        LOG.debug("ID: {}", _site.getId());
        LOG.debug("PlugIn ID: {}", _site.getPluginId());
        LOG.debug("Name: {}", _site.getRegisteredName());
        LOG.debug("SecID: {}", _site.getSecondaryId());

        runFacilityLoaderJob();

        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();

        class NodeSelectionChangedListener implements ISelectionChangedListener {

            public NodeSelectionChangedListener() {
                // Default Constructor
            }

            @Override
            public void selectionChanged(@Nonnull final SelectionChangedEvent event) {
                if (event.getSelection() instanceof StructuredSelection) {
                    final StructuredSelection selection = (StructuredSelection) event.getSelection();
                    if (!selection.equals(getSelectedNodes())) {
                        setSelectedNode(selection);
                        if (getSelectedNodes() != null && !getSelectedNodes().isEmpty()) {
                            getEditNodeAction().run();
                        }
                    }
                }
            }
        }

        _viewer.addSelectionChangedListener(new NodeSelectionChangedListener());

        this.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(@Nonnull final DisposeEvent e) {
                Repository.close();
            }
        });
    }

    /**
     * Add a new Facility to the tree root.
     * 
     * @param node
     *            the new Facility.
     */
    public final void addFacility(@Nullable final FacilityDBO node) {
        getViewer().setInput(node);
    }

    /**
     * @return false is an Editor open.<br>
     *         e.g. unsaved changes.
     * 
     */
    public boolean closeOpenEditor() {
        boolean isOpen = false;
        if (_openNodeEditor != null) {
            _openNodeEditor.perfromClose();
            if (_openNodeEditor != null) {
                isOpen = _openNodeEditor.isSaveOnCloseNeeded();
                final StructuredSelection selection = new StructuredSelection(_openNodeEditor.getNode());
                setSelectedNode(selection);
                getTreeViewer().setSelection(selection);
            } else {
                _openNodeEditor = null;
            }
        }
        return !isOpen;
    }

    /**
     * Expand the complete Tree.
     */
    public final void expandAll() {
        /*
         * TODO: Wird nicht mehr gemacht da es von der Performenc her unklug
         * ist. Es werden einfach zuviele Nodes auf einmal geladen, was zu Laden
         * zeiten in Minutenbreiche fuehrt
         */
    }

    @CheckForNull
    public AbstractNodeEditor<?> getOpenEditor() {
        return _openNodeEditor;
    }

    @Nonnull
    public StructuredSelection getSelectedNodes() {
        if (_selectedNode == null) {
            _selectedNode = new StructuredSelection();
        }
        return _selectedNode;
    }

    /**
     * @return the site
     */
    @Nonnull
    public IViewSite getSite() {
        return _site;
    }

    /**
     * 
     * @return the Control of the TreeViewer
     */
    @Nonnull
    public final TreeViewer getTreeViewer() {
        return getViewer();
    }

    @Nonnull
    public TreeViewer getViewer() {
        return _viewer;
    }

    /** refresh the Tree. Reload all Nodes */
    public final void refresh() {
        getViewer().setInput(new Object());
        getViewer().refresh();
    }

    /**
     * Refresh the Tree. Reload element Nodes
     * 
     * @param element
     *            Down at this element the tree are refreshed.
     */
    public final void refresh(@Nullable final Object element) {
        getViewer().refresh(element, true);
    }

    public final void reload() {
        _refreshAction.run();
    }

    public void removeOpenEditor(@Nullable final AbstractNodeEditor<?> openNodeEditor) {
        if (_openNodeEditor != null && _openNodeEditor.equals(openNodeEditor)) {
            _openNodeEditor = null;
        }
    }

    /**
     * @param abstractNodeEditor
     */
    public void setOpenEditor(@Nullable final AbstractNodeEditor<?> openNodeEditor) {
        _openNodeEditor = openNodeEditor;
    }

    private void contributeToActionBars() {
        final IActionBars bars = _site.getActionBars();
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalToolBar(@Nonnull final IToolBarManager manager) {
        manager.add(new Separator());
        manager.add(makeNewFacilityAction());
        manager.add(_refreshAction);
        manager.add(new Separator());
        _drillDownAdapter.addNavigationActions(manager);
        manager.add(new Separator());
        manager.add(_searchAction);
    }

    /**
     * @param manager
     */
    private void fillModuleContextMenu(@Nonnull final IMenuManager manager) {
        _newNodeAction.setText("Add new " + ModuleDBO.class.getSimpleName());
        manager.add(_newNodeAction);
        manager.add(_copyNodeAction);
        manager.add(_cutNodeAction);

        final boolean pasteEnable = _copiedNodesReferenceList != null && _copiedNodesReferenceList.size() > 0
                && ModuleDBO.class.isInstance(_copiedNodesReferenceList.get(0));
        _pasteNodeAction.setEnabled(pasteEnable);
        manager.add(_pasteNodeAction);
        manager.add(_deletNodeAction);
        manager.add(new Separator());
    }

    private void hookContextMenu() {
        final MenuManager popupMenuMgr = new MenuManager("#PopupMenu");
        popupMenuMgr.setRemoveAllWhenShown(true);
        popupMenuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(@Nonnull final IMenuManager manager) {
                ProfiBusTreeView.this.fillContextMenu(manager);
            }
        });
        final Menu menu = popupMenuMgr.createContextMenu(getViewer().getControl());
        menu.setVisible(false);

        getViewer().getControl().setMenu(menu);
        _site.registerContextMenu(popupMenuMgr, getViewer());

        final ImageDescriptor iDesc = CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
                IOConfigActivatorUI.PLUGIN_ID, "icons/collapse_all.gif");
        final Action collapseAllAction = new Action() {
            @Override
            public void run() {
                getViewer().collapseAll();
            }
        };
        collapseAllAction.setText("Collapse All");
        collapseAllAction.setToolTipText("Collapse All");
        collapseAllAction.setImageDescriptor(iDesc);
        _site.getActionBars().getToolBarManager().add(collapseAllAction);
        final ToolBar tB = new ToolBar(getViewer().getTree(), SWT.NONE);
        final ToolBarManager tBM = new ToolBarManager(tB);
        tBM.add(collapseAllAction);
        tBM.createControl(getViewer().getTree());
    }

    private void hookDoubleClickAction() {
        getViewer().addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(@Nonnull final DoubleClickEvent event) {
                getDoubleClickAction().run();
            }
        });
    }

    private void makeActions() {
        makeNewChildrenNodeAction();
        makeNewNodeAction();
        makeEditNodeAction();
        makeNewFacilityAction();
        makeOpenManageProtypeEditorAction();
        makeSearchAction();
        makeAssembleEpicsAddressStringAction();
        makeCopyNodeAction();
        makeCutNodeAction();
        makePasteNodeAction();
        makeDeletNodeAction();
        makeCreateNewXMLConfigFile();
        makeCreateNewSiemensConfigFile();
        makeCreateNewStatisticFile();
        makeTreeNodeRenameAction();
        makeRefreshAction();
    }

    /**
     * Generate a Action that reassemble the EPICS Address String for the
     * selected {@link AbstractNodeSharedImpl} and all Children.
     */
    private void makeAssembleEpicsAddressStringAction() {
        _assembleEpicsAddressStringAction = new Action() {
            @Override
            public void run() {
                final Object selectedNode = getSelectedNodes().getFirstElement();
                if (selectedNode instanceof AbstractNodeSharedImpl) {
                    final AbstractNodeSharedImpl<?, ?> node = (AbstractNodeSharedImpl<?, ?>) selectedNode;
                    try {
                        node.assembleEpicsAddressString();
                    } catch (final PersistenceException e) {
                        // TODO Handle DDB Error
                        e.printStackTrace();
                    }
                }
            }
        };
        _assembleEpicsAddressStringAction.setText("Refresh EPCICS Adr");
        _assembleEpicsAddressStringAction.setToolTipText("Refesh from all childen the EPICS Address Strings");
        _assembleEpicsAddressStringAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(IOConfigActivatorUI.PLUGIN_ID, "icons/refresh.gif"));

    }

    private void makeCopyNodeAction() {
        _copyNodeAction = new Action() {

            @Override
            @SuppressWarnings("unchecked")
            public void run() {
                setCopiedNodesReferenceList(getSelectedNodes().toList());
                setMove(false);
            }
        };
        _copyNodeAction.setText("&Copy");
        _copyNodeAction.setToolTipText("Copy this Node");
        _copyNodeAction.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_COPY));
    }

    private void makeCreateNewSiemensConfigFile() {
        _createNewSiemensConfigFile = new CreateWinModAction("Create WinMod", this);
        _createNewSiemensConfigFile.setToolTipText("Action Create tooltip");
        _createNewSiemensConfigFile.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_OBJ_FILE));
    }

    private void makeCreateNewStatisticFile() {
        _createNewStatisticFile = new CreateStatisticAction("Create Statistik", this);
        _createNewStatisticFile.setToolTipText("Action Create tooltip");
        _createNewStatisticFile.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_OBJ_FILE));
    }

    private void makeCreateNewXMLConfigFile() {
        _createNewXMLConfigFile = new CreateXMLConfigAction("Create EPICS", this);
        _createNewXMLConfigFile.setToolTipText("Action Create tooltip");
        _createNewXMLConfigFile.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_OBJ_FILE));
    }

    private void makeCutNodeAction() {
        _cutNodeAction = new Action() {

            @Override
            @SuppressWarnings("unchecked")
            public void run() {
                setCopiedNodesReferenceList(getSelectedNodes().toList());
                setMove(true);
            }
        };
        _cutNodeAction.setText("Cut");
        _cutNodeAction.setAccelerator(SWT.CTRL | 'x');
        _cutNodeAction.setToolTipText("Cut this Node");
        _cutNodeAction.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_CUT));
    }

    private void makeDeletNodeAction() {
        _deletNodeAction = new DeleteNodeAction(this);
        _deletNodeAction.setText("Delete");
        _deletNodeAction.setAccelerator(SWT.DEL);
        _deletNodeAction.setToolTipText("Delete this Node");
        _deletNodeAction.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_OBJS_ERROR_TSK));
    }

    /**
     * Generate a Action that open the {@link AbstractNodeConfig} for the
     * selected {@link AbstractNodeSharedImpl}.
     */
    private void makeEditNodeAction() {
        _editNodeAction = new Action() {

            @Override
            public void run() {
                if (getEnabled()) {
                    editNode();
                }
            }
        };
        _editNodeAction.setText("Edit");
        _editNodeAction.setToolTipText("Edit Node");
        _editNodeAction.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));

    }

    /**
     * Generate a Action that opens the 'Manage Prototypes' view.
     */
    private void makeOpenManageProtypeEditorAction() {
        _openManageProtypeEditorAction = new Action() {
            @Override
            public void run() {
                if (getSelectedNodes().getFirstElement() instanceof SlaveDBO) {
                    final SlaveDBO selectedSlave = (SlaveDBO) getSelectedNodes().getFirstElement();                                   
                    ChannelConfigDialog channelConfigDialog = new ChannelConfigDialog(Display.getCurrent()
                            .getActiveShell(), selectedSlave);                    
                    channelConfigDialog.open();
                }
            }
        };
        _openManageProtypeEditorAction.setText("Open Prototypes view");
        _openManageProtypeEditorAction.setToolTipText("Open Prototypes manager");
        _openManageProtypeEditorAction.setAccelerator('o');
        _openManageProtypeEditorAction.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));
    }

    /**
     * Generate a Action that make a new Children {@link AbstractNodeSharedImpl}
     * and open the Config View.
     */
    private void makeNewChildrenNodeAction() {
        _newChildrenNodeAction = new Action() {
            @Override
            public void run() {
                openNewEmptyChildrenNode();
            }
        };
        _newChildrenNodeAction.setText("New");
        _newChildrenNodeAction.setToolTipText("Action 1 tooltip");
        _newChildrenNodeAction.setAccelerator('n');
        _newChildrenNodeAction.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_OBJ_ADD));
    }

    /**
     * This action open a new level one empty Node. The type of this node is
     * {@link FacilityDBO}.
     */
    @Nonnull
    private Action makeNewFacilityAction() {
        final Action newFacilityAction = new Action() {

            @Override
            public void run() {
                if (closeOpenEditor()) {
                    final IHandlerService handlerService = (IHandlerService) getSite()
                            .getService(IHandlerService.class);
                    try {
                        handlerService.executeCommand(CallNewFacilityEditor.ID, null);
                    } catch (final Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        };
        newFacilityAction.setText("new Facility");
        newFacilityAction.setToolTipText("Create a new Facility");
        newFacilityAction.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));
        return newFacilityAction;

    }

    /**
     * Generate a Action that make a new Sibling {@link AbstractNodeSharedImpl}
     * and open the Config View.
     */
    private void makeNewNodeAction() {
        _newNodeAction = new Action() {
            @Override
            public void run() {
                if (closeOpenEditor()) {
                    openNewEmptySiblingNode();
                }
            }

        };
        _newNodeAction.setText("New");
        _newNodeAction.setToolTipText("Action 1 tooltip");
        _newNodeAction.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_ETOOL_HOME_NAV));
    }

    private void makePasteNodeAction() {
        _pasteNodeAction = new PasteNodeAction(this);
        _pasteNodeAction.setText("Paste");
        _pasteNodeAction.setAccelerator('v');
        _pasteNodeAction.setToolTipText("Paste this Node");
        _pasteNodeAction.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
        _pasteNodeAction.setDisabledImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));

    }

    private void makeRefreshAction() {
        _refreshAction = new Action() {
            @Override
            public void run() {
                runFacilityLoaderJob();
            }
        };

        _refreshAction.setText("Reload");
        _refreshAction.setToolTipText("Reload from the DataBase.");
        _refreshAction.setImageDescriptor(CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
                IOConfigActivatorUI.PLUGIN_ID, "icons/refresh.gif"));
    }

    /**
     * Generate a Action that open the {@link SearchDialog}.
     */
    private void makeSearchAction() {
        _searchAction = new Action() {

            @Override
            public void run() {
                final SearchDialog searchDialog = new SearchDialog(getShell(), ProfiBusTreeView.this);
                searchDialog.open();
            }

        };
        _searchAction.setText("Search");
        _searchAction.setToolTipText("Search a Node");
        _searchAction.setImageDescriptor(CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
                IOConfigActivatorUI.PLUGIN_ID, "icons/search.png"));
    }

    private void makeTreeNodeRenameAction() {

        // Create the editor and set its attributes
        final TreeEditor editor = new TreeEditor(getViewer().getTree());
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;
        _doubleClickAction = new RenameNodeAction(this, editor);

    }

    private void openEditor(@Nonnull final String editorID) {
        if (closeOpenEditor()) {
            final IHandlerService handlerService = (IHandlerService) _site.getService(IHandlerService.class);
            try {
                handlerService.executeCommand(editorID, null);
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    protected void openNewEmptySiblingNode() {
        openEditor(CallNewSiblingNodeEditor.getEditorID());
    }

    /**
     * Set the Action to handle Node's.<br>
     * - new Child<br>
     * - copy<br>
     * - paste<br>
     * - delete<br>
     * 
     * @param text
     *            Set the Text for this new Node Action.
     * @param clazz
     *            the Node class to check can paste.
     * @param childClazz
     *            the Node child class to check can paste.
     * @param manager
     *            The {@link IMenuManager} to add the Actions.
     */
    private void setContributionActions(@Nonnull final String text, @Nonnull final Class<?> clazz,
            @Nonnull final Class<?> childClazz, @Nonnull final IMenuManager manager) {
        _newChildrenNodeAction.setText(text);
        final boolean pasteEnable = _copiedNodesReferenceList != null
                && _copiedNodesReferenceList.size() > 0
                && (clazz.isInstance(_copiedNodesReferenceList.get(0))
                        || childClazz.isInstance(_copiedNodesReferenceList.get(0)) || clazz.equals(FacilityDBO.class)
                        && FacilityDBO.class.isInstance(_copiedNodesReferenceList.get(0)));
        _pasteNodeAction.setEnabled(pasteEnable);
        manager.add(_newChildrenNodeAction);
        manager.add(_copyNodeAction);
        manager.add(_cutNodeAction);
        manager.add(_pasteNodeAction);
        manager.add(_deletNodeAction);
    }

    /**
     * Open a ConfigComposite for the tree selection Node.
     */
    protected void editNode() {
        _editNodeAction.setEnabled(false);
        if (closeOpenEditor()) {
            final IHandlerService handlerService = (IHandlerService) _site.getService(IHandlerService.class);
            try {
                handlerService.executeCommand(CallEditor.ID, null);
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    // CHECKSTYLE OFF: CyclomaticComplexity
    protected void fillContextMenu(@Nonnull final IMenuManager manager) {
        final StructuredSelection selection = getSelectedNodes();
        if (selection != null) {
            final Object selectedNode = selection.getFirstElement();
            if (selectedNode instanceof FacilityDBO) {
                setContributionActions("New Ioc", FacilityDBO.class, IocDBO.class, manager);
                manager.add(new Separator());
                manager.add(_createNewXMLConfigFile);
                manager.add(_createNewSiemensConfigFile);
                manager.add(_createNewStatisticFile);
            } else if (selectedNode instanceof IocDBO) {
                setContributionActions("New Subnet", IocDBO.class, ProfibusSubnetDBO.class, manager);
                manager.add(new Separator());
                manager.add(_createNewXMLConfigFile);
                manager.add(_createNewSiemensConfigFile);
            } else if (selectedNode instanceof ProfibusSubnetDBO) {
                setContributionActions("New Master", ProfibusSubnetDBO.class, MasterDBO.class, manager);
                manager.add(_createNewXMLConfigFile);
                manager.add(_createNewSiemensConfigFile);
            } else if (selectedNode instanceof MasterDBO) {
                setContributionActions("New Slave", MasterDBO.class, SlaveDBO.class, manager);
            } else if (selectedNode instanceof SlaveDBO) {
                _newNodeAction.setText("Add new " + SlaveDBO.class.getSimpleName());
                manager.add(_newNodeAction);
                manager.add(_openManageProtypeEditorAction);
                setContributionActions("New Module", SlaveDBO.class, ModuleDBO.class, manager);
            } else if (selectedNode instanceof ModuleDBO) {
                fillModuleContextMenu(manager);
            }
            manager.add(_assembleEpicsAddressStringAction);
            manager.add(new Separator());
            _drillDownAdapter.addNavigationActions(manager);
            // Other plug-ins can contribute there actions here
            manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        }
    }

    // CHECKSTYLE ON: CyclomaticComplexity

    @Nonnull
    public List<AbstractNodeSharedImpl<?, ?>> getCopiedNodesReferenceList() {
        return _copiedNodesReferenceList;
    }

    @Nonnull
    protected IAction getDoubleClickAction() {
        return _doubleClickAction;
    }

    @Nonnull
    public final IAction getEditNodeAction() {
        assert _editNodeAction != null;
        return _editNodeAction;
    }

    @Nonnull
    public List<FacilityDBO> getLoad() {
        return _load;
    }

    public boolean isMove() {
        return _move;
    }

    protected void openInfoDialog() {
        final Shell shell = new Shell(getShell(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
        final Dialog infoDialog = new InfoDialog(shell);
        infoDialog.open();
    }

    protected void openNewEmptyChildrenNode() {
        openEditor(CallNewChildrenNodeEditor.getEditorID());
    }

    /**
     *
     */
    protected void runFacilityLoaderJob() {
        getViewer().setInput("Please wait a moment");
        final AbstractNodeEditor<?> openEditor = getOpenEditor();
        if (openEditor != null) {
            openEditor.perfromClose();
        }
        try {
            getViewer().getTree().setEnabled(false);
            final Job loadJob = new DBLoaderJob(getViewer(), this, "DBLoader");
            loadJob.setUser(true);
            loadJob.schedule();
        } catch (final RuntimeException e) {
            ProfibusHelper.openErrorDialog(_site.getShell(), "Data Base Error", "Device Data Base (DDB) Error\n"
                    + "Can't load the Root data", null, e);
            return;
        }
    }

    protected final void setCopiedNodesReferenceList(
            @Nonnull final List<AbstractNodeSharedImpl<?, ?>> copiedNodesReferenceList) {
        _copiedNodesReferenceList = copiedNodesReferenceList;
    }

    public void setLoad(@Nonnull final List<FacilityDBO> load) {
        _load = load;
    }

    protected void setMove(final boolean move) {
        _move = move;
    }

    public void setSelectedNode(@Nullable final StructuredSelection selectedNode) {
        _selectedNode = selectedNode;
    }

    /**
     * Retrieves the image descriptor for specified image from the workbench's
     * image registry. Unlike Images, image descriptors themselves do not need
     * to be disposed.
     * 
     * @param symbolicName
     *            the symbolic name of the image; there are constants declared
     *            in this interface for build-in images that come with the
     *            workbench
     * @return the image descriptor, or null if not found
     */
    @CheckForNull
    private static ImageDescriptor getSharedImageDescriptor(@Nonnull final String symbolicName) {
        return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(symbolicName);
    }
}
