package org.csstudio.dct.ui.workbenchintegration.adapter.helper;

import org.csstudio.dct.model.IDisabledRecordCounter;

public class LabelBuilder {

    public String createLabel(String name, IDisabledRecordCounter disabledRecordCounter) {
        int numberOfDisabledRecords = disabledRecordCounter.countDisabledRecords();
        if (numberOfDisabledRecords == 0) {
            return name;
        }
        return name + " [-" + numberOfDisabledRecords + "]";
    }
}
