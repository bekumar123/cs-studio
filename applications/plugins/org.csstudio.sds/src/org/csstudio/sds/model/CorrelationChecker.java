package org.csstudio.sds.model;

public interface CorrelationChecker<T> {

	boolean checkValues(T value1, T value2); 
	
}
