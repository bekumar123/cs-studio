package de.c1wps.geneal.desy.domain.plant.service;

public interface IPVAddresseProvider {
	
	public String[] getAllPVAddresses();
	
	public void addUpdateListener(IPVAddressUpdateListener listener);

}
