package org.csstudio.sds.history.playcontrol;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.csstudio.sds.history.domain.events.UpdateTimeEvent;
import org.csstudio.sds.history.domain.listener.ITimeChangeListener;
import org.csstudio.sds.history.domain.listener.ITimeperiodUpdateListener;
import org.csstudio.sds.history.internal.HistoryUiActivator;
import org.csstudio.sds.history.view.HistoryControlView;
import org.csstudio.sds.history.view.HistoryControlViewListener;
import org.csstudio.sds.history.view.HistoryScaleButtonCombination;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * Controller for the {@link HistoryControlView}.
 * 
 * @author Christian
 * 
 */

// TODO CME: use BigInteger/Decimal for calculation? maybe this usecase allows use of double 

public class HistoryControlViewController extends ViewPart implements ITimeChangeListener, ITimeperiodUpdateListener {

	private Collection<ITimeChangeListener> _timeChangeListener;

	private HistoryControlView _view;

	private DateTime _currentTime;

	private DateTime _startTime;

	private DateTime _endTime;

	private int _secondsStep;

	private float _playRatio;

	private Display _display;

	private ChannelListenerUpdateTimer _timer;

	private boolean isRunning;

	public HistoryControlViewController() {
		_display = Display.getDefault();
		_timer = new ChannelListenerUpdateTimer();
		_timeChangeListener = new ArrayList<ITimeChangeListener>();

		_endTime = new DateTime();
		_startTime = _endTime.minusHours(1);
		_currentTime = new DateTime(_startTime);
		_secondsStep = 60;
		_playRatio = 1.0f;
	}

	@Override
	public void createPartControl(Composite parent) {
		_view = new HistoryControlView(parent);
		_view.setViewListener(createViewListener());

		_view.getStartTimeField().setSelectedTime(_startTime.toGregorianCalendar());
		_view.getEndTimeField().setSelectedTime(_endTime.toGregorianCalendar());
		_view.getPlayRatioField().setText("" + _playRatio);
		_view.getSecondsStepField().setText("" + _secondsStep);
		_view.setSelectedTime(_currentTime);
		
//		TODO CME: mysterious osgi/rcp behavior. HistoryService will not get properly binded to this bundle after startup. Only after restart of HistoryService.
//		HistoryUiActivator.getDefault().addTimeChangeServiceListener(createITimeChangeServiceListener());
		_timeChangeListener = HistoryUiActivator.getDefault().getTimeChangeListeners();

		HistoryUiActivator.getDefault().getBundle().getBundleContext().registerService(ITimeperiodUpdateListener.class, this, null);
		HistoryUiActivator.getDefault().getBundle().getBundleContext().registerService(ITimeChangeListener.class, this, null);
	}

	@Override
	public void setFocus() {
	}

	private void updateSliderInterval() {
		assert _secondsStep > 0 : "Step Interval > 0";

		long startMillis = _startTime.getMillis();
		long endMillis = _endTime.getMillis();

		int stepMillis = _secondsStep * 1000;
		long timelapseMillis = endMillis - startMillis;

		int steps = (int) (timelapseMillis / stepMillis); // div by zero

		if (steps > 0) {
			_view.getStepsSlider().setMaximum(steps);
			_view.getStepsSlider().setEnabled(true);
		} else {
			_view.getStepsSlider().setEnabled(false);
		}

		if (_view.getStepsSlider().getSelection() > steps) {
			_view.getStepsSlider().setSelection(steps);
		}
	}

	private DateTime getSliderSelectedTime() {
		int step = _view.getStepsSlider().getSelection();
		int stepsMillis = _secondsStep * 1000 * step;
		DateTime currentTime = _startTime.plusMillis(stepsMillis);

		return currentTime;
	}

	private void setSliderPosition(DateTime timeStamp) {
		Long timeIndexMillis = timeStamp.getMillis();

		// TODO CME: check that timeStamp is in time interval
		// TODO CME: review calculation
		Long fromMillis = _startTime.getMillis();

		int stepMillis = _secondsStep * 1000;

		Long millisFromStart = timeIndexMillis - fromMillis;
		if (stepMillis > 0) {
			int positionIndex = (int) (millisFromStart / stepMillis);
			_view.getStepsSlider().setSelection(positionIndex);
		}
	}

	private void stopPlayback() {
		if (isRunning) {
			isRunning = false;
			_display.timerExec(-1, _timer);
			_view.getPlayButton().setSelection(false);
		}
	}

