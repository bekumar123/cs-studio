package org.csstudio.sds.component.correlationplot.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IWidgetModelFactory;

public class CorrelationPlotModelFactory implements IWidgetModelFactory {

	@Override
	public AbstractWidgetModel createWidgetModel() {
		return new CorrelationPlotModel();
	}

	@Override
	public Class<?> getWidgetModelType() {
		return CorrelationPlotModel.class;
	}

}
