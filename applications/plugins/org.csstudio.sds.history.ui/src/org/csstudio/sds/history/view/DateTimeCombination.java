package org.csstudio.sds.history.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;

/**
 * Kombination aus Datum- und Zeit-Widget.
 * 
 * Als Selection wird ein StructuredSelection-Objekt geliefert, das als erstes
 * Element ein java.util.Calendar enthält. Die aktuell eingestellte Zeit kann
 * aber auch über getSelectedTime() erfragt werden.
 * 
 * @author GeneAL cz
 */
public class DateTimeCombination implements ISelectionProvider {

	private List<ISelectionChangedListener>	listeners;
	private DateTime						dateSelector;
	private DateTime						timeSelector;
	private Composite						main;

	public DateTimeCombination(Composite parent, int style) {
		listeners = new ArrayList<ISelectionChangedListener>();

		createPartControl(parent, style);
	}

	private void createPartControl(Composite parent, int style) {
		main = new Composite(parent, style);
		main.setLayout(new GridLayout(2, false));

		SelectionListener selectionListener = createSelectionListener();

		dateSelector = new DateTime(main, SWT.DATE | SWT.DROP_DOWN);
		dateSelector.addSelectionListener(selectionListener);

		timeSelector = new DateTime(main, SWT.TIME | SWT.MEDIUM);
		timeSelector.addSelectionListener(selectionListener);
	}

	private SelectionListener createSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				notifyListeners();
			}
		};
	}

	public void setLayoutData(GridData layoutData) {
		main.setLayoutData(layoutData);
	}

	public Calendar getSelectedTime() {
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.YEAR, dateSelector.getYear());
		calendar.set(Calendar.MONTH, dateSelector.getMonth());
		calendar.set(Calendar.DAY_OF_MONTH, dateSelector.getDay());
		calendar.set(Calendar.HOUR_OF_DAY, timeSelector.getHours());
		calendar.set(Calendar.MINUTE, timeSelector.getMinutes());
		calendar.set(Calendar.SECOND, timeSelector.getSeconds());
		calendar.set(Calendar.MILLISECOND, 500);

		return calendar;
	}

	public void setSelectedTime(Calendar calendar) {
		dateSelector.setYear(calendar.get(Calendar.YEAR));
		dateSelector.setMonth(calendar.get(Calendar.MONTH));
		dateSelector.setDay(calendar.get(Calendar.DAY_OF_MONTH));
		timeSelector.setHours(calendar.get(Calendar.HOUR_OF_DAY));
		timeSelector.setMinutes(calendar.get(Calendar.MINUTE));
		timeSelector.setSeconds(calendar.get(Calendar.SECOND));
	}

	private void notifyListeners() {
		SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());

		for (ISelectionChangedListener listener : listeners) {
			listener.selectionChanged(event);
		}
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.add(listener);
	}

	@Override
	public ISelection getSelection() {
		return new StructuredSelection(getSelectedTime());
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		if (selection instanceof StructuredSelection && !((StructuredSelection) selection).isEmpty()
				&& (((StructuredSelection) selection).getFirstElement() instanceof Calendar)) {
			setSelectedTime((Calendar) ((StructuredSelection) selection).getFirstElement());
		}
	}
}
