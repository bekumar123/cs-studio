package org.csstudio.common.trendplotter.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.common.trendplotter.model.AxisConfig;
import org.csstudio.common.trendplotter.model.Model;
import org.csstudio.common.trendplotter.model.ModelItem;
import org.csstudio.common.trendplotter.model.PVItem;

public class AxisConfigurer {

    private Model _model;
    private Map<AxisConfig, AxisItems> _axisMap;

    public AxisConfigurer(Model model) {
        _model = model;
        createAxisItemsMapping();
        setMinMaxInAxisItems();
    }

    private void setMinMaxInAxisItems() {
        Set<AxisConfig> keySet = _axisMap.keySet();
        for (AxisConfig axisConfig : keySet) {
            AxisItems axisItems = _axisMap.get(axisConfig);
            for (PVItem item : axisItems.items) {
                if (axisItems.max == null) {
                    axisItems.max = item.getDisplayHighFromRecord();
                } else if (axisItems.max < item.getDisplayHighFromRecord()) {
                    axisItems.max = item.getDisplayHighFromRecord();
                }
                if (axisItems.min == null) {
                    axisItems.min = item.getDisplayLowFromRecord();
                } else if (axisItems.min > item.getDisplayLowFromRecord()) {
                    axisItems.min = item.getDisplayLowFromRecord();
                }
            }
        }
    }

    private void createAxisItemsMapping() {
        _axisMap = new HashMap<>();
        //add all axis in model to map
        int axisCount = _model.getAxisCount();
        for (int i=0; i<axisCount; i++) {
            _axisMap.put(_model.getAxis(i), new AxisItems());
        }
        //add PVItems to axis
        int itemCount = _model.getItemCount();
        for (int i=0; i<itemCount;i++) {
            ModelItem item = _model.getItem(i);
            if (item instanceof PVItem) {
                AxisItems axisItems = _axisMap.get(item.getAxis());
                axisItems.items.add((PVItem) item);
            }
        }
        return;
    }

    private class AxisItems {
        List<PVItem> items = new ArrayList<>();
        Double min = null;
        Double max = null;
    }

    public double getMin(AxisConfig axis) {
        return _axisMap.get(axis).min;
    }

    public double getMax(AxisConfig axis) {
        return _axisMap.get(axis).max;
    }
    
}
