package org.csstudio.config.ioconfig.model.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.csstudio.config.ioconfig.model.pbmodel.SlaveCfgData;

import com.google.common.base.Preconditions;

public class SlaveCfgDataList implements Iterable<SlaveCfgData> {

    private final List<SlaveCfgData> slaveCfgDataList;

    private boolean wordSize;
    private boolean input;
    private boolean output;

    SlaveCfgDataList(final List<Integer> slaveCfgDatas) {

        Preconditions.checkNotNull(slaveCfgDatas, "slaveCfgDatas must not be null");

        this.slaveCfgDataList = buildSlaveCfgDataList(slaveCfgDatas);

        wordSize = true;
        input = false;
        output = false;

        for (final SlaveCfgData slaveCfgData : this.slaveCfgDataList) {
            wordSize &= slaveCfgData.isWordSize();
            input |= slaveCfgData.isInput();
            output |= slaveCfgData.isOutput();
        }

        if (slaveCfgDataList.isEmpty()) {
            wordSize = false;
        }

    }

    public boolean isWordSize() {
        return wordSize;
    }

    public boolean isInput() {
        return input;
    }

    public boolean isOutput() {
        return output;
    }

    @Override
    public Iterator<SlaveCfgData> iterator() {
        return slaveCfgDataList.iterator();
    }

    private List<SlaveCfgData> buildSlaveCfgDataList(final List<Integer> slaveCfgDatas) {

        List<SlaveCfgData> slaveCfgDataList = new ArrayList<SlaveCfgData>();

        final Iterator<Integer> iterator = slaveCfgDatas.iterator();
        while (iterator.hasNext()) {
            final Integer parameter = iterator.next();
            // Test Simple oder Special Header
            if (parameter != 0 && ((parameter & 0x30) == 0)) {
                int parameter1;
                if (iterator.hasNext()) {
                    parameter1 = iterator.next();
                    slaveCfgDataList.add(new SlaveCfgData(parameter, parameter1));

                }
            } else {
                slaveCfgDataList.add(new SlaveCfgData(parameter));
            }
        }

        return slaveCfgDataList;
    }
}
