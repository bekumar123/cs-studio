package org.csstudio.dct.ui.workbenchintegration.adapter;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.ui.workbenchintegration.adapter.helper.LabelBuilder;

/**
 * UI adapter for {@link IPrototype}.
 * 
 * @author Sven Wende
 */
public final class PrototypeWorkbenchAdapter extends BaseWorkbenchAdapter<IPrototype> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object[] doGetChildren(IPrototype prototype) {
        List<Object> list = new ArrayList<Object>();
        list.addAll(prototype.getInstances());
        list.addAll(prototype.getRecords());
        return list.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetLabel(IPrototype prototype) {
        return new LabelBuilder().createLabel(prototype.getName(), prototype);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetIcon(IPrototype prototype) {
        return "icons/prototype.png";
    }

}
