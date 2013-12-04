package org.csstudio.sds.history.domain.service;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariable;

public interface IPvInformationService {
	
	public ProcessVariable getProcessVariable(String controlSystemAddress);

}
