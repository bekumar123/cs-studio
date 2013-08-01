package de.c1wps.geneal.desy.domain.plant.domainvalues;

import java.io.Serializable;

/**
 * Speichert den Staus von PlantSections und PVs. Wird genutzt, um z.B. in der
 * UI Inkonsistenzen darzustellen.
 * 
 * @author reiswich
 * @date 22.02.2011
 * 
 */
public enum PVConsistencyState implements Serializable {

	MODIFIED("modified"), DELETED("deleted"), INCONSISTENT("inconsistent"), CONSISTENT(
			"consistent");

	private String name;

	private PVConsistencyState(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "PVConsistencyState: " + name;
	}

}
