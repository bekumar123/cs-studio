package org.csstudio.dct.ui.workbenchintegration.adapter;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.ui.workbenchintegration.adapter.helper.LabelBuilder;
import org.csstudio.dct.util.AliasResolutionUtil;

/**
 * UI adapter for {@link IInstance}.
 * 
 * @author Sven Wende
 */
public final class InstanceWorkbenchAdapter extends BaseWorkbenchAdapter<IInstance> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object[] doGetChildren(IInstance instance) {
        List<Object> list = new ArrayList<Object>();
        list.addAll(instance.getInstances());
        list.addAll(instance.getRecords());
        return list.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetLabel(IInstance instance) {
        String name = AliasResolutionUtil.getNameFromHierarchy(instance) + " [" + instance.getPrototype().getName()
                + "]";
        return new LabelBuilder().createLabel(name, instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetIcon(IInstance instance) {
        return instance.getParent() instanceof IInstance ? "icons/instance_inherited.png" : "icons/instance.png";
    }

}
