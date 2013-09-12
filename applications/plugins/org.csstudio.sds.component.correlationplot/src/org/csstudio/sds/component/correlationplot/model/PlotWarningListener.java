package org.csstudio.sds.component.correlationplot.model;

public interface PlotWarningListener {

	void onOutOfBounds();

	void onNearUpperBound();

	void onNearLowerBound();

	void onNoWarning();

}
