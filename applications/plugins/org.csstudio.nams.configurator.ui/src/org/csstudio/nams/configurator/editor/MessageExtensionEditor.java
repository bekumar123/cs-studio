package org.csstudio.nams.configurator.editor;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.csstudio.nams.common.fachwert.RubrikTypeEnum;
import org.csstudio.nams.configurator.Messages;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.beans.MessageExtensionBean;
import org.csstudio.nams.configurator.composite.TableColumnResizeAdapter;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class MessageExtensionEditor extends
		AbstractEditor<MessageExtensionBean> {

	private static final String EDITOR_ID = "org.csstudio.nams.configurator.editor.MessageExtensionEditor"; //$NON-NLS-1$

	public static String getId() {
		return MessageExtensionEditor.EDITOR_ID;
	}

	private Text name;
	private Combo _rubrikComboEntry;
	private TableViewer tableViewer;
	private ComboViewer _rubrikComboEntryViewer;
	private FormToolkit formToolkit;
	private ScrolledForm mainForm;
	private IStructuredContentProvider contentProvider;

	@Override
	public void createPartControl(final Composite parent) {
		this.formToolkit = new FormToolkit(parent.getDisplay());
		this.mainForm = this.formToolkit.createScrolledForm(parent);
		final Composite main = this.mainForm.getBody();
		main.setBackground(parent.getBackground());
		main.setLayout(new GridLayout(1, true));
		

		// main.setLayout(new FillLayout(SWT.VERTICAL));
		{
			final Composite textFieldComp = new Composite(main, SWT.None);
			textFieldComp.setLayout(new GridLayout(2, false));
			GridDataFactory.fillDefaults().grab(true, false).applyTo(
					textFieldComp);

			{
				this.name = this.createTextEntry(textFieldComp, Messages.MessageExtensionEditor_pvName, true);

				this._rubrikComboEntryViewer = this.createComboEntry(
						textFieldComp, Messages.MessageExtensionEditor_group, true, AbstractEditor
								.getConfigurationBeanService()
								.getRubrikNamesForType(
										RubrikTypeEnum.FACILITY));
				this._rubrikComboEntry = this._rubrikComboEntryViewer
						.getCombo();
			}

			{
				final Composite tabelleUndButtonsComp = new Composite(main,
						SWT.None);
				tabelleUndButtonsComp.setLayout(new GridLayout(2, false));
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						tabelleUndButtonsComp);

				{
					final Composite tabellenComposite = new Composite(
							tabelleUndButtonsComp, SWT.NONE);
					tabellenComposite.setLayout(new GridLayout(2, false));
					GridDataFactory.fillDefaults().grab(true, true).applyTo(
							tabellenComposite);
					this.tableViewer = new TableViewer(tabellenComposite,
							SWT.FULL_SELECTION | SWT.H_SCROLL
									| SWT.V_SCROLL);
					final Table table = this.tableViewer.getTable();

					final TableViewerColumn keyColumn = new TableViewerColumn(
							this.tableViewer, SWT.NONE);
					TableColumn column = keyColumn.getColumn();
					column.setText(Messages.MessageExtensionEditor_key);
					column.setWidth(200);
					
					keyColumn.setEditingSupport(new EditingSupport(
							this.tableViewer) {

						@Override
						protected boolean canEdit(final Object element) {
							return true;
						}

						@Override
						protected CellEditor getCellEditor(final Object element) {
							final TextCellEditor editor = new TextCellEditor(
									MessageExtensionEditor.this.tableViewer
											.getTable());
							((Text) editor.getControl()).setTextLimit(128);
							
							editor.setValidator(new ICellEditorValidator() {
								
								@Override
								public String isValid(Object value) {
									if(!getWorkingCopyOfEditorInput().getMessageExtensions().containsKey(((String)value))) {
										return null;
									} else {
										return "Invalid"; //$NON-NLS-1$
									}
								}
							});
														
							return editor;
						}

						@Override
						protected Object getValue(final Object element) {
							return ((Map.Entry<?,?>)element).getKey();
						}

						@SuppressWarnings("unchecked")
						@Override
						protected void setValue(final Object element,
								final Object newKeyObject) {
							if (newKeyObject != null) {
								Map.Entry<String, String> entryElement = (Entry<String, String>) element;
								String newKey = (String) newKeyObject;

								// Remove old key
								getWorkingCopyOfEditorInput().removeMessageExtension(entryElement.getKey());

								getWorkingCopyOfEditorInput().setMessageExtension(newKey, entryElement.getValue());
							}
						}
					});
					final TableViewerColumn valueColumn = new TableViewerColumn(this.tableViewer, SWT.None);
					column = valueColumn.getColumn();
					column.setText(Messages.MessageExtensionEditor_value);
					column.setWidth(200);

					tabellenComposite.addControlListener(new TableColumnResizeAdapter(tabellenComposite, table, valueColumn.getColumn(), 300));
					
					valueColumn.setEditingSupport(new EditingSupport(
							this.tableViewer) {

						@Override
						protected boolean canEdit(final Object element) {
							return true;
						}

						@Override
						protected CellEditor getCellEditor(final Object element) {
							final TextCellEditor editor = new TextCellEditor(
									MessageExtensionEditor.this.tableViewer
											.getTable());
							((Text) editor.getControl()).setTextLimit(128);
							return editor;
						}

						@Override
						protected Object getValue(final Object element) {
							return ((Map.Entry<?,?>)element).getValue();
						}

						@SuppressWarnings("unchecked")
						@Override
						protected void setValue(final Object element,
								final Object value) {
							Map.Entry<String, String>entryElement = (Entry<String, String>) element;
							String stringValue = (String)value;
							getWorkingCopyOfEditorInput().setMessageExtension(entryElement.getKey(), stringValue);
						}
					});
					
					this.tableViewer.setLabelProvider(new ITableLabelProvider() {

								public void addListener(
										final ILabelProviderListener listener) {
								}

								public void dispose() {
								}


								@SuppressWarnings("unchecked")
								public String getColumnText(
										final Object element,
										final int columnIndex) {
									if (element instanceof Map.Entry) {
										Map.Entry<String, String> entry = (Map.Entry<String, String>) element;
										switch (columnIndex) {
										case 0:
											return entry.getKey();
										case 1:
											return entry.getValue();
										}
									}
									return null;
								}

								public boolean isLabelProperty(
										final Object element,
										final String property) {
									return false;
								}

								public void removeListener(
										final ILabelProviderListener listener) {
								}

								@Override
								public Image getColumnImage(Object element, int columnIndex) {
									return null;
								}

							});

					GridDataFactory.fillDefaults().grab(true, true).applyTo(
							this.tableViewer.getControl());

					contentProvider = new IStructuredContentProvider() {

						private List<Entry<String, String>> entries;

						@Override
						public void dispose() {
						}

						@SuppressWarnings("unchecked")
						@Override
						public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
							if (newInput != null) {
								entries = new ArrayList<Map.Entry<String, String>>(((Map<String, String>) newInput).entrySet());
							}
						}

						@Override
						public Object[] getElements(Object inputElement) {
							return (entries != null) ? entries.toArray() : null;
						}
					};
					this.tableViewer.setContentProvider(contentProvider);
					
					tableViewer.setSorter(new ViewerSorter());

					table.setHeaderVisible(true);
					table.setLinesVisible(true);
					table.setSize(400, 300);


				{
					final Composite buttonsComp = new Composite(
							tabelleUndButtonsComp, SWT.None);
					buttonsComp.setLayout(new GridLayout(1, false));
					GridDataFactory.fillDefaults().grab(false, true).applyTo(
							buttonsComp);
					{

						final Button deleteButton = this.createButtonEntry(
								buttonsComp, Messages.AlarmbearbeitergruppenEditor_delete, true, 1);
						deleteButton.addMouseListener(new MouseListener() {

							public void mouseDoubleClick(final MouseEvent e) {
							}

							public void mouseDown(final MouseEvent e) {
								final Table table = MessageExtensionEditor.this.tableViewer
										.getTable();
								int selectionIndex = table.getSelectionIndex();
								if (selectionIndex > -1) {
									TableItem[] selectedItems = table.getSelection();
									
									// disable editing cells to avoid re-insertion of deleted rows
									tableViewer.setInput(getWorkingCopyOfEditorInput().getMessageExtensions());
									
									for (final TableItem item : selectedItems) {
										Object data = item.getData();
										if(data instanceof Map.Entry && ((Map.Entry<?,?>) data).getKey() instanceof String) {
											@SuppressWarnings("unchecked")
											String messageKey = ((Map.Entry<String,?>) data).getKey();
											getWorkingCopyOfEditorInput().removeMessageExtension(messageKey);
										}
									}
									table.select(selectionIndex);
								}
							}

							public void mouseUp(final MouseEvent e) {
							}
						});

						final Button addButton = this.createButtonEntry(
								buttonsComp, Messages.MessageExtensionEditor_add, true, 1);
						addButton.addMouseListener(new MouseListener() {
							
							public void mouseDoubleClick(final MouseEvent e) {
							}
							
							public void mouseDown(final MouseEvent e) {
								int counter = 1;
								String key = Messages.MessageExtensionEditor_newEntry;
								while(getWorkingCopyOfEditorInput().getMessageExtensions().containsKey(key)) {
									key = Messages.MessageExtensionEditor_newEntry + "-" + counter; //$NON-NLS-1$
									counter++;
								}
								getWorkingCopyOfEditorInput().setMessageExtension(key, Messages.MessageExtensionEditor_newValue);
								
							}
							
							public void mouseUp(final MouseEvent e) {
							}
						});
					}

				}
			}
			}
		}
		this.initDataBinding();
		tableViewer.setInput(getWorkingCopyOfEditorInput().getMessageExtensions());
	}

	@Override
	public void setFocus() {
		this.name.setFocus();
	}

	@Override
	protected void doInit(final IEditorSite site, final IEditorInput input) {
	}

	@Override
	protected int getNumColumns() {
		return 2;
	}

	@Override
	protected void initDataBinding() {
		final DataBindingContext context = new DataBindingContext();
		
		final IObservableValue nameTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						MessageExtensionBean.PropertyNames.pvName.name());
		
		final IObservableValue rubrikTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						AlarmbearbeiterBean.AbstractPropertyNames.rubrikName
								.name());
		
		// bind observables

		context.bindValue(SWTObservables.observeText(this.name,
				SWT.Modify), nameTextObservable, null, null);
		
		context.bindValue(SWTObservables
				.observeSelection(this._rubrikComboEntry),
				rubrikTextObservable, null, null);

		// bind message extensions
		
		getWorkingCopyOfEditorInput().addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals(MessageExtensionBean.PropertyNames.messageExtensions.name())) {
					tableViewer.setInput(getWorkingCopyOfEditorInput().getMessageExtensions());
				}
				
				if(evt.getPropertyName().equals(MessageExtensionBean.PropertyNames.pvName.name())) {
					if(!isPvNameValid()) {
						name.setForeground(Display.getCurrent().getSystemColor(
								SWT.COLOR_RED));
					} else {
						name.setForeground(Display.getCurrent().getSystemColor(
								SWT.COLOR_BLACK));
					}
				}
			}
		});
	}
	
	private boolean isPvNameValid() {
		boolean result = true;
		
		MessageExtensionBean[] messageExtensionBeans = getConfigurationBeanService().getMessageExtensionBeans();
		for (MessageExtensionBean messageExtensionBean : messageExtensionBeans) {
			if(messageExtensionBean.getPvName().equalsIgnoreCase(name.getText()) && messageExtensionBean.getID() != getWorkingCopyOfEditorInput().getID()) {
				result = false;
				break;
			}
		}
		
		return result;
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		if(isPvNameValid()) {
			super.doSave(monitor);
		} else {
			final MessageBox messageBox = new MessageBox(PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getShell());
			messageBox.setText("Speichern fehlgeschlagen");
			String message = "Es existiert bereits eine Konfiguration für den PV-Namen " + name.getText() + ".";
			messageBox.setMessage(message);
			messageBox.open();
		}
	}
}
