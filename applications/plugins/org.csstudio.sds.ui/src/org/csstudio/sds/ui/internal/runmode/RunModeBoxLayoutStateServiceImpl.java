package org.csstudio.sds.ui.internal.runmode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.domain.desy.types.Tuple;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.graphics.Point;

public class RunModeBoxLayoutStateServiceImpl {

	private static final String RUN_MODE_BOX_LAYOUT_DATA_FILE = "runModeBoxLayoutData";

	private Map<Tuple<String, Map<String, String>>, RunModeBoxLayoutData> displayFileNameToLayoutData;
	

	public RunModeBoxLayoutStateServiceImpl() {
		File layoutDataPersistenceFile = getLayoutDataPersistenceFile();

		if (layoutDataPersistenceFile.exists()) {
			readLayoutDataFromPersistenceFile(layoutDataPersistenceFile);
		} else {
			createNewLayoutDataMap();
		}
	}

	public RunModeBoxLayoutData getBoxLayoutDataForDisplay(String displayName, Map<String, String> aliases) {
		return displayFileNameToLayoutData.get(new Tuple<String, Map<String, String>>(displayName, aliases));
	}
	
	public void setLayoutDataForDisplay(String displayName, Map<String, String> aliases, Point position, Point size, double zoomFactor) {
		RunModeBoxLayoutData layoutData = new RunModeBoxLayoutData(displayName, position, size, zoomFactor);
		displayFileNameToLayoutData.put(new Tuple<String, Map<String, String>>(displayName, aliases), layoutData);
		writeLayoutToPersistenceFile();
	}
	
	public void resetPositions() {
		createNewLayoutDataMap();
		writeLayoutToPersistenceFile();
	}

	private void createNewLayoutDataMap() {
		this.displayFileNameToLayoutData = new HashMap<Tuple<String,Map<String,String>>, RunModeBoxLayoutData>();
	}

	@SuppressWarnings("unchecked")
	private void readLayoutDataFromPersistenceFile(File layoutDataPersistenceFile) {
		ObjectInputStream mapInputStream = null;
		try {
			mapInputStream = new ObjectInputStream(new FileInputStream(
					layoutDataPersistenceFile));
			displayFileNameToLayoutData = (Map<Tuple<String, Map<String, String>>, RunModeBoxLayoutData>) mapInputStream
					.readObject();
		} catch (Throwable e) {
			e.printStackTrace();
			createNewLayoutDataMap();
		} finally {
			try {
				if (mapInputStream != null) {
					mapInputStream.close();
				}
			} catch (IOException e) {}
		}
	}

	private void writeLayoutToPersistenceFile() {
		ObjectOutputStream outputStream = null;
		try {
			File positionsPersistenceFile = getLayoutDataPersistenceFile();
			if(!positionsPersistenceFile.exists()) {
				positionsPersistenceFile.getParentFile().mkdirs();
				positionsPersistenceFile.createNewFile();
			}
			outputStream = new ObjectOutputStream(new FileOutputStream(
					positionsPersistenceFile));
			outputStream.writeObject(displayFileNameToLayoutData);

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

	private File getLayoutDataPersistenceFile() {
		File workspaceFile = ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toFile();

		File result = new File(workspaceFile, ".metadata/.plugins/"
				+ SdsUiPlugin.PLUGIN_ID + "/" + RUN_MODE_BOX_LAYOUT_DATA_FILE);

		return result;
	}
}
