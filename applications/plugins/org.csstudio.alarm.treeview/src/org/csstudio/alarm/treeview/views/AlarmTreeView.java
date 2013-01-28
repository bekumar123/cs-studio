/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.alarm.treeview.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.treeview.AlarmTreePlugin;
import org.csstudio.alarm.treeview.jobs.ConnectionJob;
import org.csstudio.alarm.treeview.jobs.JobFactory;
import org.csstudio.alarm.treeview.localization.Messages;
import org.csstudio.alarm.treeview.model.IAlarmProcessVariableNode;
import org.csstudio.alarm.treeview.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeview.model.IAlarmTreeNode;
import org.csstudio.alarm.treeview.model.IProcessVariableNodeListener;
import org.csstudio.alarm.treeview.model.ProcessVariableNode;
import org.csstudio.alarm.treeview.model.SubtreeNode;
import org.csstudio.alarm.treeview.model.TreeNodeSource;
import org.csstudio.alarm.treeview.preferences.AlarmTreePreference;
import org.csstudio.alarm.treeview.service.AlarmMessageListener;
import org.csstudio.alarm.treeview.views.actions.AlarmTreeViewActionFactory;
import org.csstudio.auth.ui.security.AbstractUserDependentAction;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.servicelocator.ServiceLocator;
import org.csstudio.utility.ldap.treeconfiguration.EpicsAlarmcfgTreeNodeAttribute;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.DelegatingDragAdapter;
import org.eclipse.jface.util.DelegatingDropAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.ui.progress.PendingUpdateAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tree view of process variables and their alarm state. This view uses LDAP to get a hierarchy of
 * process variables and displays them in a tree view. Process variables which are in an alarm state
 * are visually marked in the view.
 *
 * @author Joerg Rathlev
 * @author Bastian Knerr
 */
public final class AlarmTreeView extends ViewPart implements ISaveablePart2 {
    
    /**
     * The ID of this view.
     */
    private static final String ID = "org.csstudio.alarm.treeview.views.AlarmTreeView"; //$NON-NLS-1$
    
    private static final Logger LOG = LoggerFactory.getLogger(AlarmTreeView.class);
    
    // The connection to the underlying implementation, be it DAL or JMS. Is null, if connectionJob failed.
    private IAlarmConnection _connection;
    
