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

import org.csstudio.sds.ui.SdsUiPlugin;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.graphics.Point;

public class RunModeBoxLayoutStateServiceImpl {

	private static final String RUN_MODE_BOX_LAYOUT_DATA_FILE = "runModeBoxLayoutData";

	private Map<String, RunModeBoxLayoutData> displayFileNameToLayoutData;
	

	@SuppressWarnings("unchecked")
	public RunModeBoxLayoutStateServiceImpl() {
		File layoutDataPersistenceFile = getLayoutDataPersistenceFile();

		if (layoutDataPersistenceFile.exists()) {
			ObjectInputStream mapInputStream = null;
			try {
				mapInputStream = new ObjectInputStream(new FileInputStream(
						layoutDataPersistenceFile));
				displayFileNameToLayoutData = (Map<String, RunModeBoxLayoutData>) mapInputStream
						.readObject();
			} catch (Throwable e) {
				e.printStackTrace();
				createNewLayoutMaps();
			} finally {
				try {
					if (mapInputStream != null) {
						mapInputStream.close();
					}
				} catch (IOException e) {}
			}
		} else {
			createNewLayoutMaps();
		}
	}
	
	public boolean existsPositionForDisplay(String displayName) {
		return displayFileNameToLayoutData.containsKey(displayName);
	}
	
	public Point getPositionForDisplay(String displayName) {
		Point result = null;
		
		RunModeBoxLayoutData runModeBoxLayoutData = displayFileNameToLayoutData.get(displayName);
		if(runModeBoxLayoutData != null) {
			result = runModeBoxLayoutData.getPosition(); 
		}
		
		return result;
	}
	
	public Point getSizeForDisplay(String displayName) {
		Point result = null;
		
		RunModeBoxLayoutData runModeBoxLayoutData = displayFileNameToLayoutData.get(displayName);
		if(runModeBoxLayoutData != null) {
			result = runModeBoxLayoutData.getSize(); 
		}
		
		return result;
	}
	
	public RunModeBoxLayoutData getBoxLayoutDataForDisplay(String displayName) {
		return displayFileNameToLayoutData.get(displayName);
	}
	
	public void setLayoutDataForDisplay(String displayName, Point position, Point size, double zoomFactor) {
		RunModeBoxLayoutData layoutData = new RunModeBoxLayoutData(displayName, position, size, zoomFactor);
		displayFileNameToLayoutData.put(displayName, layoutData);
		writeLayoutToPersistenceFile();
	}
	
	public void resetPositions() {
		createNewLayoutMaps();
		writeLayoutToPersistenceFile();
	}

	private void createNewLayoutMaps() {
		this.displayFileNameToLayoutData = new HashMap<String, RunModeBoxLayoutData>();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
