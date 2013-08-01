package de.c1wps.geneal.desy.domain.plant.plantmaterials;

import java.util.List;

public interface IPVAttributeContainer {

	public void addAttribute(ProcessVariableAttribute attribute);

	public void removeAttribute(ProcessVariableAttribute pvAttribute);

	public List<ProcessVariableAttribute> getAttributes();

	public ProcessVariableAttribute getAttributeByName(
			PvAttributeNames attributeName);

	boolean hasAttribute(PvAttributeNames attributeName);

}