    /**
     * Returns whether a list of nodes contains only ProcessVariableNodes.
     */
    static boolean containsOnlyPVNodes(@Nonnull final List<IAlarmTreeNode> nodes) {
        for (final IAlarmTreeNode node : nodes) {
            if (!(node instanceof ProcessVariableNode)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns the id of this view.
     *
     * @return the id of this view.
     */
    @Nonnull
    public static String getID() {
        return ID;
    }
    
    /**
     * Converts a selection into a list of selected alarm tree nodes.
     */
    @Nonnull
    static List<IAlarmTreeNode> selectionToNodeList(@Nullable final ISelection selection) {
        
        if (selection instanceof IStructuredSelection) {
            final List<IAlarmTreeNode> result = new ArrayList<IAlarmTreeNode>();
            final IStructuredSelection s = (IStructuredSelection) selection;
            for (final Iterator<?> i = s.iterator(); i.hasNext();) {
                result.add((IAlarmTreeNode) i.next());
            }
            return result;
        }
        return Collections.emptyList();
    }
    
    // The tree viewer that displays the alarm objects.
    private TreeViewer _viewer;
    
    // The message area above the tree viewer
    private MessageArea _myMessageArea;
    
    // The callback for the alarm messages
    private AlarmMessageListener _alarmListener;
    
    private Action _reloadAction;
    private Action _remoteReloadAction;
    
    // Action to persist current Alarm Tree View in LDAP.
    private AbstractUserDependentAction _saveInLdapAction;
    private Action _importXmlFileAction;
    private Action _exportXmlFileAction;
    
    // Saves the currently configured alarm tree as xml file.
    private Action _saveAsXmlFileAction;
    private Action _acknowledgeAction;
    private Action _runCssAlarmDisplayAction;
    private Action _runCssDisplayAction;
    private Action _openCssStripChartAction;
    private Action _showHelpPageAction;
    private Action _showHelpGuidanceAction;
    private Action _createRecordAction;
    private Action _createComponentAction;
    private Action _renameAction;
    private Action _deleteNodeAction;
    private Action _showPropertyViewAction;
    
    // the action to show / hide the message area
    private Action _showMessageAreaAction;
    
    // Action to retrieve the initial state of subtrees.
    private Action _retrieveInitialStateAction;
    
    // Action which toggles the filter on and off.
    private Action _toggleFilterAction;
    
    /**
     * A filter which hides all nodes which are not currently in an alarm state.
     */
    private ViewerFilter _currentAlarmFilter;
    
    /**
     * The root node of this alarm tree. Shall only be generated once per view!
     */
    private final IAlarmSubtreeNode _rootNode;
    
    /**
     * Queue that stores all modifications that have to be applied to the LDAP store on
     * {@link org.csstudio.alarm.treeview.views.actions.SaveInLdapAction}.
     */
    private final Queue<ITreeModificationItem> _ldapModificationItems = new LdapModificationQueue();
    
    // Listener for the life cycle of the pv-nodes in the tree. Used for de/registering pvs at the underlying system.
    private IProcessVariableNodeListener _processVariableNodeListener;
    
    // Listener for the remote command telling the view to reload the configuration
    private IAlarmService.IListener _configurationUpdateListener;

	private Action _unacknowledgedAlarmFilterAction;
    
    /**
     * Constructor.
     * Creates an LDAP tree viewer.
     */
    public AlarmTreeView() {
        _rootNode = new SubtreeNode.Builder(LdapEpicsAlarmcfgConfiguration.VIRTUAL_ROOT.getObjectClass(),
                                            LdapEpicsAlarmcfgConfiguration.VIRTUAL_ROOT,
                                            TreeNodeSource.ROOT).build();
    }
    
    /**
     * Adds drag and drop support to the tree viewer.
     */
    private void addDragAndDropSupport() {
        final DelegatingDropAdapter dropAdapter = new DelegatingDropAdapter();
        dropAdapter
                .addDropTargetListener(new AlarmTreeLocalSelectionDropListener(this,
                                                                               _ldapModificationItems));
        dropAdapter
                .addDropTargetListener(new AlarmTreeProcessVariableDropListener(this,
                                                                                _ldapModificationItems));
        _viewer.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE,
                               dropAdapter.getTransfers(),
                               dropAdapter);
        
        final DelegatingDragAdapter dragAdapter = new DelegatingDragAdapter();
        dragAdapter.addDragSourceListener(new AlarmTreeLocalSelectionDragListener(this));
        dragAdapter.addDragSourceListener(new AlarmTreeProcessVariableDragListener(this));
        dragAdapter.addDragSourceListener(new AlarmTreeTextDragListener(this));
        _viewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE,
                               dragAdapter.getTransfers(),
                               dragAdapter);
    }
    
    /**
     * Returns whether the given selection contains at least one node with an unacknowledged alarm.
     *
     * @param sel the selection.
     * @return <code>true</code> if the selection contains a node with an unacknowledged alarm,
     *         <code>false</code> otherwise.
     */
    private boolean containsNodeWithUnackAlarm(@Nonnull final IStructuredSelection sel) {
        final Object selectedElement = sel.getFirstElement();
        // Note: selectedElement is not instance of IAlarmTreeNode if nothing
        // is selected (selectedElement == null), and during initialization,
        // when it is an instance of PendingUpdateAdapter.
        if (selectedElement instanceof IAlarmTreeNode) {
            return ((IAlarmTreeNode) selectedElement).getUnacknowledgedAlarmSeverity() != EpicsAlarmSeverity.NO_ALARM;
        }
        return false;
    }
    
    /**
     * Adds tool buttons and menu items to the action bar of this view.
     */
    private void contributeToActionBars() {
        final IActionBars bars = getViewSite().getActionBars();
        fillLocalToolBar(bars.getToolBarManager());
    }
    
    /**
     * Creates the actions offered by this view.
     * @param iWorkbenchPartSite
     * @param alarmListener
     * @param viewer
     * @param currentAlarmFilter
     *
     * CHECKSTYLE OFF: MethodLength (this method properly encapsulates all view actions)
     */
    private void createActions(@Nonnull final IAlarmSubtreeNode rootNode,
                               @Nonnull final TreeViewer viewer,
                               @Nonnull final AlarmMessageListener alarmListener,
                               @Nonnull final IWorkbenchPartSite site,
                               @Nonnull final ViewerFilter currentAlarmFilter,
                               @Nonnull final ViewerFilter unacknowledgedAlarmFilter,
                               @Nonnull final Queue<ITreeModificationItem> modificationItems) {
        
        _reloadAction = AlarmTreeViewActionFactory
                .createReloadAction(JobFactory.createImportInitialConfigJob(this, rootNode),
                                    site,
                                    alarmListener,
                                    viewer,
                                    modificationItems);
        
        _remoteReloadAction = AlarmTreeViewActionFactory.createRemoteReloadAction(_myMessageArea);
        
        _saveInLdapAction = AlarmTreeViewActionFactory.createSaveInLdapAction(rootNode,
                                                                              site,
                                                                              viewer,
                                                                              modificationItems);
        
        _importXmlFileAction = AlarmTreeViewActionFactory.createImportXmlFileAction(JobFactory
                .createImportXmlFileJob(this, rootNode), site, alarmListener, viewer);
        
        _exportXmlFileAction = AlarmTreeViewActionFactory.createExportXmlFileAction(site, rootNode);
        
        _acknowledgeAction = AlarmTreeViewActionFactory.createAcknowledgeAction(viewer);
        
        _runCssAlarmDisplayAction = AlarmTreeViewActionFactory.createCssAlarmDisplayAction(viewer);
        
        _runCssDisplayAction = AlarmTreeViewActionFactory.createRunCssDisplayAction(viewer);
        
        _openCssStripChartAction = AlarmTreeViewActionFactory.createCssStripChartAction(site,
                                                                                        viewer);
        
        _showHelpGuidanceAction = AlarmTreeViewActionFactory.createShowHelpGuidanceAction(site,
                                                                                          viewer);
        
        _showHelpPageAction = AlarmTreeViewActionFactory.createShowHelpPageAction(viewer);
        
        _createRecordAction = AlarmTreeViewActionFactory
                .createCreateRecordAction(site, viewer, this, modificationItems);
        
        _createComponentAction = AlarmTreeViewActionFactory
                .createCreateComponentAction(site, viewer, modificationItems);
        _renameAction = AlarmTreeViewActionFactory.createRenameAction(site,
                                                                      viewer,
                                                                      modificationItems);
        
        _deleteNodeAction = AlarmTreeViewActionFactory.createDeleteNodeAction(site,
                                                                              viewer,
                                                                              modificationItems);
        
        _showPropertyViewAction = AlarmTreeViewActionFactory.createShowPropertyViewAction(site);
        
        _showMessageAreaAction = AlarmTreeViewActionFactory
                .createShowMessageAreaAction(_myMessageArea);
        
        _toggleFilterAction = AlarmTreeViewActionFactory
                .createToggleFilterAction(viewer, currentAlarmFilter);
        
        _unacknowledgedAlarmFilterAction = AlarmTreeViewActionFactory.createToggleUnacknowledgedAlarmFilterAction(viewer, unacknowledgedAlarmFilter);
        
        _saveAsXmlFileAction = AlarmTreeViewActionFactory.createSaveAsXmlFileAction(site, viewer);
        
        _retrieveInitialStateAction = AlarmTreeViewActionFactory
                .createRetrieveInitialStateAction(site, JobFactory
                        .createRetrieveInitialStateJob(this, rootNode), viewer);
    }
    
    // CHECKSTYLE ON: MethodLength (this method properly encapsulates all view actions)
    
    // requires the existence of the reload action when run
    private void createAndRegisterReloadCommand() {
        _configurationUpdateListener = new MyAlarmListener();
        ServiceLocator.getService(IAlarmService.class).register(_configurationUpdateListener);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(@Nonnull final Composite parent) {
        final GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        parent.setLayout(layout);
        
        _myMessageArea = new MessageArea(parent);
        
        _viewer = createTreeViewer(parent);
        getSite().setSelectionProvider(_viewer);
        
        _currentAlarmFilter = new CurrentAlarmFilter();
        
        _alarmListener = new AlarmMessageListener(_rootNode);
        
        initializeContextMenu();
        
        createActions(_rootNode,
                      _viewer,
                      _alarmListener,
                      getSite(),
                      _currentAlarmFilter,
                      new UnacknowledgedAlarmFilter(),
                      _ldapModificationItems);
        
        createAndRegisterReloadCommand();
        
        contributeToActionBars();
        
        createAndScheduleConnectionJob();
        addDragAndDropSupport();
        trackModificationItemsForWarning();
    }
    
    @Nonnull
    private TreeViewer createTreeViewer(@Nonnull final Composite parent) {
        final TreeViewer viewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL
                | SWT.V_SCROLL);
        
        viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        viewer.setContentProvider(new AlarmTreeContentProvider());
        AlarmTreeLabelProvider labelProvider = new AlarmTreeLabelProvider();
		viewer.setLabelProvider(labelProvider);
        
        final ISelectionChangedListener selectionChangedListener = new ISelectionChangedListener() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void selectionChanged(@Nonnull final SelectionChangedEvent event) {
                AlarmTreeView.this.selectionChanged(event);
            }
        };
        viewer.addSelectionChangedListener(selectionChangedListener);
        
        final IPreferenceStore preferenceStore = AlarmTreePlugin.getDefault().getPreferenceStore();
        FontData fontData = PreferenceConverter.getFontData(preferenceStore, AlarmTreePreference.FONT.getKeyAsString());
        Font font = new Font(Display.getDefault(), fontData);
		viewer.getTree().setFont(font);
        
        GC gc = new GC(parent);
		gc.setFont(font);
		int height = gc.getFontMetrics().getAscent();
		gc.dispose();
		labelProvider.setImagePixelHeight(height);
        
        return viewer;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        tryToDeregisterReloadListener();
        tryToDisconnect();
        super.dispose();
    }
    
