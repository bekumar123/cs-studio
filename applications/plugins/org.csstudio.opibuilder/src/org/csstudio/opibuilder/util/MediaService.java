/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.DataFormatException;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

/**
 * A service help to maintain the color macros.
 * 
 * @author Xihui Chen
 * 
 */
public final class MediaService {

	public static final String DEFAULT_FONT = "Default"; //$NON-NLS-1$

	public static final String DEFAULT_BOLD_FONT = "Default Bold"; //$NON-NLS-1$
	
	public static final String HEADER1 = "Header 1"; //$NON-NLS-1$
	public static final String HEADER2 = "Header 2"; //$NON-NLS-1$
	public static final String HEADER3 = "Header 3"; //$NON-NLS-1$
	/**
	 * The shared instance of this class.
	 */
	private static MediaService instance = null;

	private Map<String, OPIColor> colorMap;
	private Map<String, OPIFont> fontMap;

	private IPath colorFilePath;
	private IPath fontFilePath;

	public final static RGB DEFAULT_UNKNOWN_COLOR = new RGB(0, 0, 0);

	public final static FontData DEFAULT_UNKNOWN_FONT = CustomMediaFactory.FONT_ARIAL;

	/**
	 * @return the instance
	 */
	public synchronized static final MediaService getInstance() {
		if (instance == null)
			instance = new MediaService();
		return instance;
	}

	public MediaService() {
		colorMap = new LinkedHashMap<String, OPIColor>();
		fontMap = new LinkedHashMap<String, OPIFont>();
		loadColorFile();
		loadFontFile();
	}

	private void loadPredefinedColors() {
		colorMap.put(AlarmRepresentationScheme.MAJOR, new OPIColor(
				AlarmRepresentationScheme.MAJOR, CustomMediaFactory.COLOR_RED,
				true));
		colorMap.put(AlarmRepresentationScheme.MINOR, new OPIColor(
				AlarmRepresentationScheme.MINOR,
				CustomMediaFactory.COLOR_ORANGE, true));
		colorMap.put(AlarmRepresentationScheme.INVALID, new OPIColor(
				AlarmRepresentationScheme.INVALID,
				CustomMediaFactory.COLOR_PINK, true));
		colorMap.put(AlarmRepresentationScheme.DISCONNECTED, new OPIColor(
				AlarmRepresentationScheme.DISCONNECTED,
				CustomMediaFactory.COLOR_PINK, true));
	}

	private void loadPredefinedFonts() {
		FontData defaultFont = Display.getDefault().getSystemFont()
				.getFontData()[0];
		// String osName = getOSName();
		//		if(osName.equals("linux_gtk")) //$NON-NLS-1$
		//			defaultFont = new FontData("Sans", 10, SWT.NORMAL); //$NON-NLS-1$
		//		else if(osName.equals("macosx")) //$NON-NLS-1$
		//			defaultFont = new FontData("Courier", 12, SWT.NORMAL);//$NON-NLS-1$

		fontMap.put(DEFAULT_FONT, new OPIFont(DEFAULT_FONT, defaultFont));
		FontData defaultBoldFont = new FontData(defaultFont.getName(), defaultFont.getHeight(), SWT.BOLD);
		fontMap.put(DEFAULT_BOLD_FONT, new OPIFont(DEFAULT_BOLD_FONT, defaultBoldFont));
		FontData header1 = new FontData(defaultFont.getName(), 18, SWT.BOLD);
		fontMap.put(HEADER1, new OPIFont(HEADER1, header1));
		FontData header2 = new FontData(defaultFont.getName(), 14, SWT.BOLD);
		fontMap.put(HEADER2, new OPIFont(HEADER2, header2));
		FontData header3 = new FontData(defaultFont.getName(), 12, SWT.BOLD);
		fontMap.put(HEADER3, new OPIFont(HEADER3, header3));

	}

	/**
	 * Reload color and font files.
	 */
	public synchronized void reload() {
		reloadColorFile();
		reloadFontFile();
	}

	/**
	 * Reload predefined colors from color file.
	 */
	public synchronized void reloadColorFile() {

		Job job = new Job("Load Color File") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Connecting to " + colorFilePath,
						IProgressMonitor.UNKNOWN);
				colorMap.clear();
				loadColorFile();
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	/**
	 * Reload predefined fonts from font file.
	 */
	public synchronized void reloadFontFile() {
		UIJob job = new UIJob("Load Font File") {	

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				monitor.beginTask("Connecting to " + fontFilePath,
						IProgressMonitor.UNKNOWN);
				fontMap.clear();
				loadFontFile();
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	/**
	 * @return true if load successfully.
	 */
	private void loadColorFile() {
		loadPredefinedColors();

		colorFilePath = PreferencesHelper.getColorFilePath();
		if (colorFilePath == null || colorFilePath.isEmpty()) {
//			String message = "No color definition file was found.";
//			ConsoleService.getInstance().writeInfo(message);
			return;
		}

		try {
			// read file
			InputStream inputStream = ResourceUtil.pathToInputStream(
					colorFilePath, false);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));
			String line;
			// fill the color map.
			while ((line = reader.readLine()) != null) {
				// support comments
				if (line.trim().startsWith("#") || line.trim().startsWith("//")) //$NON-NLS-1$ //$NON-NLS-2$
					continue;
				int i;
				if ((i = line.indexOf('=')) != -1) {
					String name = line.substring(0, i).trim();
					try {
						RGB color = StringConverter.asRGB(line.substring(i + 1)
								.trim());

						colorMap.put(name, new OPIColor(name, color, true));
					} catch (DataFormatException e) {
						String message = "Format error in color definition file.";
						OPIBuilderPlugin.getLogger().log(Level.WARNING,
								message, e);
						ConsoleService.getInstance().writeError(message);
					}
				}
			}
			inputStream.close();
			reader.close();
		} catch (Exception e) {
			String message = "Failed to read color definition file.";
			OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
			ConsoleService.getInstance().writeWarning(message);
		}
	}

