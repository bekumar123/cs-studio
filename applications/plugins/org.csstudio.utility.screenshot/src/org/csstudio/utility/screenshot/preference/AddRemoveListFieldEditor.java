
/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.utility.screenshot.preference;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

/**
 * A field editor for displaying and storing a list of strings.
 * Buttons are provided for adding items to the list and removing
 * items from the list.
 */
public class AddRemoveListFieldEditor extends FieldEditor
{
	private static final String DEFAULT_ADD_LABEL = "Add";
	private static final String DEFAULT_REMOVE_LABEL = "Remove";
	private static final String DEFAULT_SEPERATOR = ";";

	private static final int VERTICAL_DIALOG_UNITS_PER_CHAR = 8;
	// private static final int HORIZONTAL_DIALOG_UNITS_PER_CHAR = 4;
	private static final int LIST_HEIGHT_IN_CHARS = 10;
	private static final int LIST_HEIGHT_IN_DLUS =
		LIST_HEIGHT_IN_CHARS * VERTICAL_DIALOG_UNITS_PER_CHAR;

	// The top-level control for the field editor.
	private Composite top;

	// The list of tags.
	private List list;

	// The text field for inputting new tags.
	private Text textField;

	// The button for adding the contents of
	// the text field to the list.
	private Button add;

	// The button for removing the currently-selected list item.
	private Button remove;

	// The string used to seperate list items
	// in a single String representation.
	private String seperator = DEFAULT_SEPERATOR;

	public AddRemoveListFieldEditor(final String name,
	                                final String labelText,
	                                final Composite parent) {
	    super(name, labelText, parent);
	}