    private void tryToDeregisterReloadListener() {
        IAlarmService service = ServiceLocator.getService(IAlarmService.class);
        if (service != null) {
            service.deregister(_configurationUpdateListener);
        }
    }
    
    private void tryToDisconnect() {
        if (_connection != null) {
            _connection.disconnect();
        }
    }
    
    /**
     * Adds the context menu actions.
     *
     * @param menuManager the menu manager.
     */
    private void fillContextMenu(@Nullable final IMenuManager menuManager) {
        if (menuManager == null) {
            MessageDialog.openError(getSite().getShell(),
                                    Messages.AlarmTreeView_MessageDialog_ContextMenu_Error_Title,
                                    Messages.AlarmTreeView_MessageDialog_ContextMenu_Error_Message);
            return;
        }
        
        final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
        if (selection.size() > 0) {
            menuManager.add(_acknowledgeAction);
        }
        if (selection.size() == 1) {
            menuManager.add(_runCssAlarmDisplayAction);
            menuManager.add(_runCssDisplayAction);
            menuManager.add(_openCssStripChartAction);
            menuManager.add(_showHelpGuidanceAction);
            menuManager.add(_showHelpPageAction);
            menuManager.add(new Separator(Messages.AlarmTreeView_Menu_Separator_Edit));
            menuManager.add(_deleteNodeAction);
            
            final Object selected = selection.getFirstElement();
            if (selected instanceof SubtreeNode) {
                menuManager.add(_retrieveInitialStateAction);
            }
            
            final IAlarmTreeNode firstElement = (IAlarmTreeNode) selection.getFirstElement();
            final LdapEpicsAlarmcfgConfiguration oc = firstElement.getTreeNodeConfiguration();
            
            if (!LdapEpicsAlarmcfgConfiguration.RECORD.equals(oc)) {
                menuManager.add(_createRecordAction);
                menuManager.add(_createComponentAction);
            }
            if (!LdapEpicsAlarmcfgConfiguration.FACILITY.equals(oc)) {
                menuManager.add(_renameAction);
            } else {
                menuManager.add(_saveAsXmlFileAction);
            }
        }
        
        // adds a separator after which contributed actions from other plug-ins
        // will be displayed
        menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }
    
