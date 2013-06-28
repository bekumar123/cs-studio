package org.csstudio.dct.export.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.csstudio.dct.export.IExporter;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.ResolutionUtil;

/**
 * Renders records in DB file syntax (as accepted by an IOC) but sorts them
 * according to the record name field.
 * 
 * @author Joerg Penning
 * 
 */
public final class SortedDbFileExporter implements IExporter {
    private static final String NEWLINE = "\r\n";
    private static final Comparator<IRecord> RecordComparator = new Comparator<IRecord>() {
        public int compare(IRecord o1, IRecord o2) {
            int result = 0;
            try {
                String epicsName1 = ResolutionUtil.resolve(AliasResolutionUtil.getEpicsNameFromHierarchy(o1), o1);
                String epicsName2 = ResolutionUtil.resolve(AliasResolutionUtil.getEpicsNameFromHierarchy(o2), o2);
                result = epicsName1.compareTo(epicsName2);
            } catch (AliasResolutionException e) {
                throw new RuntimeException("File creation cancelled: Could not resolve record name: " + e.getMessage());
            }
            return result;
        };
    };

    /**
     * {@inheritDoc}
     */
    public String render(IRecord record) {

        StringBuffer sb = new StringBuffer();
        sb.append("record(");
        sb.append(record.getType());
        sb.append(", \"");
        try {
            sb.append(ResolutionUtil.resolve(AliasResolutionUtil.getEpicsNameFromHierarchy(record), record));
        } catch (AliasResolutionException e) {
            sb.append("<" + e.getMessage() + ">");
        }

        sb.append("\") {");
        sb.append(NEWLINE);

        Map<String, String> fields = ResolutionUtil.resolveFields(record);

        for (String key : fields.keySet()) {
            String v = fields.get(key) != null ? fields.get(key) : "";
            v = v.trim();
            if (!v.equals(record.getDefaultFields().get(key))) {
                if (!v.isEmpty()) {
                    sb.append("   field(");
                    sb.append(key);
                    sb.append(", \"");
                    sb.append(v);
                    sb.append("\")");
                    sb.append(NEWLINE);
                }
            }
        }

        sb.append("}");

        return sb.toString();
    }

    public String export(IProject project) {
        StringBuffer sb = new StringBuffer();

        // sort the records according to the record name
        List<IRecord> sortedRecords = new ArrayList<IRecord>(project.getFinalRecords());

        // check for tunneling of exception from sorter
        try {
            Collections.sort(sortedRecords, RecordComparator);
            // now render the sorted records
            for (IRecord r : sortedRecords) {
                sb.append(render(r));
                sb.append("\r\n\r\n");
            }
        } catch (RuntimeException e) {
            sb.append("\r\n==========================\r\n");
            sb.append(e.getMessage());
            sb.append("\r\n==========================\r\n");
        }

        return sb.toString();
    }

}
