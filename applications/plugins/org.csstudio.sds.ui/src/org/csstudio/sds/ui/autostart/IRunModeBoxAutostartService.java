package org.csstudio.sds.ui.autostart;

public interface IRunModeBoxAutostartService {

	boolean containsDisplay(String displayName);
	
	void startRestoredRunModeBoxes();
	
	void writeBoxInputsToPersistenceFile();
}
