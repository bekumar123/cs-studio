package org.csstudio.config.ioconfig.view.treeview;

import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.DDB_PASSWORD;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.DDB_USER_NAME;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.DIALECT;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.HIBERNATE_CONNECTION_DRIVER_CLASS;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.HIBERNATE_CONNECTION_URL;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateDBPreferenceChangeListener implements IPreferenceChangeListener {

    protected static final Logger LOG = LoggerFactory.getLogger(DBLoaderJob.class);

    private final TreeViewer treeViewer;
    private final ILoader loader;

    public HibernateDBPreferenceChangeListener(@Nonnull TreeViewer treeViewer, ILoader loader) {
        this.treeViewer = treeViewer;
        this.loader = loader;
    }

    @Override
    public void preferenceChange(@Nonnull final PreferenceChangeEvent event) {
        final String property = event.getKey();
        if (property.equals(DDB_PASSWORD) || property.equals(DDB_USER_NAME) || property.equals(DIALECT)
                || property.equals(HIBERNATE_CONNECTION_DRIVER_CLASS) || property.equals(HIBERNATE_CONNECTION_URL)) {
            try {
                final List<FacilityDBO> load = Repository.load(FacilityDBO.class);
                loader.setLoad(load);
            } catch (final PersistenceException e) {
                loader.setLoad(new ArrayList<FacilityDBO>());
                DeviceDatabaseErrorDialog.open(null, "Can't read from Database! Database Error.", e);
                LOG.error("Can't read from Database! Database Error.", e);
            }
            treeViewer.getTree().removeAll();
            treeViewer.setInput(loader.getLoad());
            treeViewer.refresh(false);
        }
    }
}