    /**
     * Adds the tool bar actions.
     *
     * @param manager the menu manager.
     */
    private void fillLocalToolBar(@Nonnull final IToolBarManager manager) {
    	manager.add(_unacknowledgedAlarmFilterAction);
        manager.add(_toggleFilterAction);
        manager.add(new Separator());
        manager.add(_showPropertyViewAction);
        manager.add(_showMessageAreaAction);
        manager.add(_saveInLdapAction);
        manager.add(_remoteReloadAction);
        manager.add(_reloadAction);
        manager.add(new Separator());
        manager.add(_importXmlFileAction);
        manager.add(_exportXmlFileAction);
    }
    
    /**
     * Getter.
     * @param the alarm listener
     */
    @CheckForNull
    public AlarmMessageListener getAlarmListener() {
        return _alarmListener;
    }
    
    /**
     * Getter.
     * @return the message area
     */
    @CheckForNull
    public MessageArea getMessageArea() {
        return _myMessageArea;
    }
    
    /**
     * Getter.
     * @return the reload action reference
     */
    @CheckForNull
    public Action getReloadAction() {
        return _reloadAction;
    }
    
    /**
     * Getter.
     * @return the rename action reference
     */
    @CheckForNull
    public Action getRenameAction() {
        return _renameAction;
    }
    
