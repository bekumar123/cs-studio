package org.csstudio.dct.nameresolution.internal;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.nameresolution.FieldFunctionContentProposal;
import org.csstudio.dct.nameresolution.IFieldFunction;
import org.csstudio.dct.nameresolution.RecordFinder;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.eclipse.jface.fieldassist.IContentProposal;

import com.google.common.base.Strings;

/**
 * Implementation for the forwardlink() function.
 * 
 * @author Sven Wende
 * 
 */
public final class ForwardLinkFieldFunction implements IFieldFunction {

    /**
     * {@inheritDoc}
     */
    public String evaluate(String name, String[] parameters, IRecord record, String fieldName) throws Exception {
        IRecord r = RecordFinder.findRecordByPath(parameters[0], record.getContainer());

        String result = null;

        if (r != null) {
            result = AliasResolutionUtil.getEpicsNameFromHierarchy(r);
        } else {
            result = "%%% No Record found";
        }

        return result;
    }

    public List<IContentProposal> getParameterProposal(int parameterIndex, String[] knownParameters, IRecord record) {
        List<IContentProposal> result = new ArrayList<IContentProposal>();

        for (IRecord r : record.getContainer().getRecords()) {

            String description;
            String epicsName = AliasResolutionUtil.getEpicsNameFromHierarchy(r);
            if (!Strings.isNullOrEmpty(epicsName)) {
                description = "Epics-Name: " + epicsName;
            } else {
                description = AliasResolutionUtil.getNameFromHierarchy(r) + " Description";
            }

            // @formatter:off
            result.add(new FieldFunctionContentProposal(
			    AliasResolutionUtil.getNameFromHierarchy(r), 
			    AliasResolutionUtil.getNameFromHierarchy(r),
			    description, 0));
			   // @formatter:on
        }

        return result;
    }
}
