package org.csstudio.config.ioconfig.view.treeview;

import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.FacilityDBO;

public interface ILoader {
    void setLoad(@Nonnull final List<FacilityDBO> load);

    List<FacilityDBO> getLoad();
}