    /**
     * Getter.
     * @return the save in LDAP action reference
     */
    @CheckForNull
    public Action getSaveInLdapAction() {
        return _saveInLdapAction;
    }
    
    /**
     * Getter.
     * @return the view's root node (not visible).
     */
    @Nonnull
    public IAlarmSubtreeNode getRootNode() {
        return _rootNode;
    }
    
    /**
     * Getter.
     * @return the tree viewer
     */
    @CheckForNull
    public TreeViewer getViewer() {
        return _viewer;
    }
    
    // TODO (jpenning) remove connection, use listener concept instead
    /**
     * @return the connection or null
     */
    @CheckForNull
    public IAlarmConnection getConnection() {
        return _connection;
    }
    
    @Nonnull
    @SuppressWarnings("synthetic-access")
    public IProcessVariableNodeListener getPVNodeListener() {
        if (_processVariableNodeListener == null) {
            _processVariableNodeListener = new IProcessVariableNodeListener() {
                
                @Override
                public void wasAdded(@Nonnull final String pvName) {
                    if (_connection != null) {
                    	if (!isPresentInTree(pvName)) {
                    		_connection.registerPV(pvName);
                    		AlarmTreeView.LOG.trace("pv registered: " + pvName); //$NON-NLS-1$
                    	}
                    }
                }
                
				@Override
                public void wasRemoved(@Nonnull final String pvName) {
                    if (_connection != null) {
                    	if (!isPresentInTree(pvName)) {
                    		_connection.deregisterPV(pvName);
                    		AlarmTreeView.LOG.trace("pv deregistered: " + pvName); //$NON-NLS-1$
                    	}
                    }
                }
            };
        }
        return _processVariableNodeListener;
    }
    
    private boolean isPresentInTree(@Nonnull final String pvName) {
    	List<IAlarmProcessVariableNode> alarmProcessVariableNodes = getRootNode().findProcessVariableNodes(pvName);
		return !alarmProcessVariableNodes.isEmpty();
	}


    /**
     * Returns whether the given process variable node in the tree has an associated CSS alarm
     * display configured.
     *
     * @param node the node.
     * @return <code>true</code> if a CSS alarm display is configured for the node,
     *         <code>false</code> otherwise.
     */
    private boolean hasCssAlarmDisplay(@Nonnull final Object node) {
        return isDisplayUrl(node, EpicsAlarmcfgTreeNodeAttribute.CSS_ALARM_DISPLAY);
    }
    
    /**
     * Returns whether the given node has a CSS display.
     *
     * @param node the node.
     * @return <code>true</code> if the node has a display, <code>false</code> otherwise.
     */
    private boolean hasCssDisplay(@Nonnull final Object node) {
        return isDisplayUrl(node, EpicsAlarmcfgTreeNodeAttribute.CSS_DISPLAY);
    }
    
