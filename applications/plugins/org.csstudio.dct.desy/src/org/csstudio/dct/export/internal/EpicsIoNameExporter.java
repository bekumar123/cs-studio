package org.csstudio.dct.export.internal;

import java.util.ArrayList;
import java.util.Map;

import org.csstudio.dct.export.IExporter;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.ResolutionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EpicsIoNameExporter implements IExporter {

    private static final Logger LOG = LoggerFactory.getLogger(EpicsIoNameExporter.class);

	private static final String NEWLINE = "\r\n";

	ArrayList<IRecord> _recordList = new ArrayList<IRecord>(); 

	public String export(IProject project) {
		LOG.info("Export Io Record Name list for project: " + project.getName());
		for (IRecord r : project.getFinalRecords()) {
			_recordList.add(r);
		}
		return createOutput();
	}

	private String createOutput() {
		StringBuffer sb = new StringBuffer();
		sb.append(NEWLINE);
		sb.append("List of record name and io name : ");
		sb.append(NEWLINE);
		sb.append(NEWLINE);
		
		for (IRecord record : _recordList) {
			String epicsName = "";
			String inpString = "";
	        try {
				epicsName = ResolutionUtil.resolve(
				        AliasResolutionUtil.getEpicsNameFromHierarchy(record), record);
				Map<String, String> fields = ResolutionUtil.resolveFields(record);
				
				inpString = fields.get("INP");
//				for (String key : fields.keySet()) {
//					String v = fields.get(key) != null ? fields.get(key) : "";
//				}
			} catch (AliasResolutionException e) {
				LOG.error("Error resolving epics name " + e.getMessage());
			}
			Map<String, String> finalFields = record.getFinalFields();
			String inp = finalFields.get("INP");
			if (inp != null) {
				sb.append(epicsName).append(";").append(inpString);
				sb.append(NEWLINE);
			}
		}
		return sb.toString();
	}
}
