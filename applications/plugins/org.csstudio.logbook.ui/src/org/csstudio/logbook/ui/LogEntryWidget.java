/**
 * 
 */
package org.csstudio.logbook.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.csstudio.apputil.ui.swt.Screenshot;
import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.LogbookBuilder;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.logbook.Tag;
import org.csstudio.logbook.TagBuilder;
import org.csstudio.logbook.util.LogEntryUtil;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.wb.swt.ResourceManager;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

/**
 * @author shroffk
 * 
 */
public class LogEntryWidget extends Composite {

	private boolean editable;
	// This serves as the model for the complete logEntry that is simply being
	// displayed.
	private LogEntry logEntry;
	// This serves as the model behind the new entry being created to be written
	// to the logbook service
	private LogEntryBuilder logEntryBuilder;
	private LogbookClient logbookClient;
	// List of all the possible logbooks and tags which may be added to a
	// logEntry.
	private java.util.List<String> logbookNames;
	private java.util.List<String> tagNames;

	private Text text;
	private Text textDate;
	private Text textOwner;
	private Button btnEnableEdit;
	private List logbookList;
	private List tagList;

	protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);
	private Button btnSubmit;
	private Button btnAddLogbook;
	private Button btnAddTags;
	final private FormData empty;
	private Label label;
	private CTabItem tbtmAttachments;
	private CTabFolder tabFolder;
	private Composite tbtmAttachmentsComposite;
	private ImageStackWidget imageStackWidget;
	private Button btnAddImage;
	private Button btnAddScreenshot;
	private Button btnCSSWindow;

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	public LogEntryWidget(final Composite parent, int style) {
		super(parent, style);
		final Composite logEntryWidget = this;
		setLayout(new FormLayout());

		Label lblDate = new Label(this, SWT.NONE);
		FormData fd_lblDate = new FormData();
		fd_lblDate.top = new FormAttachment(0, 5);
		fd_lblDate.left = new FormAttachment(0, 5);
		lblDate.setLayoutData(fd_lblDate);
		lblDate.setText("Date:");

		textDate = new Text(this, SWT.NONE);
		textDate.setEditable(false);
		FormData fd_textDate = new FormData();
		fd_textDate.top = new FormAttachment(0, 5);
		fd_textDate.left = new FormAttachment(lblDate, 5);
		textDate.setLayoutData(fd_textDate);

		text = new Text(this, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		FormData fd_text = new FormData();
		fd_text.right = new FormAttachment(70);
		fd_text.top = new FormAttachment(lblDate, 10, SWT.BOTTOM);
		fd_text.left = new FormAttachment(0, 5);
		text.setLayoutData(fd_text);

		label = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_label = new FormData();
		fd_label.top = new FormAttachment(0, 2);
		fd_label.bottom = new FormAttachment(100, -2);
		fd_label.left = new FormAttachment(text, 2);
		label.setLayoutData(fd_label);

		Label lblOwner = new Label(this, SWT.NONE);
		FormData fd_lblOwner = new FormData();
		fd_lblOwner.top = new FormAttachment(0, 5);
		fd_lblOwner.left = new FormAttachment(label, 2);
		lblOwner.setLayoutData(fd_lblOwner);
		lblOwner.setText("Owner:");

		textOwner = new Text(this, SWT.BORDER);
		FormData fd_textOwner = new FormData();
		fd_textOwner.top = new FormAttachment(0, 5);
		fd_textOwner.right = new FormAttachment(100, -5);
		fd_textOwner.left = new FormAttachment(lblOwner, 2);
		textOwner.setLayoutData(fd_textOwner);

		btnSubmit = new Button(this, SWT.NONE);
		FormData fd_btnSubmit = new FormData();
		fd_btnSubmit.right = new FormAttachment(100, -5);
		fd_btnSubmit.bottom = new FormAttachment(100, -5);
		fd_btnSubmit.left = new FormAttachment(label, 2);
		btnSubmit.setLayoutData(fd_btnSubmit);
		btnSubmit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO submit the logEntry to the logbook service
			}
		});
		btnSubmit.setEnabled(false);
		btnSubmit.setText("Submit");
		btnSubmit.setEnabled(true);

		btnEnableEdit = new Button(this, SWT.CHECK);
		FormData fd_btnEnableEdit = new FormData();
		fd_btnEnableEdit.bottom = new FormAttachment(btnSubmit, -5);
		fd_btnEnableEdit.left = new FormAttachment(label, 2);
		fd_btnEnableEdit.right = new FormAttachment(100, -5);
		btnEnableEdit.setLayoutData(fd_btnEnableEdit);
		btnEnableEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setEditable(btnEnableEdit.getSelection());
			}
		});
		btnEnableEdit.setText("Edit Entry");
		btnEnableEdit.setSelection(editable);

		logbookList = new List(this, SWT.BORDER | SWT.V_SCROLL);
		FormData fd_logbookList = new FormData();
		fd_logbookList.right = new FormAttachment(100, -5);
		fd_logbookList.top = new FormAttachment(lblDate, 10, SWT.BOTTOM);
		fd_logbookList.left = new FormAttachment(label, 2);
		logbookList.setLayoutData(fd_logbookList);

		btnAddLogbook = new Button(this, SWT.NONE);
		btnAddLogbook.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Open a dialog which allows users to select logbooks
				StringListSelectionDialog dialog = new StringListSelectionDialog(
						parent.getShell(), logbookNames, LogEntryUtil
								.getLogbookNames(logEntryBuilder.build()),
						"Add Logbooks");
				if (dialog.open() == IDialogConstants.OK_ID) {
					Collection<LogbookBuilder> newLogbooks = new ArrayList<LogbookBuilder>();
					for (String logbookName : dialog.getSelectedValues()) {
						newLogbooks.add(LogbookBuilder.logbook(logbookName));
					}
					// logEntryBuilder.setLogbooks(newLogbooks);
					// updateWidget();
					logbookList.setItems(dialog.getSelectedValues().toArray(
							new String[dialog.getSelectedValues().size()]));
					parent.layout();
				}
			}
		});
		btnAddLogbook.setImage(ResourceManager.getPluginImage(
				"org.csstudio.logbook.ui", "icons/logbook-16.png"));
		FormData fd_btnAddLogbook = new FormData();
		fd_btnAddLogbook.top = new FormAttachment(logbookList, 5);
		fd_btnAddLogbook.left = new FormAttachment(label, 2);
		fd_btnAddLogbook.right = new FormAttachment(100, -5);
		btnAddLogbook.setLayoutData(fd_btnAddLogbook);
		btnAddLogbook.setText("Add Logbook");

		tagList = new List(this, SWT.BORDER);
		FormData fd_tagList = new FormData();
		fd_tagList.top = new FormAttachment(btnAddLogbook, 5);
		fd_tagList.right = new FormAttachment(100, -5);
		fd_tagList.left = new FormAttachment(label, 2);
		tagList.setLayoutData(fd_tagList);

		btnAddTags = new Button(this, SWT.NONE);
		btnAddTags.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Open a dialog which allows users to select tags
				StringListSelectionDialog dialog = new StringListSelectionDialog(
						parent.getShell(), tagNames, LogEntryUtil
								.getTagNames(logEntryBuilder.build()),
						"Add Tags");
				if (dialog.open() == IDialogConstants.OK_ID) {
					Collection<TagBuilder> newTags = new ArrayList<TagBuilder>();
					for (String tagName : dialog.getSelectedValues()) {
						newTags.add(TagBuilder.tag(tagName));
					}
					// logEntryBuilder.setTags(newTags);
					// updateWidget();
					tagList.setItems(dialog.getSelectedValues().toArray(
							new String[dialog.getSelectedValues().size()]));
					parent.layout();
				}
			}
		});
		btnAddTags.setText("Add Tags");
		FormData fd_btnAddTags = new FormData();
		fd_btnAddTags.top = new FormAttachment(tagList, 5);
		fd_btnAddTags.left = new FormAttachment(label, 2);
		fd_btnAddTags.right = new FormAttachment(100, -5);
		btnAddTags.setLayoutData(fd_btnAddTags);

		tabFolder = new CTabFolder(this, SWT.BORDER);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.top = new FormAttachment(text, 5);
		fd_tabFolder.left = new FormAttachment(0, 5);
		fd_tabFolder.right = new FormAttachment(70);
		fd_tabFolder.bottom = new FormAttachment(100, -5);
		tabFolder.setLayoutData(fd_tabFolder);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		tbtmAttachments = new CTabItem(tabFolder, SWT.NONE);
		tbtmAttachments.setText("Attachments");
		tabFolder.setSelection(tbtmAttachments);

		tbtmAttachmentsComposite = new Composite(tabFolder, SWT.NONE);
		tbtmAttachments.setControl(tbtmAttachmentsComposite);
		tbtmAttachmentsComposite.setLayout(new FormLayout());

		btnAddImage = new Button(tbtmAttachmentsComposite, SWT.NONE);
		btnAddImage.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
				dlg.setFilterExtensions(new String[] { "*.png" }); //$NON-NLS-1$
				dlg.setFilterNames(new String[] { "PNG Image" }); //$NON-NLS-1$
				final String filename = dlg.open();
				if (filename != null) {
					imageStackWidget.addImageFilename(filename);
				}
			}
		});
		FormData fd_btnAddImage = new FormData();
		fd_btnAddImage.left = new FormAttachment(1);
		fd_btnAddImage.bottom = new FormAttachment(100, -2);
		fd_btnAddImage.right = new FormAttachment(32);
		btnAddImage.setLayoutData(fd_btnAddImage);
		btnAddImage.setText("Add Image");

		btnAddScreenshot = new Button(tbtmAttachmentsComposite, SWT.NONE);
		btnAddScreenshot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addScreenshot(true);
			}
		});
		FormData fd_btnAddScreenshot = new FormData();
		fd_btnAddScreenshot.left = new FormAttachment(33);
		fd_btnAddScreenshot.bottom = new FormAttachment(100, -2);
		fd_btnAddScreenshot.right = new FormAttachment(65);
		btnAddScreenshot.setLayoutData(fd_btnAddScreenshot);
		btnAddScreenshot.setText("Screenshot");

		btnCSSWindow = new Button(tbtmAttachmentsComposite, SWT.NONE);
		btnCSSWindow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addScreenshot(false);
			}
		});
		FormData fd_btnCSSWindow = new FormData();
		fd_btnCSSWindow.left = new FormAttachment(66);
		fd_btnCSSWindow.bottom = new FormAttachment(100, -2);
		fd_btnCSSWindow.right = new FormAttachment(99);
		btnCSSWindow.setLayoutData(fd_btnCSSWindow);
		btnCSSWindow.setText("CSS Window");

		imageStackWidget = new ImageStackWidget(tbtmAttachmentsComposite,
				SWT.NONE);
		FormData fd_imageStackWidget = new FormData();
		fd_imageStackWidget.bottom = new FormAttachment(btnAddImage, -2);
		fd_imageStackWidget.right = new FormAttachment(100, -2);
		fd_imageStackWidget.top = new FormAttachment(0, 2);
		fd_imageStackWidget.left = new FormAttachment(0, 2);
		imageStackWidget.setLayoutData(fd_imageStackWidget);

		empty = new FormData();
		empty.top = new FormAttachment(0);
		empty.bottom = new FormAttachment(0);
		empty.left = new FormAttachment(0);
		empty.right = new FormAttachment(0);

		this.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				switch (evt.getPropertyName()) {
				case "editable":
					// mark as editable/uneditable all the input UIs
					configureWidgets(logEntryWidget);
					if ((boolean) evt.getNewValue() == true) {
						// the widget was just marked read for editing.
						if (logEntryBuilder == null) {
							// No old logEntryBuilder
							// create a new one using the logEntry
							if (getLogEntry() != null) {
								logEntryBuilder = LogEntryBuilder
										.logEntry(getLogEntry());
							} else {
								logEntryBuilder = LogEntryBuilder.withText("");
							}
						}
					} else if ((boolean) evt.getNewValue() == false) {
						// No longer Editing so save the state of the currently
						// created logEntry in the logEntryBuilder
						Collection<TagBuilder> newTags = new ArrayList<TagBuilder>();
						for (String tagName : tagList.getItems()) {
							if (!tagName.equals("Tags:"))
								newTags.add(TagBuilder.tag(tagName));
						}
						Collection<LogbookBuilder> newLogbooks = new ArrayList<LogbookBuilder>();
						for (String logbookName : logbookList.getItems()) {
							if (!logbookName.equalsIgnoreCase("Logbooks:"))
								newLogbooks.add(LogbookBuilder
										.logbook(logbookName));
						}
						logEntryBuilder = LogEntryBuilder
								.withText(text.getText())
								.owner(textOwner.getText())
								.setLogbooks(newLogbooks).setTags(newTags);
					}
					try {
						if (logbookClient == null) {
							logbookClient = LogbookClientManager
									.getLogbookClientFactory().getClient();
							logbookNames = Lists.transform(
									new ArrayList<Logbook>(logbookClient
											.listLogbooks()),
									new Function<Logbook, String>() {
										public String apply(Logbook input) {
											return input.getName();
										};
									});
							tagNames = Lists.transform(new ArrayList<Tag>(
									logbookClient.listTags()),
									new Function<Tag, String>() {
										public String apply(Tag input) {
											return input.getName();
										};
									});
						}
					} catch (Exception e) {
						// Failed to get a client to the logbook
						// Display exception and disable editing.
						e.printStackTrace();
					}
					updateWidget();
					break;
				case "logEntry":
					// mark as editable/uneditable all the input UIs
					updateWidget();
					break;
				default:
					break;
				}
			}
		});
		configureWidgets(logEntryWidget);
		updateWidget();
	}

	private void configureWidgets(Composite parent) {
		btnEnableEdit.setSelection(editable);
		text.setEditable(editable);
		textOwner.setEditable(editable);
		btnSubmit.setEnabled(editable);
		// logbookList.setEnabled(editable);
		// tagList.setEnabled(editable);
		btnAddLogbook.setVisible(editable);
		btnAddTags.setVisible(editable);
		// Attachment buttons need to be enabled/disabled
		btnAddImage.setVisible(editable);
		btnAddScreenshot.setVisible(editable);
		btnCSSWindow.setVisible(editable);
		if (!editable) {
			btnAddLogbook.setSize(btnAddLogbook.getSize().x, 0);
			btnAddTags.setSize(btnAddTags.getSize().x, 0);
			FormData fd_tagList = ((FormData) tagList.getLayoutData());
			fd_tagList.top = new FormAttachment(logbookList, 5);
			tagList.setLayoutData(fd_tagList);
			// Attachment Tab Layout
			FormData fd = ((FormData) imageStackWidget.getLayoutData());
			fd.bottom = new FormAttachment(100, -2);
			imageStackWidget.setLayoutData(fd);
		} else {
			btnAddLogbook.setSize(btnAddLogbook.getSize().x, SWT.DEFAULT);
			btnAddTags.setSize(btnAddTags.getSize().x, SWT.DEFAULT);
			FormData fd_tagList = ((FormData) tagList.getLayoutData());
			fd_tagList.top = new FormAttachment(btnAddLogbook, 5);
			tagList.setLayoutData(fd_tagList);
			// Attachment Tab Layout
			FormData fd = ((FormData) imageStackWidget.getLayoutData());
			fd.bottom = new FormAttachment(btnAddImage, -2);
			imageStackWidget.setLayoutData(fd);
		}
		tbtmAttachmentsComposite.layout();
		parent.layout();
	}

	private void updateWidget() {
		if (editable) {
			// Show the LogBuilder
			updateWidget(logEntryBuilder.build());
			this.layout();
		} else {
			updateWidget(logEntry);
		}
	}

	private void updateWidget(LogEntry logEntry) {
		if (logEntry != null) {
			// Show the logEntry
			text.setText(logEntry.getText());
			// textDate.setText(DateFormat.getDateInstance().format(
			// logEntry.getCreateDate()));
			textOwner.setText(logEntry.getOwner());
			java.util.List<String> logbookNames = new ArrayList<String>();
			logbookNames.add("Logbooks:");
			logbookNames.addAll(LogEntryUtil.getLogbookNames(logEntry));
			logbookList.setItems(logbookNames.toArray(new String[logbookNames
					.size()]));
			java.util.List<String> tagNames = new ArrayList<String>();
			tagNames.add("Tags:");
			tagNames.addAll(LogEntryUtil.getTagNames(logEntry));
			tagList.setItems(tagNames.toArray(new String[tagNames.size()]));
		} else {
			text.setText("");
			textOwner.setText("");
			logbookList.setItems(new String[0]);
			tagList.setItems(new String[0]);
		}
		this.layout();
	};

	@SuppressWarnings("nls")
	private void addScreenshot(final boolean full) {
		// Hide the shell that displays the dialog
		// to keep the dialog itself out of the screenshot
		getShell().setVisible(false);

		// Take the screen shot
		final Image image = full ? Screenshot.getFullScreenshot() : Screenshot
				.getApplicationScreenshot();

		// Show the dialog again
		getShell().setVisible(true);

		// Write to file
		try {
			final File screenshot_file = File.createTempFile("screenshot",
					".png");
			screenshot_file.deleteOnExit();

			final ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[] { image.getImageData() };
			image.dispose();
			// Save
			loader.save(screenshot_file.getPath(), SWT.IMAGE_PNG);

			imageStackWidget.addImageFilename(screenshot_file.getPath());
		} catch (Exception ex) {
			MessageDialog.openError(getShell(), "Error", ex.getMessage());
		}
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		boolean oldValue = this.editable;
		this.editable = editable;
		changeSupport.firePropertyChange("editable", oldValue, editable);
	}

	public LogEntry getLogEntry() {
		return logEntry;
	}

	public void setLogEntry(LogEntry logEntry) {
		LogEntry oldValue = this.logEntry;
		this.logEntry = logEntry;
		changeSupport.firePropertyChange("logEntry", oldValue, logEntry);
	}
}