	private HistoryControlViewListener createViewListener() {
		return new HistoryControlViewListener() {

			@Override
			public void stepForward() {
				stopPlayback();
				HistoryScaleButtonCombination stepsSlider = _view.getStepsSlider();

				int nextStep = stepsSlider.getSelection() + 1;
				if (nextStep >= stepsSlider.getMinimum() && nextStep <= stepsSlider.getMaximum()) {
					stepsSlider.setSelection(nextStep);
				}
				_currentTime = getSliderSelectedTime();
				_view.setSelectedTime(_currentTime);

				updateTimeChangeListener(true);
			}

			@Override
			public void stepBackward() {
				stopPlayback();
				HistoryScaleButtonCombination stepsSlider = _view.getStepsSlider();

				int nextStep = stepsSlider.getSelection() - 1;
				if (nextStep >= stepsSlider.getMinimum() && nextStep <= stepsSlider.getMaximum()) {
					stepsSlider.setSelection(nextStep);
				}
				_currentTime = getSliderSelectedTime();
				_view.setSelectedTime(_currentTime);

				updateTimeChangeListener(true);
			}

			@Override
			public void play() {
				if (!isRunning) {
					_display.timerExec(1000, _timer);
					isRunning = true;
				}
				_view.getPlayButton().setSelection(true);
			}

			@Override
			public void pause() {
				stopPlayback();
			}

			@Override
			public void changedTimeInterval(DateTime start, DateTime end) {
				_startTime = start;
				_endTime = end;

				updateSliderInterval();
				_currentTime = getSliderSelectedTime();
				_view.setSelectedTime(getSliderSelectedTime());
			}

			@Override
			public void changedStepInterval(int secondsStep) {

				if (_startTime.plusSeconds(secondsStep).isBefore(_endTime)) {
					_secondsStep = secondsStep;
					updateSliderInterval();
					setSliderPosition(_currentTime);
					_currentTime = getSliderSelectedTime();
					_view.setSelectedTime(_currentTime);
					
					_view.getStepsSlider().setEnabled(true);
					_view.setEnableStepButtons(true);
				} else {
					_view.getStepsSlider().setEnabled(false);
					_view.setEnableStepButtons(false);
				}
			}

			@Override
			public void changedSliderPosition(int position, boolean mouseUp) {
				_currentTime = getSliderSelectedTime();
				_view.setSelectedTime(getSliderSelectedTime());

				updateTimeChangeListener(true);
			}

			@Override
			public void changedPlayRatio(float playRatio) {
				_playRatio = playRatio;

			}
		};
	}

	private void updateTimeChangeListener(boolean updateData) {
		UpdateTimeEvent event = new UpdateTimeEvent(_currentTime, this, updateData, new Interval(_startTime, _endTime));
		for (ITimeChangeListener listener : _timeChangeListener) {
			listener.handleTimeIndexChanged(event);
		}
	}

	@Override
	public void setTimePeriod(DateTime start, DateTime end) {
		assert start.isBefore(end) : "start time < end time";

		_startTime = start;
		_endTime = end;

		Calendar startCal = start.toGregorianCalendar();
		Calendar endCal = end.toGregorianCalendar();

		if (startCal.after(_endTime)) {
			_endTime = end;
			_view.getEndTimeField().setSelectedTime(endCal);
			_view.getStartTimeField().setSelectedTime(startCal);
		} else {
			_view.getStartTimeField().setSelectedTime(startCal);
			_view.getEndTimeField().setSelectedTime(endCal);
		}

		_currentTime = start;
		_view.setSelectedTime(start);

		updateSliderInterval();
	}

	@Override
	public void handleTimeIndexChanged(UpdateTimeEvent updateTimeEvent) {
		if (updateTimeEvent.getEventSource() == this) {
			return;
		}

		_currentTime = updateTimeEvent.getTimeStamp();
		_view.setSelectedTime(updateTimeEvent.getTimeStamp());

		setSliderPosition(updateTimeEvent.getTimeStamp());
	}

	private class ChannelListenerUpdateTimer implements Runnable {

		private static final int UPDATE_INTERVAL = 1000;

		@Override
		public void run() {
			int millisPerUpdate = calculateMillisPerSecondFromPlayrate();
			
			DateTime newTimeTmp = _currentTime.plusMillis(millisPerUpdate);
			if (newTimeTmp.isAfter(_endTime)) {
				isRunning = false;
				_view.getPlayButton().setSelection(false);
				return;
			}
			
			_currentTime = newTimeTmp;
			_view.setSelectedTime(_currentTime);

			setSliderPosition(_currentTime);

			_display.timerExec(UPDATE_INTERVAL, this);

			updateTimeChangeListener(true);
		}

		private int calculateMillisPerSecondFromPlayrate() {
			float playRatio = _playRatio / HistoryControlView.PLAY_RATIO_DENOMINATOR_ONE;
			int millisPerSecond = (int) playRatio * UPDATE_INTERVAL; // the play ratio field allows only 3 digits after the comma -> after * 1000 no user relevant digits after the

			return millisPerSecond;
		}
	}

}
