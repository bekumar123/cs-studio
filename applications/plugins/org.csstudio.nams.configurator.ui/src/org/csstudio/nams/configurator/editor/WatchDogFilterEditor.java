
package org.csstudio.nams.configurator.editor;

import java.util.Iterator;

import org.csstudio.nams.common.fachwert.RubrikTypeEnum;
import org.csstudio.nams.configurator.Messages;
import org.csstudio.nams.configurator.actions.BeanToEditorId;
import org.csstudio.nams.configurator.beans.AlarmTopicFilterAction;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterFilterAction;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterGruppenBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeitergruppenFilterAction;
import org.csstudio.nams.configurator.beans.AlarmtopicBean;
import org.csstudio.nams.configurator.beans.FilterAction;
import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.beans.IReceiverBean;
import org.csstudio.nams.configurator.beans.MessageTemplateBean;
import org.csstudio.nams.configurator.beans.WatchDogFilterBean;
import org.csstudio.nams.configurator.beans.filters.JunctorConditionForFilterTreeBean;
import org.csstudio.nams.configurator.beans.filters.NotConditionForFilterTreeBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.FilterActionType;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class WatchDogFilterEditor extends AbstractEditor<WatchDogFilterBean> {

	private static final String EDITOR_ID = "org.csstudio.nams.configurator.editor.WatchDogFilterEditor"; //$NON-NLS-1$

	public static String getId() {
		return WatchDogFilterEditor.EDITOR_ID;
	}

	private WatchDogFilterTreeContentProvider filterTreeContentProvider;
	private Text _idTextEntry;
	private Text _nameTextEntry;
	private Combo _rubrikComboEntry;
	private Text _defaultMessageTextEntry;
	private ComboViewer _rubrikComboEntryViewer;
	
	private TreeViewer filterConditionsTreeViewer;

	private FormToolkit formToolkit;

	private ScrolledForm mainForm;

	private TableViewer actionTableViewer;

	public WatchDogFilterEditor() {
		super();

	}

	@Override
	public void createPartControl(final Composite parent) {
		this.filterTreeContentProvider = new WatchDogFilterTreeContentProvider();

		this.formToolkit = new FormToolkit(parent.getDisplay());
		this.mainForm = this.formToolkit.createScrolledForm(parent);
		final Composite outerFormMain = this.mainForm.getBody();
		outerFormMain.setBackground(parent.getBackground());
		outerFormMain.setLayout(new GridLayout(1, false));

		final Composite main = new Composite(outerFormMain, SWT.NONE);
		main.setLayout(new GridLayout(this.NUM_COLUMNS, false));
		this.addSeparator(main);
		_idTextEntry = this.createTextEntry(main, "ID", false); //$NON-NLS-1$
		this._nameTextEntry = this.createTextEntry(main, Messages.FilterEditor_name, true);
		this._rubrikComboEntryViewer = this.createComboEntry(main, Messages.FilterEditor_category,
				true, AbstractEditor.getConfigurationBeanService()
						.getRubrikNamesForType(RubrikTypeEnum.FILTER));
		this._rubrikComboEntry = this._rubrikComboEntryViewer.getCombo();
		this.addSeparator(main);

		final MessageTemplateBean[] messageTemplates = AbstractEditor.configurationBeanService
				.getMessageTemplates();
		final String[] templateNames = new String[messageTemplates.length];
		final String[] templateContent = new String[messageTemplates.length];
		for (int i = 0; i < templateNames.length; i++) {
			templateNames[i] = messageTemplates[i].getName();
			templateContent[i] = messageTemplates[i].getMessage();
		}
		final ComboViewer templateComboViewer = this.createComboEntry(main,
				Messages.FilterEditor_templates, false, templateNames);
		final Button addTemplateButton = this.createButtonEntry(main,
				Messages.FilterEditor_add_template, true, 2);
		addTemplateButton.addMouseListener(new MouseListener() {

			@Override
            public void mouseDoubleClick(final MouseEvent e) {
			    // Not used yet
			}

			@Override
            public void mouseDown(final MouseEvent e) {
			    // Not used yet
			}

			@Override
            public void mouseUp(final MouseEvent e) {
				WatchDogFilterEditor.this._defaultMessageTextEntry
						.append(templateContent[templateComboViewer.getCombo()
								.getSelectionIndex()]);
			}

		});
		this._defaultMessageTextEntry = this.createDescriptionTextEntry(main,
				Messages.FilterEditor_default_message);

		{
			final Composite treeAndButtonsComposite = new Composite(outerFormMain,
					SWT.None);
			treeAndButtonsComposite.setLayout(new GridLayout(1, false));
			GridDataFactory.fillDefaults().grab(true, true).applyTo(
					treeAndButtonsComposite);
			new Label(treeAndButtonsComposite, SWT.None).setText(Messages.FilterEditor_filterconditions);

			this.filterConditionsTreeViewer = this.createTreeViewer(
					treeAndButtonsComposite,
					this.filterTreeContentProvider);
			filterConditionsTreeViewer.setInput(this.getWorkingCopyOfEditorInput());
			filterConditionsTreeViewer.expandAll();
		}
		
		this.createFilterActionWidget(outerFormMain);

		this.initDND();
		
		this.initDataBinding();
		
	}
		
	private TreeViewer createTreeViewer(Composite parent, final WatchDogFilterTreeContentProvider contentProvider) {
		final TreeViewer result = new TreeViewer(parent, SWT.MULTI
				| SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		final Tree filterTree = result.getTree();
		GridDataFactory.fillDefaults().grab(true, true)
				.applyTo(result.getControl());

		final GridData treeLayout = (GridData) filterTree.getLayoutData();
		treeLayout.minimumHeight = 100;
		treeLayout.minimumWidth = 300;

		result.setContentProvider(contentProvider);
		result.setLabelProvider(new FilterTreeLabelProvider());

		final NewJunctorAction newAndAction = new NewJunctorAction(
				result, JunctorConditionType.AND);
		final NewJunctorAction newOrAction = new NewJunctorAction(
				result, JunctorConditionType.OR);
		final NewNotAction newNotAction = new NewNotAction(result, contentProvider);

		result.addSelectionChangedListener(newAndAction);
		result.addSelectionChangedListener(newOrAction);
		result.addSelectionChangedListener(newNotAction);

		result.expandAll();

		final MenuManager menuManager = new MenuManager();
		menuManager.add(newAndAction);
		menuManager.add(newOrAction);
		menuManager.add(newNotAction);

		filterTree.setMenu(menuManager.createContextMenu(filterTree));

		filterTree.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				try {
					ConfigurationEditorInput editorInput;
					final IStructuredSelection selection = (IStructuredSelection) result
							.getSelection();
					IConfigurationBean filterBedingung = (IConfigurationBean) selection
							.getFirstElement();

					if (filterBedingung instanceof NotConditionForFilterTreeBean) {
						NotConditionForFilterTreeBean not = (NotConditionForFilterTreeBean) filterBedingung;
						filterBedingung = not.getFilterbedingungBean();
					}

					if (!(filterBedingung instanceof JunctorConditionForFilterTreeBean)) {
						editorInput = new ConfigurationEditorInput(
								filterBedingung);

						final IWorkbenchPage activePage = PlatformUI
								.getWorkbench().getActiveWorkbenchWindow()
								.getActivePage();
						final String editorId = BeanToEditorId.getEnumForClass(
								FilterbedingungBean.class).getEditorId();

						activePage.openEditor(editorInput, editorId);
					}
				} catch (final PartInitException pie) {
					pie.printStackTrace();
				}
			}

			public void mouseDown(final MouseEvent e) {
				// Not used yet
			}

			public void mouseUp(final MouseEvent e) {
				// Not used yet
			}
		});

		// Create buttons
		final Button button = new Button(parent, SWT.PUSH);
		button.setText(Messages.FilterEditor_remove_filtercondition);
		button.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				// Not used yet
			}

			@Override
			@SuppressWarnings("unchecked")//$NON-NLS-1$
			public void mouseDown(final MouseEvent e) {
				final TreeSelection selection = (TreeSelection) result
						.getSelection();
				for (final Iterator<FilterbedingungBean> iter = selection
						.iterator(); iter.hasNext();) {
					final FilterbedingungBean bean2remove = iter.next();
					final Object parent = contentProvider.getParent(bean2remove);
					JunctorConditionForFilterTreeBean junctorParent = null;
					if (parent instanceof NotConditionForFilterTreeBean) {
						junctorParent = (JunctorConditionForFilterTreeBean) ((NotConditionForFilterTreeBean) parent)
								.getFilterbedingungBean();
					}
					if (parent instanceof JunctorConditionForFilterTreeBean) {
						junctorParent = (JunctorConditionForFilterTreeBean) parent;
					}
					if (junctorParent != null) {
						junctorParent.removeOperand(bean2remove);
						result.refresh();
						WatchDogFilterEditor.this.updateBeanAndFireEvent();
					}

				}

			}

			@Override
			public void mouseUp(final MouseEvent e) {
				// Not used yet
			}
		});

		return result;
	}
	

	@Override
	public void onBeanInsert(final IConfigurationBean bean) {
		// FIXME synchronize bean and beanClone
		if (this.filterConditionsTreeViewer != null) {
			this.filterConditionsTreeViewer.refresh();
		}
		// if (!isDirty()) {
		// afterSafe();
		// }
		super.onBeanInsert(bean);
	}

	@Override
	public void onBeanUpdate(final IConfigurationBean bean) {
		// FIXME synchronize bean and beanClone
		if (this.filterConditionsTreeViewer != null) {
			this.filterConditionsTreeViewer.refresh();
		}
		// if (!isDirty()) {
		// afterSafe();
		// }
		super.onBeanUpdate(bean);
	}

	@Override
	public void setFocus() {
		this._nameTextEntry.setFocus();
	}

	@Override
	protected void afterSafe() {
		// Sonderfall für die Filterbean, da sie einen Baum von unterbeans
		// enthält.
		this.getWorkingCopyOfEditorInput().updateState(
				this.getOriginalEditorInput());
		
		this.actionTableViewer.setInput(this.getWorkingCopyOfEditorInput()
				.getActions().toArray());
		
		this.filterConditionsTreeViewer.setInput(this.getWorkingCopyOfEditorInput());
		this.filterConditionsTreeViewer.expandAll();
	}

	@Override
	protected void doInit(final IEditorSite site, final IEditorInput input) {
	    // Not used yet
	}

	@Override
	protected int getNumColumns() {
		return 2;
	}

	@Override
	protected void initDataBinding() {
		final DataBindingContext context = new DataBindingContext();

		final IObservableValue idTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						FilterBean.PropertyNames.filterID.name());
		
		final IObservableValue nameTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						FilterBean.PropertyNames.name.name());

		final IObservableValue descriptionTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						FilterBean.PropertyNames.defaultMessage.name());

		// IObservableTree filterConditionsObservable = BeansObservables
		// .observeList(context.getValidationRealm(), this.beanClone,
		// FilterBean.PropertyNames.conditions.name());

		final IObservableValue rubrikTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						FilterBean.AbstractPropertyNames.rubrikName.name());

		// bind observables
		context.bindValue(SWTObservables.observeText(this._idTextEntry,
				SWT.Modify), idTextObservable, null, null);
		
		context.bindValue(SWTObservables.observeText(this._nameTextEntry,
				SWT.Modify), nameTextObservable, new UpdateValueStrategy() {

			@Override
			protected IValidator createValidator(final Object fromType,
					final Object toType) {
				// TODO Validate FilterMessage Variables here
				return super.createValidator(fromType, toType);
			}

		}, null);

		context.bindValue(SWTObservables.observeText(
				this._defaultMessageTextEntry, SWT.Modify),
				descriptionTextObservable, null, null);
		// IObservableTree observeItems = SWTObservables
		// .observeItems(filterConditionsTreeViewer.getTree());
		// context.bindList(observeItems, filterConditionsObservable, null,
		// null);

		context.bindValue(SWTObservables
				.observeSelection(this._rubrikComboEntry),
				rubrikTextObservable, null, null);
	}

	protected void updateBeanAndFireEvent() {
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	private void createFilterActionWidget(final Composite outerFormMain) {
		this.actionTableViewer = new TableViewer(outerFormMain,
				SWT.FULL_SELECTION);

		GridDataFactory.fillDefaults().grab(true, true).applyTo(
				this.actionTableViewer.getControl());

		final GridData treeLayout = (GridData) this.actionTableViewer
				.getTable().getLayoutData();
		treeLayout.minimumHeight = 100;
		treeLayout.minimumWidth = 300;

		this.actionTableViewer.getTable().setHeaderVisible(true);
		this.actionTableViewer.getTable().setLinesVisible(true);
		this.actionTableViewer.setContentProvider(new ArrayContentProvider());

		final String[] titles = { Messages.FilterEditor_receiver, Messages.FilterEditor_alarm_action, Messages.FilterEditor_message };
		final int[] bounds = { 150, 150, 200 };

		final TableViewerColumn[] tableViewerColumns = new TableViewerColumn[3];

		for (int i = 0; i < titles.length; i++) {
			tableViewerColumns[i] = new TableViewerColumn(
					this.actionTableViewer, SWT.LEFT);

			final TableColumn column = tableViewerColumns[i].getColumn();
			column.setText(titles[i]);
			column.setWidth(bounds[i]);
			column.setResizable(true);
		}

		// Empfänger
		tableViewerColumns[0].setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				return ((FilterAction) element).getEmpfaengerName();
			}
		});

		// Alarmaktion
		tableViewerColumns[1].setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final FilterActionType type = ((FilterAction) element)
						.getFilterActionType();
				if (type != null) {
					return type.getDescription();
				}
				return Messages.FilterEditor_please_select;
			}
		});
		tableViewerColumns[1].setEditingSupport(new EditingSupport(
				this.actionTableViewer) {

			@Override
			protected boolean canEdit(final Object element) {
				if (element instanceof AlarmTopicFilterAction) {
					return false;
				}
				return true;
			}

			@Override
			protected CellEditor getCellEditor(final Object element) {
				final FilterActionType[] types = ((FilterAction) element)
						.getFilterActionTypeValues();
				final String[] strings = new String[types.length];
				for (int i = 0; i < types.length; i++) {
					strings[i] = types[i].getDescription();
				}
				return new ComboBoxCellEditor(
						WatchDogFilterEditor.this.actionTableViewer.getTable(),
						strings, SWT.READ_ONLY);
			}

			@Override
			protected Object getValue(final Object element) {
				final FilterActionType type = ((FilterAction) element)
						.getFilterActionType();
				if (type != null) {
					final FilterActionType[] types = ((FilterAction) element)
							.getFilterActionTypeValues();
					for (int i = 0; i < types.length; i++) {
						if (type.equals(types[i])) {
							return i;
						}
					}
				}
				return 0;
			}

			@Override
			protected void setValue(final Object element, final Object value) {
				final FilterActionType[] types = ((FilterAction) element)
						.getFilterActionTypeValues();
				((FilterAction) element).setType(types[((Integer) value)
						.intValue()]);
				WatchDogFilterEditor.this.actionTableViewer.refresh();
				WatchDogFilterEditor.this.firePropertyChange(IEditorPart.PROP_DIRTY);
			}

		});

		// Nachricht
		tableViewerColumns[2].setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				return ((FilterAction) element).getMessage();
			}
		});
		tableViewerColumns[2].setEditingSupport(new EditingSupport(
				this.actionTableViewer) {
			private TextCellEditor textEditor;

			@Override
			protected boolean canEdit(final Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(final Object element) {
				this.textEditor = new TextCellEditor(
						WatchDogFilterEditor.this.actionTableViewer.getTable());
				this.textEditor.setValidator(new ICellEditorValidator() {
					public String isValid(final Object value) {
						// TODO (gs)validator anpassen

						return null;
					}
				});
				((Text) this.textEditor.getControl()).setTextLimit(1024);
				return this.textEditor;
			}

			@Override
			protected Object getValue(final Object element) {
				return ((FilterAction) element).getMessage();
			}

			@Override
			protected void setValue(final Object element, final Object value) {
				if (this.textEditor.getErrorMessage() == null) {
					((FilterAction) element).setMessage((String) value);
				} else {
					((FilterAction) element).setMessage(this.textEditor
							.getErrorMessage());
				}
				WatchDogFilterEditor.this.actionTableViewer.refresh();
				WatchDogFilterEditor.this.firePropertyChange(IEditorPart.PROP_DIRTY);
			}

		});

		// Buttons
		Composite buttonComposite = new Composite(outerFormMain, SWT.NONE);
		buttonComposite.setLayout(new RowLayout());
		// delete
		Button deleteButton = new Button(buttonComposite, SWT.PUSH);
		deleteButton.setText(Messages.FilterEditor_remove_action);
		deleteButton.addMouseListener(new MouseListener() {
			
		    @Override
            public void mouseDoubleClick(MouseEvent e) {
		        // Not used yet
			}

			@Override
            public void mouseDown(MouseEvent e) {
				FilterBean bean = WatchDogFilterEditor.this
						.getWorkingCopyOfEditorInput();
				FilterAction action = (FilterAction) ((StructuredSelection) WatchDogFilterEditor.this.actionTableViewer
						.getSelection()).getFirstElement();
				bean.removeAction(action);
				WatchDogFilterEditor.this.actionTableViewer.setInput(WatchDogFilterEditor.this
						.getWorkingCopyOfEditorInput().getActions().toArray());
				WatchDogFilterEditor.this.firePropertyChange(IEditorPart.PROP_DIRTY);
			}

			@Override
            public void mouseUp(MouseEvent e) {
			    // Not used yet
			}
		});
		// up
		Button upButton = new Button(buttonComposite, SWT.PUSH);
		upButton.setText(Messages.FilterEditor_move_action_up);
		upButton.addMouseListener(new MouseListener() {
			
		    @Override
            public void mouseDoubleClick(MouseEvent e) {
			    // Not used yet
			}

			@Override
            public void mouseDown(MouseEvent e) {
				FilterBean bean = WatchDogFilterEditor.this
						.getWorkingCopyOfEditorInput();
				FilterAction action = (FilterAction) ((StructuredSelection) WatchDogFilterEditor.this.actionTableViewer
						.getSelection()).getFirstElement();
				bean.moveUpAction(action);
				WatchDogFilterEditor.this.actionTableViewer.setInput(WatchDogFilterEditor.this
						.getWorkingCopyOfEditorInput().getActions().toArray());
				WatchDogFilterEditor.this.firePropertyChange(IEditorPart.PROP_DIRTY);
			}

			@Override
            public void mouseUp(MouseEvent e) {
			    // Not used yet
			}
		});
		// down
		Button downButton = new Button(buttonComposite, SWT.PUSH);
		downButton.setText(Messages.FilterEditor_move_action_down);
		downButton.addMouseListener(new MouseListener() {
			
		    @Override
            public void mouseDoubleClick(MouseEvent e) {
			    // Not used yet
			}

			@Override
            public void mouseDown(MouseEvent e) {
				FilterBean bean = WatchDogFilterEditor.this
						.getWorkingCopyOfEditorInput();
				FilterAction action = (FilterAction) ((StructuredSelection) WatchDogFilterEditor.this.actionTableViewer
						.getSelection()).getFirstElement();
				bean.moveDownAction(action);
				WatchDogFilterEditor.this.actionTableViewer.setInput(WatchDogFilterEditor.this
						.getWorkingCopyOfEditorInput().getActions().toArray());
				WatchDogFilterEditor.this.firePropertyChange(IEditorPart.PROP_DIRTY);
			}

			@Override
            public void mouseUp(MouseEvent e) {
			    // Not used yet
			}
		});
				

		this.actionTableViewer.setInput(this.getWorkingCopyOfEditorInput()
				.getActions().toArray());

	}

	private void initDND() {
		ViewerDropAdapter startViewerDropAdapter = new TreeViewerDropAdapter(this.filterConditionsTreeViewer);
		this.filterConditionsTreeViewer.addDropSupport(DND.DROP_LINK,
				new Transfer[] { LocalSelectionTransfer.getTransfer() },
				startViewerDropAdapter);
	
		this.actionTableViewer.addDropSupport(DND.DROP_LINK,
				new Transfer[] { LocalSelectionTransfer.getTransfer() },
				new ViewerDropAdapter(this.actionTableViewer) {
	
					@Override
					public void dragEnter(final DropTargetEvent event) {
						event.detail = DND.DROP_LINK;
						super.dragEnter(event);
					}
	
					@Override
					public boolean performDrop(final Object data) {
						boolean result = false;
						final IStructuredSelection selection = (IStructuredSelection) data;
						final Object selectedObject = selection
								.getFirstElement();
						FilterAction action = null;
	
						if (selectedObject instanceof AlarmbearbeiterBean) {
							action = new AlarmbearbeiterFilterAction();
						} else if (selectedObject instanceof AlarmbearbeiterGruppenBean) {
							action = new AlarmbearbeitergruppenFilterAction();
						} else if (selectedObject instanceof AlarmtopicBean) {
							action = new AlarmTopicFilterAction();
						}
						if (action != null) {
							action.setReceiver((IReceiverBean) selectedObject);
							WatchDogFilterEditor.this.getWorkingCopyOfEditorInput()
									.addFilterAction(action);
							WatchDogFilterEditor.this.actionTableViewer
									.setInput(WatchDogFilterEditor.this
											.getWorkingCopyOfEditorInput()
											.getActions().toArray());
							result = true;
							WatchDogFilterEditor.this
									.firePropertyChange(IEditorPart.PROP_DIRTY);
						}
						return result;
					}
	
					@Override
					public boolean validateDrop(final Object target,
							final int operation, final TransferData transferType) {
						boolean result = false;
						final IStructuredSelection selection = (IStructuredSelection) LocalSelectionTransfer
								.getTransfer().getSelection();
						final Object selectedElement = selection
								.getFirstElement();
						if ((selectedElement instanceof AlarmbearbeiterBean)
								|| (selectedElement instanceof AlarmbearbeiterGruppenBean)
								|| (selectedElement instanceof AlarmtopicBean)) {
							result = true;
						}
						return result;
					}
	
				});
	}
	
	private class TreeViewerDropAdapter extends ViewerDropAdapter {

		
		private final TreeViewer treeViewer;

		public TreeViewerDropAdapter(TreeViewer viewer) {
			super(viewer);
			treeViewer = viewer;
		}
		
		@Override
		public void dragEnter(final DropTargetEvent event) {
			event.detail = DND.DROP_LINK;
			super.dragEnter(event);
		}

		@Override
		public boolean performDrop(final Object data) {
			final Object target = this.getCurrentTarget();
			final IStructuredSelection selection = (IStructuredSelection) data;
			final FilterbedingungBean bean = (FilterbedingungBean) selection
					.getFirstElement();
			boolean result = false;
			if (target instanceof JunctorConditionForFilterTreeBean) {
				final JunctorConditionForFilterTreeBean targetBean = (JunctorConditionForFilterTreeBean) target;
				targetBean.addOperand(bean);
				result = true;
			} else if (target instanceof NotConditionForFilterTreeBean) {

				final NotConditionForFilterTreeBean targetBean = (NotConditionForFilterTreeBean) target;
				final FilterbedingungBean filterbedingungBean = targetBean
						.getFilterbedingungBean();
				if (filterbedingungBean instanceof JunctorConditionForFilterTreeBean) {
					final JunctorConditionForFilterTreeBean junctorFilterbedingungBean = (JunctorConditionForFilterTreeBean) filterbedingungBean;
					junctorFilterbedingungBean.addOperand(bean);
					result = true;
				}
			}
			treeViewer.refresh();
			if (result) {
				treeViewer.expandToLevel(bean, 0);
				treeViewer.setSelection(new StructuredSelection(bean));
				WatchDogFilterEditor.this.updateBeanAndFireEvent();
			}
			return result;
		}

		@Override
		public boolean validateDrop(final Object target,
				final int operation, final TransferData transferType) {
			boolean result = false;
			if ((target instanceof JunctorConditionForFilterTreeBean)
					|| (target instanceof NotConditionForFilterTreeBean)) {
				final IStructuredSelection selection = (IStructuredSelection) LocalSelectionTransfer
						.getTransfer().getSelection();
				if (selection.getFirstElement() instanceof FilterbedingungBean) {
					result = true;
				}
			}
			return result;
		}
	}

	private class NewJunctorAction extends Action implements
			ISelectionChangedListener {
		private JunctorConditionForFilterTreeBean _selectedBean;
		private final JunctorConditionType _type;
		private final TreeViewer treeViewer;
	
		private NewJunctorAction(TreeViewer treeViewer, final JunctorConditionType type) {
			this.treeViewer = treeViewer;
			this._type = type;
		}
	
		@Override
		public String getText() {
			return Messages.FilterEditor_add + this._type.name();
		}
	
		@Override
		public void run() {
			final JunctorConditionForFilterTreeBean node = new JunctorConditionForFilterTreeBean();
			node.setJunctorConditionType(this._type);
			final boolean added = this._selectedBean.addOperand(node);
			WatchDogFilterEditor.this.filterConditionsTreeViewer.refresh();
			if (added) {
				treeViewer.expandToLevel(node, 0);
				treeViewer.setSelection(new StructuredSelection(node));
				WatchDogFilterEditor.this.updateBeanAndFireEvent();
			}
		}
	
		@Override
	    public void selectionChanged(final SelectionChangedEvent event) {
			final IStructuredSelection selection = (IStructuredSelection) event
					.getSelection();
			final Object element = selection.getFirstElement();
			this._selectedBean = null;
			if (element instanceof JunctorConditionForFilterTreeBean) {
				this._selectedBean = (JunctorConditionForFilterTreeBean) element;
				this.setEnabled(true);
			} else if (element instanceof NotConditionForFilterTreeBean) {
				final NotConditionForFilterTreeBean notBean = (NotConditionForFilterTreeBean) element;
				if (notBean.getFilterbedingungBean() instanceof JunctorConditionForFilterTreeBean) {
					this._selectedBean = (JunctorConditionForFilterTreeBean) notBean
							.getFilterbedingungBean();
					this.setEnabled(true);
				} else {
					this.setEnabled(false);
				}
			} else {
				this.setEnabled(false);
			}
		}
	}

	private class NewNotAction extends Action implements
			ISelectionChangedListener {
		private FilterbedingungBean selectedBean;
		private boolean not;
		private final FilterTreeContentProvider contentProvider;
		private final TreeViewer treeViewer;
		
		public NewNotAction(TreeViewer treeViewer, FilterTreeContentProvider contentProvider) {
			this.treeViewer = treeViewer;
			this.contentProvider = contentProvider;
		}
	
		@Override
		public void run() {
			final FilterbedingungBean parent = (FilterbedingungBean) contentProvider.getParent(this.selectedBean);
			FilterbedingungBean newBean = null;
			if (parent != null) {
				JunctorConditionForFilterTreeBean junction = null;
				if (parent instanceof NotConditionForFilterTreeBean) {
					junction = (JunctorConditionForFilterTreeBean) ((NotConditionForFilterTreeBean) parent)
							.getFilterbedingungBean();
				} else if (parent instanceof JunctorConditionForFilterTreeBean) {
					junction = (JunctorConditionForFilterTreeBean) parent;
				}
				if (this.not) {
					final NotConditionForFilterTreeBean notBean = new NotConditionForFilterTreeBean();
					newBean = notBean;
					junction.removeOperand(this.selectedBean);
					notBean.setFilterbedingungBean(this.selectedBean);
					junction.addOperand(notBean);
				} else {
					junction.removeOperand(this.selectedBean);
					final NotConditionForFilterTreeBean notBean = (NotConditionForFilterTreeBean) this.selectedBean;
					final FilterbedingungBean filterbedingungBean = notBean
							.getFilterbedingungBean();
					newBean = filterbedingungBean;
					junction.addOperand(filterbedingungBean);
				}
				WatchDogFilterEditor.this.updateBeanAndFireEvent();
			}
	
			treeViewer.refresh();
			if (newBean != null) {
				treeViewer.expandToLevel(
						newBean, 0);
				treeViewer.setSelection(new StructuredSelection(newBean));
			}
	
		}
	
		@Override
	    public void selectionChanged(final SelectionChangedEvent event) {
			final IStructuredSelection selection = (IStructuredSelection) event
					.getSelection();
			final Object element = selection.getFirstElement();
			this.setEnabled(true);
			if (element instanceof FilterbedingungBean) {
				this.selectedBean = (FilterbedingungBean) element;
				if (!(element instanceof NotConditionForFilterTreeBean)) {
					this.setText(Messages.FilterEditor_add_not);
					this.not = true;
				} else {
					this.setText(Messages.FilterEditor_remove_not);
					this.not = false;
				}
			}
		}
	}
	
	private FilterbedingungBean getParentForFilterConditionBeanRecursive(FilterbedingungBean recursiveParent, FilterbedingungBean bean) {
		FilterbedingungBean result = null;
		
		if(recursiveParent instanceof JunctorConditionForFilterTreeBean) {
			for (FilterbedingungBean childBean : ((JunctorConditionForFilterTreeBean) recursiveParent).getOperands()) {
				if(bean.equals(childBean)) {
					result = recursiveParent;
					break;
				}
			}
		}
		else if(recursiveParent instanceof NotConditionForFilterTreeBean) {
			FilterbedingungBean otherBean = ((NotConditionForFilterTreeBean) recursiveParent).getFilterbedingungBean();
			if(bean.equals(otherBean)) {
				result = recursiveParent;
			}
		}
		return result;
	}
}