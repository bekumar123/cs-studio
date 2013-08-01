package org.csstudio.sds.history.view;

import org.joda.time.DateTime;

public interface HistoryControlViewListener {
	
	public void changedTimeInterval(DateTime start, DateTime end);
	
	public void changedPlayRatio(float playRatio);
	
	public void changedStepInterval(int secondsStep);
		
	public void play();
	
	public void pause();
	
	public void changedSliderPosition(int position, boolean stoppedChangingSelection);
	
	public void stepForward();
	
	public void stepBackward();
	
}