	public AddRemoveListFieldEditor(final String name,
	                                final String labelText,
	                                final String addButtonText,
	                                final String removeButtonText,
	                                final Composite parent) {
        super(name, labelText, parent);
        setAddButtonText(addButtonText);
        setRemoveButtonText(removeButtonText);
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
	 */
	@Override
	protected void adjustForNumColumns(final int numColumns) {
	    ((GridData)top.getLayoutData()).horizontalSpan = numColumns;
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid
	 * (Composite, int)
	 */
	@Override
	protected void doFillIntoGrid(final Composite parent, final int numColumns) {

	    top = parent;

		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		top.setLayoutData(gd);

		final Label label = getLabelControl(top);
		final GridData labelData = new GridData();
		labelData.horizontalSpan = numColumns;
		label.setLayoutData(labelData);

		list = new List(top, SWT.BORDER | SWT.V_SCROLL);

		// Create a grid data that takes up the extra
		// space in the dialog and spans both columns.
		final GridData listData = new GridData(GridData.FILL_HORIZONTAL);
		listData.heightHint =
			convertVerticalDLUsToPixels(list, LIST_HEIGHT_IN_DLUS);
		listData.horizontalSpan = numColumns;

		list.setLayoutData(listData);
		list.addSelectionListener(new SelectionAdapter()
        {
			@Override
			public void widgetSelected(final SelectionEvent e)
            {
				selectionChanged();
			}
		});

		// Create a composite for the add and remove
		// buttons and the input text field.
		final Composite addRemoveGroup = new Composite(top, SWT.NONE);

		final GridData addRemoveData = new GridData(GridData.FILL_HORIZONTAL);
		addRemoveData.horizontalSpan = numColumns;
		addRemoveGroup.setLayoutData(addRemoveData);

		final GridLayout addRemoveLayout = new GridLayout();
		addRemoveLayout.numColumns = numColumns;
		addRemoveLayout.marginHeight = 0;
		addRemoveLayout.marginWidth = 0;
		addRemoveGroup.setLayout(addRemoveLayout);

		// Create a composite for the add and remove buttons.
		final Composite buttonGroup = new Composite(addRemoveGroup, SWT.NONE);
		buttonGroup.setLayoutData(new GridData());

		final GridLayout buttonLayout = new GridLayout();
		buttonLayout.marginHeight = 0;
		buttonLayout.marginWidth = 0;
		buttonGroup.setLayout(buttonLayout);

		// Create the add button.
		add = new Button(buttonGroup, SWT.NONE);
		add.setText(DEFAULT_ADD_LABEL);
		add.addSelectionListener(new SelectionAdapter()
        {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				add();
			}
		});

		final GridData addData = new GridData(GridData.FILL_HORIZONTAL);
		//addData.heightHint = convertVerticalDLUsToPixels(add, IDialogConstants.BUTTON_HEIGHT);
		addData.widthHint = convertHorizontalDLUsToPixels(add, IDialogConstants.BUTTON_WIDTH);
		add.setLayoutData(addData);

		// Create the remove button.
		remove = new Button(buttonGroup, SWT.NONE);
		remove.setEnabled(false);
		remove.setText(DEFAULT_REMOVE_LABEL);
		remove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
			    getTagList().remove(getTagList().getSelectionIndex());
				selectionChanged();
			}
		});
		final GridData removeData = new GridData(GridData.FILL_HORIZONTAL);
		// removeData.heightHint = convertVerticalDLUsToPixels(remove, IDialogConstants.BUTTON_HEIGHT);
		removeData.widthHint = convertHorizontalDLUsToPixels(remove, IDialogConstants.BUTTON_WIDTH);
		remove.setLayoutData(removeData);

		// Create the text field.
		textField = new Text(addRemoveGroup, SWT.BORDER);

		final GridData textData = new GridData(GridData.FILL_HORIZONTAL);
		textData.horizontalSpan = numColumns - 1;
		textData.verticalAlignment = GridData.BEGINNING;
		textField.setLayoutData(textData);
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	@Override
	protected void doLoad() {
		final String items = getPreferenceStore().getString(getPreferenceName());
		setList(items);
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	@Override
	protected void doLoadDefault() {
		final String items = getPreferenceStore().getDefaultString(getPreferenceName());
		setList(items);
	}

	// Parses the string into seperate list items and adds them to the list.
	private void setList(final String items) {
		final String[] itemArray = parseString(items);
		list.setItems(itemArray);
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	@Override
	protected void doStore() {
		final String s = createListString(list.getItems());
		if (s != null) {
            getPreferenceStore().setValue(getPreferenceName(), s);
        }
	}

	public List getTagList() {
	    return list;
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	@Override
	public int getNumberOfControls() {
		// The button composite and the text field.
		return 2;
	}

	// Adds the string in the text field to the list.
	void add() {
		final String tag = textField.getText();
		if (tag != null && tag.length() > 0) {
            list.add(tag);
        }
		textField.setText("");
	}

	/**
	 *  Sets the label for the button that adds
	 * the contents of the text field to the list.
	 */
	public void setAddButtonText(final String text) {
		add.setText(text);
	}

	/**
	 *  Sets the label for the button that removes
	 * the selected item from the list.
	 */
	public void setRemoveButtonText(final String text) {
		remove.setText(text);
	}

	/**
	 * Sets the string that seperates items in the list when the
	 * list is stored as a single String in the preference store.
	 */
	public void setSeperator(final String seperator) {
		this.seperator = seperator;
	}

	/**
	 *  Creates the single String representation of the list
	 * that is stored in the preference store.
	 */
	private String createListString(final String[] items) {
		final StringBuffer path = new StringBuffer("");//$NON-NLS-1$

		for (final String item : items) {
			path.append(item);
			path.append(seperator);
		}

		return path.toString();
	}

	/**
	 *  Parses the single String representation of the list
	 * into an array of list items.
	 */
	private String[] parseString(final String stringList) {

	    final StringTokenizer st = new StringTokenizer(stringList, seperator); //$NON-NLS-1$
		final ArrayList<String> v = new ArrayList<String>();

        while(st.hasMoreElements()) {
			v.add((String)st.nextElement());
		}

        return v.toArray(new String[v.size()]);
	}

	// Sets the enablement of the remove button depending
	// on the selection in the list.
	void selectionChanged() {
		final int index = list.getSelectionIndex();
		remove.setEnabled(index >= 0);
	}
}