	private void loadFontFile() {
		
		loadPredefinedFonts();	
		Map<String, OPIFont> rawFontMap = new LinkedHashMap<String, OPIFont>();
		Set<String> trimmedNameSet = new LinkedHashSet<String>();
		fontFilePath = PreferencesHelper.getFontFilePath();
		if (fontFilePath == null || fontFilePath.isEmpty()) {
//			String message = "No font definition file was found.";
//			ConsoleService.getInstance().writeInfo(message);
			return;
		}

		try {
			// read file
			InputStream inputStream = ResourceUtil.pathToInputStream(
					fontFilePath, false);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));
			String line;
			// fill the font map.
			while ((line = reader.readLine()) != null) {
				// support comments
				if (line.trim().startsWith("#") || line.trim().startsWith("//")) //$NON-NLS-1$ //$NON-NLS-2$
					continue;
				int i;
				if ((i = line.indexOf('=')) != -1) {
					String name = line.substring(0, i).trim();
					String trimmedName = name;
					if (name.contains("(")) //$NON-NLS-1$
						trimmedName = name.substring(0, name.indexOf("(")); //$NON-NLS-1$
					trimmedNameSet.add(trimmedName);
					try {
						FontData fontdata = StringConverter.asFontData(line
								.substring(i + 1).trim());
						if (fontdata.getName().equals("SystemDefault")) //$NON-NLS-1$
							fontdata.setName(Display.getDefault()
									.getSystemFont().getFontData()[0].getName());
						rawFontMap
								.put(name, new OPIFont(trimmedName, fontdata));
					} catch (DataFormatException e) {
						String message = "Format error in font definition file.";
						OPIBuilderPlugin.getLogger().log(Level.WARNING,
								message, e);
						ConsoleService.getInstance().writeError(message);
					}
				}
			}
			inputStream.close();
			reader.close();
		} catch (Exception e) {
			String message = "Failed to read font definition file.";
			OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
			ConsoleService.getInstance().writeWarning(message);
		}

		String osname = getOSName();
		for (String trimmedName : trimmedNameSet) {
			String equippedName = trimmedName + "(" + osname + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			if (rawFontMap.containsKey(equippedName))
				fontMap.put(trimmedName, rawFontMap.get(equippedName));
			else if (rawFontMap.containsKey(trimmedName))
				fontMap.put(trimmedName, rawFontMap.get(trimmedName));
		}

	}

	private String getOSName() {
		String osname = System.getProperty("os.name").trim(); //$NON-NLS-1$
		String wsname = Util.getWS().trim();
		osname = StringConverter.removeWhiteSpaces(osname).toLowerCase();
		if (wsname != null && wsname.length() > 0) {
			wsname = StringConverter.removeWhiteSpaces(wsname).toLowerCase();
			osname = osname + "_" + wsname;
		}
		return osname;
	}

	/**
	 * Get the color from the predefined color map, which is defined in the
	 * color file.
	 * 
	 * @param name
	 *            the predefined name of the color.
	 * @return the RGB color, or the default RGB value if the name doesn't exist
	 *         in the color file.
	 */
	public RGB getColor(String name) {
		if (colorMap.containsKey(name))
			return colorMap.get(name).getRGBValue();
		return DEFAULT_UNKNOWN_COLOR;
	}

	public OPIColor getOPIColor(String name) {
		return getOPIColor(name, DEFAULT_UNKNOWN_COLOR);
	}
	
	/**Get OPIColor based on name. If no such name exist, use the rgb value as its color.
	 * @param name name of OPIColor
	 * @param rgb rgb value in case the name is not exist.
	 * @return the OPIColor.
	 */
	public OPIColor getOPIColor(String name, RGB rgb) {
		if (colorMap.containsKey(name))
			return colorMap.get(name);
		return new OPIColor(name, rgb, true);
	}

	public OPIColor[] getAllPredefinedColors() {
		OPIColor[] result = new OPIColor[colorMap.size()];
		int i = 0;
		for (OPIColor c : colorMap.values()) {
			result[i++] = c;
		}
		return result;
	}

	/**
	 * Get the font from the predefined font map, which is defined in the font
	 * file.
	 * 
	 * @param name
	 *            the predefined name of the font.
	 * @return the FontData, or the default font if the name doesn't exist in
	 *         the font file.
	 */
	public FontData getFontData(String name) {
		if (fontMap.containsKey(name))
			return fontMap.get(name).getFontData();
		return DEFAULT_UNKNOWN_FONT;
	}
	
	/**Get the OPIFont by name, use {@link #DEFAULT_UNKNOWN_FONT} if no such a name is found.
	 * @param name
	 * @return
	 * @see #getOPIFont(String, FontData)
	 */
	public OPIFont getOPIFont(String name){
		return getOPIFont(name, DEFAULT_UNKNOWN_FONT);
	}
	

	/**Get the OPIFont based on name. Use the provided fontData if no
	 * such a name is found.
	 * @param name 
	 * @param fontData
	 * @return
	 */
	public OPIFont getOPIFont(String name, FontData fontData) {
		if (fontMap.containsKey(name))
			return fontMap.get(name);
		return new OPIFont(name, fontData);
	}

	public OPIFont[] getAllPredefinedFonts() {
		OPIFont[] result = new OPIFont[fontMap.size()];
		int i = 0;
		for (OPIFont c : fontMap.values()) {
			result[i++] = c;
		}
		return result;
	}

}
