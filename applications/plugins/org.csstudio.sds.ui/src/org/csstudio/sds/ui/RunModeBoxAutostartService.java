package org.csstudio.sds.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.csstudio.domain.desy.types.Tuple;
import org.csstudio.sds.internal.runmode.RunModeBoxInput;
import org.csstudio.sds.ui.autostart.IRunModeBoxAutostartService;
import org.csstudio.sds.ui.runmode.RunModeService;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;


public class RunModeBoxAutostartService implements IRunModeBoxAutostartService {

	private static final String RUN_MODE_BOXES_PERSTISTENCE_FILE_NAME = "openRunModeBoxes";

	private List<Tuple<String, Map<String, String>>> restoredBoxInputs;

	public RunModeBoxAutostartService() {
		readBoxInputsFromPersistenceFile();
	}

	@SuppressWarnings("unchecked")
	private void readBoxInputsFromPersistenceFile() {
		File boxInputDataPersistenceFile = getRunModeBoxesPersistenceFile();
		
		if (boxInputDataPersistenceFile.exists()) {
			ObjectInputStream mapInputStream = null;
			try {
				mapInputStream = new ObjectInputStream(new FileInputStream(
						boxInputDataPersistenceFile));

				restoredBoxInputs = (List<Tuple<String, Map<String, String>>>) mapInputStream.readObject();
			} catch (Throwable e) {
				restoredBoxInputs = new ArrayList<Tuple<String, Map<String, String>>>();
				e.printStackTrace();
			} finally {
				try {
					if (mapInputStream != null) {
						mapInputStream.close();
					}
				} catch (IOException e) {}
			}
		} else {
			restoredBoxInputs = new ArrayList<Tuple<String, Map<String, String>>>();
		}
	}
	
	public void startRestoredRunModeBoxes() {
		for (Tuple<String, Map<String, String>> restoredBoxInputTuple : restoredBoxInputs) {
			RunModeService.getInstance().openDisplayShellInRunMode(new Path(restoredBoxInputTuple.getFirst()), restoredBoxInputTuple.getSecond());
			try {
				// Wait a little while to prevent reopened displays from
				// connecting all at once
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
	}

	public void writeBoxInputsToPersistenceFile() {
		List<RunModeBoxInput> boxInputsToSave = RunModeService.getInstance().getAllRunModeBoxInputs(); 
		
		// Build file to save
		restoredBoxInputs = new ArrayList<Tuple<String,Map<String,String>>>(boxInputsToSave.size());
		
		for (RunModeBoxInput runModeBoxInput : boxInputsToSave) {
			restoredBoxInputs.add(new Tuple<String, Map<String,String>>(runModeBoxInput.getFilePath().toString(), runModeBoxInput.getAliases()));
		}
		
		ObjectOutputStream outputStream = null;
		try {
			File positionsPersistenceFile = getRunModeBoxesPersistenceFile();
			if(!positionsPersistenceFile.exists()) {
				positionsPersistenceFile.getParentFile().mkdirs();
				positionsPersistenceFile.createNewFile();
			}
			outputStream = new ObjectOutputStream(new FileOutputStream(
					positionsPersistenceFile));
			outputStream.writeObject(restoredBoxInputs);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {}
		}
	}

	private File getRunModeBoxesPersistenceFile() {
		File workspaceFile = ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toFile();

		File result = new File(workspaceFile, ".metadata/.plugins/"
				+ SdsUiPlugin.PLUGIN_ID + "/" + RUN_MODE_BOXES_PERSTISTENCE_FILE_NAME);

		return result;
	}

	@Override
	public boolean containsDisplay(String displayName) {
		boolean result = false;

		for (Tuple<String, Map<String, String>> boxInput : restoredBoxInputs) {
			if(boxInput.getFirst().equalsIgnoreCase(displayName)) {
				result = true;
				break;
			}
		}
		
		return result;
	}

}