    private boolean isDisplayUrl(@Nonnull final Object node,
                                 @Nonnull final EpicsAlarmcfgTreeNodeAttribute attribute) {
        if (node instanceof IAlarmTreeNode) {
            String display = ((IAlarmTreeNode) node).getInheritedPropertyWithUrlProtocol(attribute);
            return display != null && display.matches(".+\\.css-sds"); //$NON-NLS-1$
        }
        return false;
    }
    
    /**
     * Returns whether the given node has a CSS strip chart.
     *
     * @param node the node.
     * @return <code>true</code> if the node has a strip chart, <code>false</code> otherwise.
     */
    private boolean hasCssStripChart(@Nonnull final Object node) {
        if (node instanceof IAlarmTreeNode) {
            String string = ((IAlarmTreeNode) node)
                    .getInheritedPropertyWithUrlProtocol(EpicsAlarmcfgTreeNodeAttribute.CSS_STRIP_CHART);
            // dot must not be checked for: .plt is valid and .sds-plt also
            return string != null && string.endsWith("plt"); //$NON-NLS-1$
        }
        return false;
    }
    
    /**
     * Return whether help guidance is available for the given node.
     *
     * @param node the node.
     * @return <code>true</code> if the node has a help guidance string, <code>false</code>
     *         otherwise.
     */
    private boolean hasHelpGuidance(@Nonnull final Object node) {
        return isNonEmptyString(node, EpicsAlarmcfgTreeNodeAttribute.HELP_GUIDANCE);
    }
    
    /**
     * Return whether the given node has an associated help page.
     *
     * @param node the node.
     * @return <code>true</code> if the node has an associated help page, <code>false</code>
     *         otherwise.
     */
    private boolean hasHelpPage(@Nonnull final Object node) {
        return isNonEmptyString(node, EpicsAlarmcfgTreeNodeAttribute.HELP_PAGE);
    }
    
    private boolean isNonEmptyString(@Nonnull final Object node,
                                     @Nonnull final EpicsAlarmcfgTreeNodeAttribute attribute) {
        if (node instanceof IAlarmTreeNode) {
            String string = ((IAlarmTreeNode) node).getInheritedPropertyWithUrlProtocol(attribute);
            return (string != null) && (!string.isEmpty());
        }
        return false;
    }
    
    /**
     * Adds a context menu to the tree view.
     */
    private void initializeContextMenu() {
        final MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        
        // add menu items to the context menu when it is about to show
        menuMgr.addMenuListener(new IMenuListener() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void menuAboutToShow(@Nullable final IMenuManager manager) {
                AlarmTreeView.this.fillContextMenu(manager);
            }
        });
        
        // add the context menu to the tree viewer
        final Menu contextMenu = menuMgr.createContextMenu(_viewer.getTree());
        _viewer.getTree().setMenu(contextMenu);
        
