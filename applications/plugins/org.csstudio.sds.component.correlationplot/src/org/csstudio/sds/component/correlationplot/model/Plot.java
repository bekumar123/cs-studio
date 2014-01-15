package org.csstudio.sds.component.correlationplot.model;

import java.util.Collection;
import java.util.List;


public interface Plot {

	void setPolynomials(List<Polynomial> polynomials);
	void setPolynomial(int index, Polynomial polynomial);
	void setPolynomialName(int index, String polynomialName);

	void setPolylines(List<Polyline> polylines);
	void setPolyline(int index, Polyline polyline);
	void setFieldOfWorkPolygon(FieldOfWork fieldOfWork);
	void setPlotValues(Collection<PlotValue> plotValues);
	void setWarning(String text);
	
	Axis getXAxis();
	Axis getYAxis();

	void onUpdatedConfiguration();
	
	void setStyleProvider(PlotStyleProvider styleProvider);
	void setMask(Polyline mask);
}
