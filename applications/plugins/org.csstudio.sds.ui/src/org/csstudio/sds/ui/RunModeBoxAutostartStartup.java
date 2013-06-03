package org.csstudio.sds.ui;

import org.csstudio.sds.ui.autostart.IRunModeBoxAutostartService;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;

public class RunModeBoxAutostartStartup implements IStartup {

	private IRunModeBoxAutostartService runModeBoxAutostartService;

	@Override
	public void earlyStartup() {
		runModeBoxAutostartService = SdsUiPlugin.getDefault().getRunModeBoxAutostartService();
		runModeBoxAutostartService.startRestoredRunModeBoxes();

		PlatformUI.getWorkbench().addWorkbenchListener(createWorkbenchShutdownListener());
	}
	
	private IWorkbenchListener createWorkbenchShutdownListener() {
		return new IWorkbenchListener() {
			@Override
			public boolean preShutdown(IWorkbench workbench, boolean forced) {
				runModeBoxAutostartService.writeBoxInputsToPersistenceFile();
				
				return true;
			}
			
			@Override
			public void postShutdown(IWorkbench workbench) {
			}
		};
	}



}
