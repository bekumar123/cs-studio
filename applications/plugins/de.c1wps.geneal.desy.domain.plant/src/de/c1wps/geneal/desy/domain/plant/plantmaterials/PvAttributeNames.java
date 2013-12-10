package de.c1wps.geneal.desy.domain.plant.plantmaterials;

public enum PvAttributeNames {
	
	VAL, MIN, MAX, EGU, LO, LOLO, HI, HIHI, SEVR, ONAM, ZNAM, UNDEFINED, DESC, UNIT;
	
	public static PvAttributeNames getByName(String name) {
		try {
			return valueOf(name);
		}
		catch (IllegalArgumentException e) {
			return UNDEFINED;
		}
	}
}
