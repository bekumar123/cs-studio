package org.csstudio.sds.history.view;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;

public class HistoryScaleButtonCombination {
	private List<SelectionListener> listeners;
	private List<MouseListener> selectionStoppedListener;
	private Scale scale;
	private Composite main;

	public HistoryScaleButtonCombination(Composite parent, int style) {
		listeners = new ArrayList<SelectionListener>();
		selectionStoppedListener = new ArrayList<MouseListener>();

		createPartControl(parent, style);
	}

	@PostConstruct
	private void createPartControl(Composite parent, int style) {
		main = new Composite(parent, style);
		main.setLayout(new GridLayout(1, false));

		scale = createScale(main);
	}

	private Scale createScale(Composite parent) {
		Scale scale = new Scale(parent, SWT.HORIZONTAL);
		scale.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		scale.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				notifyTimeLabelChangeListener(e);
			}
		});

		scale.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				notifySelectionStoppedListener(e);
			}
		});

		scale.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.ARROW_LEFT || e.keyCode == SWT.ARROW_RIGHT) {
					//TODO CME: notify listeners
				}
			}
		});

		return scale;
	}

	public void setLayoutData(GridData layoutData) {
		main.setLayoutData(layoutData);
	}

	public void addSelectionListener(SelectionListener listener) {
		listeners.add(listener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		listeners.remove(listener);
	}

	public void addSelectionStoppedListener(MouseListener listener) {
		selectionStoppedListener.add(listener);
	}

	public void removeSelectionStoppedListener(MouseListener listener) {
		selectionStoppedListener.remove(listener);
	}
	
	public void setFocus() {
		main.setFocus();
	}

	public boolean getEnabled() {
		return scale.getEnabled();
	}

	public void setEnabled(boolean enabled) {
		scale.setEnabled(enabled);
	}

	public int getMinimum() {
		return scale.getMinimum();
	}

	public void setMinimum(int value) {
		assert value >= 0 : "value >= 0";
		assert value < getMaximum() : "value < getMaximum()";

		scale.setMinimum(value);
		if (getSelection() < value) {
			setSelection(value);
		}
	}

	public int getMaximum() {
		return scale.getMaximum();
	}

	public void setMaximum(int value) {
		assert value > getMinimum() : "value > getMinimum()";

		scale.setMaximum(value);
		if (getSelection() > value) {
			setSelection(value);
		}
	}

	public int getSelection() {
		return scale.getSelection();
	}

	public void setSelection(int value) {
		assert value >= getMinimum() : "value >= getMinimum()";
		assert value <= getMaximum() : "value <= getMaximum()";

		scale.setSelection(value);
	}

	public void setIncrement(int increment) {
		scale.setIncrement(increment);
	}

	private void notifyTimeLabelChangeListener(SelectionEvent event) {
		for (SelectionListener listener : listeners) {
			listener.widgetSelected(event);
		}
	}

	private void notifySelectionStoppedListener(MouseEvent e) {
		for (MouseListener listener : selectionStoppedListener) {
			listener.mouseUp(e);
		}
	}
}
