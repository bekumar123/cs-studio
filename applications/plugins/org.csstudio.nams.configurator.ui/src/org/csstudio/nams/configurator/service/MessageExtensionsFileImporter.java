package org.csstudio.nams.configurator.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.nams.configurator.beans.MessageExtensionBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.logging.declaration.ILogger;

/**
 * Imports message extensions from *.extcsv files and stores them into the configuration db.<br />
 * <br />
 * Extcsv file format is defined as follows:<br />
 * The column separator is , (comma) <br />
 * A line starting with "##Tag" as it's first column defines the message keys (##Tag,key1,key2, ...)<br />
 * The following lines define message extensions for the name defined in their first column (pvname,value1,value2, ...)<br />
 * A "#" at the beginning of a line defines a comment<br />
 * <br />
 * <br />Example:
 * <pre># Hallo Welt
     
##TAG	,Tag1		,Tag2 

NameX	,ValueTag1x	,ValueTag2x   
NameY	,ValueTag1y	,ValueTag2y

# Kommentar

NameZ	,ValueTag1z     ,ValueTag2z   </pre>
 */
public class MessageExtensionsFileImporter {
	
	private static final String EXT_CSV_COMMENT_MARKER = "#";
	private static final String EXT_CSV_TAG_MARKER_LOWERCASE = "##tag";
	private static final String EXT_CSV_SEPERATOR = ",";

	private final ConfigurationBeanService configurationBeanService;
	private final ILogger logger;

	public MessageExtensionsFileImporter(ConfigurationBeanService configurationBeanService, ILogger logger) {
		this.configurationBeanService = configurationBeanService;
		this.logger = logger;
	}
	
	public void importMessageExtensions(File csvFile) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(csvFile));

			List<String> tags = readTags(reader);
			Map<String, Map<String, String>> messageExtensions = readAllMessageExtensions(tags, reader);
			
			MessageExtensionBean[] existingMessageExtensions = configurationBeanService.getMessageExtensionBeans();
			for (String pvName: messageExtensions.keySet()) {
				MessageExtensionBean messageExtensionBean = new MessageExtensionBean();
				for (MessageExtensionBean existingMessageExtensionBean : existingMessageExtensions) {
					if(pvName.equalsIgnoreCase(existingMessageExtensionBean.getPvName())) {
						messageExtensionBean = existingMessageExtensionBean;
					}
				}
				
				messageExtensionBean.setPvName(pvName);
				messageExtensionBean.setMessageExtensions(messageExtensions.get(pvName));
				configurationBeanService.save(messageExtensionBean);
			}
		} catch (FileNotFoundException e) {
			logger.logWarningMessage(this, e.getLocalizedMessage());
		} catch (IOException e) {
			logger.logWarningMessage(this, e.getLocalizedMessage());
		} catch (StorageError e) {
			logger.logWarningMessage(this, e.getLocalizedMessage());
		} catch (StorageException e) {
			logger.logWarningMessage(this, e.getLocalizedMessage());
		} catch (InconsistentConfigurationException e) {
			logger.logWarningMessage(this, e.getLocalizedMessage());
		}
		finally {
			try {
				reader.close();
			} catch (IOException e) {
				logger.logWarningMessage(this, e.getLocalizedMessage());
			}
		}
	}

	private Map<String, Map<String, String>> readAllMessageExtensions(List<String> tags, BufferedReader reader) throws IOException {
		Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();

		String line = null;
		while ((line = reader.readLine()) != null) {
			String[] columns = line.split(EXT_CSV_SEPERATOR);
			if (columns.length > 0 && (columns.length - 1) <= tags.size()) {
				String pvName = columns[0].trim();
				if (!pvName.isEmpty() && !pvName.startsWith(EXT_CSV_COMMENT_MARKER)) {
					Map<String, String> pvExtensions = new HashMap<String, String>();
					result.put(pvName, pvExtensions);

					for (int index = 1; index < columns.length; index++) {
						String key = tags.get(index - 1);
						String message = columns[index].trim();
						pvExtensions.put(key, message);
					}
				}
			}

		}
		return result;
	}

	private List<String> readTags(BufferedReader reader) throws IOException {
		List<String> result = new ArrayList<String>();
		
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.toLowerCase().startsWith(EXT_CSV_TAG_MARKER_LOWERCASE)) {
				String[] columns = line.split(EXT_CSV_SEPERATOR);
				for (int index = 1; index < columns.length; index++) {
					result.add(columns[index].trim());
				}
				break;
			}
		}
		
		return result;
	}
}
