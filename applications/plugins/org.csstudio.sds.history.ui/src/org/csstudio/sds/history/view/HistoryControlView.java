package org.csstudio.sds.history.view;

import org.csstudio.sds.history.internal.HistoryControlImages;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * View to control the history mode of a display.
 * 
 * @author Christian Mein
 * 
 */
public class HistoryControlView {

	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormat.forPattern("dd.MM.yy HH:mm:ss");
	private static final int TIME_FONT_SIZE = 12;
	public static final int PLAY_RATIO_DENOMINATOR_ONE = 1;

	private DateTimeCombination fromField;
	private HistoryScaleButtonCombination stepsSlider;

	private DateTimeCombination toField;
	private Group timeGroup;
	private Group intervalGroup;
	private Group controlGroup;

	private Text playRatioField;
	private Text secondsStepField;
	private Label timeLabel;

	private HistoryControlViewListener viewListener;
	private Button playButton;
	private Button pauseButton;
	private Button backwardBbutton;
	private Button forwardButton;

	public HistoryControlView(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());

		createTimeGroup(main);
		createIntervalGroup(main);
		createControlGroup(main);
		createSlider(main);

		initListeners();
	}
	
	public void setViewListener(HistoryControlViewListener listener) {
		assert listener != null : "listener != null";

		viewListener = listener;
	}

	private void createTimeGroup(Composite parent) {
		timeGroup = new Group(parent, SWT.NONE);
		timeGroup.setText("Time");
		timeGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		timeGroup.setLayout(new GridLayout(1, false));

		timeLabel = new Label(timeGroup, SWT.NONE);
		FontData[] fontData = timeLabel.getFont().getFontData();
		fontData[0].setHeight(TIME_FONT_SIZE);
		fontData[0].setStyle(SWT.BOLD);
		timeLabel.setFont(new Font(Display.getDefault(), fontData[0]));
	}

	private void createIntervalGroup(Composite parent) {
		intervalGroup = new Group(parent, SWT.NONE);
		intervalGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		intervalGroup.setText("Interval");
		intervalGroup.setLayout(new GridLayout(4, false));

		Label fromLabel = new Label(intervalGroup, SWT.NONE);
		fromLabel.setText("From:");
		fromField = new DateTimeCombination(intervalGroup, SWT.NONE);

		Label toLabel = new Label(intervalGroup, SWT.NONE);
		toLabel.setText("To:");
		toField = new DateTimeCombination(intervalGroup, SWT.NONE);
	}

	private void createControlGroup(Composite parent) {
		controlGroup = new Group(parent, SWT.NONE);
		controlGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		controlGroup.setText("Control");
		controlGroup.setLayout(new GridLayout(10, false));

		Label playRatioLabel = new Label(controlGroup, SWT.NONE);
		playRatioLabel.setText("Play Ratio ");

		playRatioField = new Text(controlGroup, SWT.BORDER | SWT.RIGHT);
		playRatioField.setText("1.0");
		playRatioField.setEditable(true);
		playRatioField.setTextLimit(4);
		playRatioField.addVerifyListener(createPlayRatioFieldVerifier());

		GridData fd1 = new GridData();
		fd1.horizontalAlignment = SWT.RIGHT;
		fd1.widthHint = 22;
		fd1.minimumWidth = 22;
		playRatioField.setLayoutData(fd1);

		Label playRatioLabel2 = new Label(controlGroup, SWT.NONE);
		playRatioLabel2.setText(": " + Integer.toString(PLAY_RATIO_DENOMINATOR_ONE));

		playButton = new Button(controlGroup, SWT.TOGGLE);
		playButton.setImage(HistoryControlImages.PLAY.getImage());
		playButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewListener.play();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		pauseButton = new Button(controlGroup, SWT.PUSH);
		pauseButton.setImage(HistoryControlImages.PAUSE.getImage());
		pauseButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewListener.pause();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Label sliderStep = new Label(controlGroup, SWT.NONE);
		sliderStep.setText("Slider Step: ");

		secondsStepField = new Text(controlGroup, SWT.BORDER | SWT.RIGHT);
		secondsStepField.setText("60");
		secondsStepField.setEditable(true);
		secondsStepField.setTextLimit(4);
		GridData fd2 = new GridData();
		fd2.widthHint = 30;
		fd2.minimumWidth = 30;
		secondsStepField.setLayoutData(fd2);
		secondsStepField.addVerifyListener(createSecondsStepFieldVerifier());

		Label stepIntervalLabel2 = new Label(controlGroup, SWT.NONE);
		stepIntervalLabel2.setText("sec");

		backwardBbutton = new Button(controlGroup, SWT.PUSH);
		backwardBbutton.setImage(HistoryControlImages.STEP_BACKWARD.getImage());
		backwardBbutton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewListener.stepBackward();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		

		forwardButton = new Button(controlGroup, SWT.PUSH);
		forwardButton.setImage(HistoryControlImages.STEP_FORWARD.getImage());
		forwardButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewListener.stepForward();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void createSlider(Composite parent) {
		stepsSlider = new HistoryScaleButtonCombination(parent, SWT.BORDER);
		stepsSlider.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		stepsSlider.setMinimum(0);
		stepsSlider.setIncrement(1);
		stepsSlider.setSelection(0);
	}

	private void initListeners() {
		fromField.addSelectionChangedListener(createFromFieldListener());
		toField.addSelectionChangedListener(createToFieldListener());
		stepsSlider.addSelectionListener(createStepsSelectionListener());
		stepsSlider.addSelectionStoppedListener(createStepsSelectionStoppedListener());
		// TODO CME: step slider also reacts on mouse wheel commands
	}

	private ISelectionChangedListener createFromFieldListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (fromField.getSelectedTime().after(toField.getSelectedTime())) {
					fromField.setSelectedTime(toField.getSelectedTime());
				}
				viewListener.changedTimeInterval(new DateTime(fromField.getSelectedTime()), new DateTime(toField.getSelectedTime()));
			}
		};
	}

	private ISelectionChangedListener createToFieldListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (toField.getSelectedTime().before(fromField.getSelectedTime())) {
					toField.setSelectedTime(fromField.getSelectedTime());
				}
				viewListener.changedTimeInterval(new DateTime(fromField.getSelectedTime()), new DateTime(toField.getSelectedTime()));
			}
		};
	}

	private SelectionListener createStepsSelectionListener() {
		return new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				viewListener.changedSliderPosition(stepsSlider.getSelection(), false);
			}
		};
	}

	private MouseListener createStepsSelectionStoppedListener() {
		return new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				viewListener.changedSliderPosition(stepsSlider.getSelection(), true);
			}
		};
	}
	
	private VerifyListener createSecondsStepFieldVerifier() {
		return new VerifyListener() {

			@Override
			public void verifyText(VerifyEvent e) {

				Text text = (Text) e.getSource();

				// get old text and create new text by using the VerifyEvent.text
				final String oldS = text.getText();
				String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);

				Integer secondsStep = new Integer(60);

				boolean isInt = true;
				try {
					secondsStep = Integer.parseInt(newS);
				} catch (NumberFormatException ex) {
					isInt = false;
				}

				if (!isInt || secondsStep == 0)
					e.doit = false;
				else
					viewListener.changedStepInterval(secondsStep);
			}
		};
	}

	private VerifyListener createPlayRatioFieldVerifier() {
		return new VerifyListener() {

			@Override
			public void verifyText(VerifyEvent e) {

				Text text = (Text) e.getSource();

				// get old text and create new text by using the VerifyEvent.text
				final String oldS = text.getText();
				String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);

				Float playRatio = new Float(1.0f);

				boolean isFloat = true;
				try {
					playRatio = Float.parseFloat(newS);
				} catch (NumberFormatException ex) {
					isFloat = false;
				}

				if (!isFloat)
					e.doit = false;
				else
					viewListener.changedPlayRatio(playRatio);
			}
		};
	}

	public void setSelectedTime(DateTime time) {
		timeLabel.setText(TIME_FORMAT.print(time));
	}

	public HistoryScaleButtonCombination getStepsSlider() {
		return stepsSlider;
	}

	public DateTimeCombination getStartTimeField() {
		return fromField;
	}

	public DateTimeCombination getEndTimeField() {
		return toField;
	}
	
	public Text getPlayRatioField() {
		return playRatioField;
	}
	
	public Text getSecondsStepField() {
		return secondsStepField;
	}
	
	public Button getPlayButton() {
		return playButton;
	}
	
	public void setEnableStepButtons(boolean enabled) {
		forwardButton.setEnabled(enabled);
		backwardBbutton.setEnabled(enabled);
	}
}