        // register the context menu for extension by other plug-ins
        getSite().registerContextMenu(menuMgr, _viewer);
    }
    
    /**
     * Refreshes this view.
     */
    public void refresh() {
        _viewer.refresh();
    }
    
    /**
     * Called when the selection of the tree changes.
     *
     * @param event the selection event.
     */
    private void selectionChanged(@Nonnull final SelectionChangedEvent event) {
        final IStructuredSelection sel = (IStructuredSelection) event.getSelection();
        _acknowledgeAction.setEnabled(containsNodeWithUnackAlarm(sel));
        final Object firstElement = sel.getFirstElement();
        _runCssAlarmDisplayAction.setEnabled(hasCssAlarmDisplay(firstElement));
        _runCssDisplayAction.setEnabled(hasCssDisplay(firstElement));
        _openCssStripChartAction.setEnabled(hasCssStripChart(firstElement));
        _showHelpGuidanceAction.setEnabled(hasHelpGuidance(firstElement));
        _showHelpPageAction.setEnabled(hasHelpPage(firstElement));
    }
    
    /**
     * Passes the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        _viewer.getControl().setFocus();
    }
    
    /**
     * Starts the connection.
     */
    private void createAndScheduleConnectionJob() {
        LOG.debug("Starting connection."); //$NON-NLS-1$
        
        final IWorkbenchSiteProgressService progressService = (IWorkbenchSiteProgressService) getSite()
                .getAdapter(IWorkbenchSiteProgressService.class);
        
        final ConnectionJob connectionJob = JobFactory.createConnectionJob(this);
        
        // Gain access to the connection
        connectionJob.addJobChangeListener(new JobChangeAdapter() {
            
            @SuppressWarnings("synthetic-access")
            @Override
            public void done(@Nullable final IJobChangeEvent event) {
                _connection = connectionJob.getConnection();
            }
        });
        
        progressService.schedule(connectionJob, 0, true);
    }
    
    /**
     * Starts a job which reads the contents of the directory in the background.
     * @param rootNode
     * @return the created and already scheduled job
     */
    @Nonnull
    public Job createAndScheduleImportInitialConfiguration(@Nonnull final IAlarmSubtreeNode rootNode) {
        LOG.debug("Start import initial configuration."); //$NON-NLS-1$
        final IWorkbenchSiteProgressService progressService = (IWorkbenchSiteProgressService) getSite()
                .getAdapter(IWorkbenchSiteProgressService.class);
        
        final Job importInitialConfigJob = JobFactory.createImportInitialConfigJob(this, rootNode);
        
        // This means updates will be queued for later application.
        _alarmListener.startUpdateProcessing();
        
        // The directory is read in the background. Until then, set the viewer's
        // input to a placeholder object.
        _viewer.setInput(new Object[] {new PendingUpdateAdapter()});
        
        // Start the directory reader job.
        progressService.schedule(importInitialConfigJob, 0, true);
        return importInitialConfigJob;
    }
    
    /**
     * Adds the given item to the modification item list that is processed on save in ldap action.
     * @param item
     */
    public void addLdapTreeModificationItem(@Nonnull final ITreeModificationItem item) {
        _ldapModificationItems.add(item);
    }
    
    @Override
    public void doSave(@Nonnull final IProgressMonitor monitor) {
        getSaveInLdapAction().run();
    }
    
    @Override
    public void doSaveAs() {
        // not yet implemented: may save to xml instead of ldap
    }
    
    @Override
    public boolean isDirty() {
        return getSaveInLdapAction().isEnabled();
    }
    
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }
    
    @Override
    public boolean isSaveOnCloseNeeded() {
        return true;
    }
    
    @Override
    public int promptToSaveOnClose() {
        // not yet implemented: provide own dialog: be able to save as xml, tell count of unsaved items
        return ISaveablePart2.DEFAULT;
    }
    
    /**
     * track changes to the tree. if an ldap sourced tree is modified and no permission to save it into ldap is given,
     * a warning appears in the message area.
     */
    private void trackModificationItemsForWarning() {
        addPropertyListener(new IPropertyListener() {
            
            @SuppressWarnings("synthetic-access")
            @Override
            public void propertyChanged(@Nonnull final Object source, final int propId) {
                if ( (propId == ISaveablePart.PROP_DIRTY) && !_saveInLdapAction.hasPermission()
                        && !_ldapModificationItems.isEmpty()) {
                    _myMessageArea.showMessage(SWT.ICON_WARNING,
                                               Messages.AlarmTreeView_Save_Title,
                                               Messages.AlarmTreeView_Save_FailHint);
                }
            }
        });
    }
    
    /**
     * This queue contains the modification items about to be stored in ldap. Additions occur at each change to the alarm tree.
     * After a save into the ldap database the queue is cleared. The state of the queue is tracked to make the enclosing view
     * behave like an editor.
     * 
     * @author jpenning
     */
    private class LdapModificationQueue extends ConcurrentLinkedQueue<ITreeModificationItem> {
        private static final long serialVersionUID = 1L;
        
        public LdapModificationQueue() {
            // Nothing to do
        }
        
        @SuppressWarnings("synthetic-access")
        @Override
        public void clear() {
            super.clear();
            getSaveInLdapAction().setEnabled(false);
            firePropertyChange(ISaveablePart.PROP_DIRTY);
        }
        
        @SuppressWarnings("synthetic-access")
        @Nonnull
        @Override
        public ITreeModificationItem remove() {
            ITreeModificationItem result = super.remove();
            if (isEmpty()) {
                getSaveInLdapAction().setEnabled(false);
                firePropertyChange(ISaveablePart.PROP_DIRTY);
            }
            return result;
        }
        
        @SuppressWarnings("synthetic-access")
        @Override
        public boolean add(@Nonnull final ITreeModificationItem arg0) {
            boolean result = super.add(arg0);
            if (result) {
                getSaveInLdapAction().setEnabled(true);
                firePropertyChange(ISaveablePart.PROP_DIRTY);
            }
            return result;
        }
    }
    
    /**
     * handles callbacks from remote commands
     */
    private class MyAlarmListener implements IAlarmService.IListener {
        
        public MyAlarmListener() {
            // nothing to do
        }
        
        @Override
        public void configurationUpdated() {
            if (AlarmPreference.ALARMSERVICE_LISTENS_TO_ALARMSERVER.getValue()) {
                showMessage(SWT.ICON_INFORMATION, Messages.AlarmTreeView_AlarmServer_ConfigurationUpdated);
            } else {
                reloadTree();
            }
        }
        
        @Override
        public void alarmServerReloaded() {
            if (AlarmPreference.ALARMSERVICE_LISTENS_TO_ALARMSERVER.getValue()) {
                reloadTree();
            } // else ignore
        }
        
        @Override
        public void alarmServerStarted() {
            showMessage(SWT.ICON_INFORMATION, Messages.AlarmTreeView_AlarmServer_Started);
        }
        
        @Override
        public void alarmServerWillStop() {
            showMessage(SWT.ICON_ERROR, Messages.AlarmTreeView_AlarmServer_WillStop);
        }
        
        private void reloadTree() {
            // this is called from a non-ui-thread so we have to enqueue it
            Display.getDefault().asyncExec(new Runnable() {
                
                @Override
                @SuppressWarnings("synthetic-access")
                public void run() {
                    showMessage(SWT.ICON_INFORMATION, Messages.AlarmTreeView_AlarmServer_Calls_For_Reload);
                    // unsaved changes are discarded without notifying the user!
                    _ldapModificationItems.clear();
                    _reloadAction.run();
                    
                }
            });
        }

        private void showMessage(final int icon, @Nonnull final String message) {
            // this is called from a non-ui-thread so we have to enqueue it
            Display.getDefault().asyncExec(new Runnable() {
                
                @SuppressWarnings("synthetic-access")
                @Override
                public void run() {
                    _myMessageArea.showMessage(icon, Messages.AlarmTreeView_AlarmServer_Title, message);
                }
            });
        }

    }

	public void select(String pvName) {
		Object object = _viewer.getInput();
		if (object instanceof IAlarmSubtreeNode) {
			List<IAlarmTreeNode> leafs = getLeafNodes(((IAlarmSubtreeNode) object).getChildren(), pvName);
			for (IAlarmTreeNode iAlarmTreeNode : leafs) {
				_viewer.expandToLevel(iAlarmTreeNode, TreeViewer.ALL_LEVELS);
			}
			if (leafs.size() > 0) {
				_viewer.setSelection(new StructuredSelection(leafs.get(0)));
			}
		}
		
	}
	
	private List<IAlarmTreeNode> getLeafNodes(List<IAlarmTreeNode> nodes, String pvName) {
		List<IAlarmTreeNode> result = new ArrayList<IAlarmTreeNode>();
		for (IAlarmTreeNode iAlarmTreeNode : nodes) {
			if (iAlarmTreeNode instanceof IAlarmSubtreeNode) {
				result.addAll(getLeafNodes(((IAlarmSubtreeNode) iAlarmTreeNode).getChildren(), pvName));
			} else if (iAlarmTreeNode instanceof IAlarmProcessVariableNode && iAlarmTreeNode.getName().equals(pvName)) {
				result.add(iAlarmTreeNode);
			}
		}
		return result;
	}
}
