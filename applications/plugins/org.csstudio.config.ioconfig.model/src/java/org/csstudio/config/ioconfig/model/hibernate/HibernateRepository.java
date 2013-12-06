package org.csstudio.config.ioconfig.model.hibernate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.AbstractNodeSharedImpl;
import org.csstudio.config.ioconfig.model.DBClass;
import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.PV2IONameMatcherModelDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.SensorsDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructureDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBOReadOnly;
import org.csstudio.config.ioconfig.model.service.internal.Channel4ServicesDBO;
import org.csstudio.config.ioconfig.model.types.ConfiguredModuleList;
import org.csstudio.config.ioconfig.model.types.GsdFileId;
import org.csstudio.config.ioconfig.model.types.ModuleId;
import org.csstudio.config.ioconfig.model.types.ModuleList;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * Implementation for a Hibernate Repository.
 * 
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.9 $
 * @since 03.06.2009
 */
public class HibernateRepository implements IRepository {

    /**
     * @author hrickens
     * @since 24.01.2012
     */
    private final class LoadChannelHibernateCallback implements IHibernateCallback {
        private final String _ioName;

        /**
         * Constructor.
         * 
         * @param ioName
         */
        protected LoadChannelHibernateCallback(@Nonnull final String ioName) {
            _ioName = ioName;
        }

        @SuppressWarnings("unchecked")
        @Override
        @CheckForNull
        public ChannelDBO execute(@Nonnull final Session session) {
            if (_ioName == null) {
                return null;
            }
            ChannelDBO channel;
            if (true) {
                channel = useTreeWalkWay(session);
            } else {
                /*
                 * TODO (hrickens)[25.01.2012]: Das ist der gewuenschte Weg.
                 * Funktioniert leider nicht da Hibernate mit der Momentanen
                 * konfiguration mit den Genarics nicht zurecht kommt. Es gibt
                 * vieleicht eine Moeglichkeit die Funktioniert die aber eine
                 * Konfiguration ueber XML und Subclasses noetig macht. (siehe
                 * z.b. Buch: Hibernate Das Praxisbuch fuer Entwickler /s.260
                 * Mappping-Beispiele)
                 */
                channel = useRegularWay(session);
            }
            return channel;
        }

        @CheckForNull
        private ChannelDBO useRegularWay(@Nonnull final Session session) {
            final Query createQuery = session.createQuery("select c from " + ChannelDBO.class.getName()
                    + " c where c.ioName like ?");
            createQuery.setString(0, _ioName);
            final ChannelDBO nodes = (ChannelDBO) createQuery.uniqueResult();
            return nodes;
        }

        @CheckForNull
        private ChannelDBO useTreeWalkWay(@Nonnull final Session session) {

            final ArrayList<BigDecimal> ids = climbTreeUp(_ioName, session);
            ChannelDBO channel = null;
            AbstractNodeSharedImpl<?, ?> nodeImpl = null;
            if (ids.size() == 7) {
                final BigDecimal remove = ids.remove(6);
                final Query createQuery = session.createQuery(String.format("select f from %s f where f.id = '%d'",
                        FacilityDBO.class.getName(), remove.intValue()));
                try {
                    nodeImpl = (AbstractNodeSharedImpl<?, ?>) createQuery.uniqueResult();
                    channel = climbeTreeDown2Channel(nodeImpl, ids, _ioName);
                } catch (final ObjectNotFoundException e) {
                    LOG.debug("Query: Object not found = " + createQuery.getQueryString());
                    channel = null;
                }
            }
            return channel;
        }

        @Nonnull
        private ArrayList<BigDecimal> climbTreeUp(@Nonnull final String ioName, @Nonnull final Session session) {
            final ArrayList<BigDecimal> ids = new ArrayList<BigDecimal>();
            SQLQuery createSQLQuery = session
                    .createSQLQuery(String
                            .format("select n.parent_id from DDB_PROFIBUS_Channel c, DDB_NODE n where c.ioName like '%s' AND c.id = n.id",
                                    ioName));
            Object uniqueResult = createSQLQuery.uniqueResult();
            if (uniqueResult instanceof BigDecimal) {
                BigDecimal parentId = (BigDecimal) uniqueResult;
                ids.add(parentId);
                while (parentId != null) {
                    createSQLQuery = session.createSQLQuery(String.format(
                            "select n.parent_id from DDB_NODE n where n.id = '%d'", parentId.intValue()));
                    uniqueResult = createSQLQuery.uniqueResult();
                    if (uniqueResult instanceof BigDecimal) {
                        parentId = (BigDecimal) uniqueResult;
                        ids.add(parentId);
                    } else {
                        parentId = null;
                    }
                }

            }
            return ids;
        }
    }

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.9 $
     * @since 08.04.2010
     */
    private static final class EpicsAddressHibernateCallback implements IHibernateCallback {
        private final String _ioName;

        protected EpicsAddressHibernateCallback(@Nonnull final String ioName) {
            _ioName = ioName;
        }

        @Nonnull
        @SuppressWarnings("unchecked")
        private List<String> doQuery(@Nonnull final Query query) {
            return query.list();
        }

        @SuppressWarnings("unchecked")
        @Override
        @Nonnull
        public String execute(@Nonnull final Session session) {
            final Query query = session.createQuery("select channel.epicsAddressString from "
                    + ChannelDBO.class.getName() + "  as channel where channel.ioName like '" + _ioName + "'");

            final List<String> channels = doQuery(query);
            if (channels.size() < 1) {
                return "%%% IO-Name (" + _ioName + ") NOT found! %%%";
            } else if (channels.size() > 1) {
                final StringBuilder sb = new StringBuilder("%%% IO-Name (");
                sb.append(_ioName);
                sb.append(" NOT Unique! %%% ");
                for (final String string : channels) {
                    sb.append(" ,");
                    sb.append(string);
                }
                return sb.toString();
            }
            return channels.get(0);
        }
    }

    protected static final Logger LOG = LoggerFactory.getLogger(HibernateRepository.class);

    private final IHibernateManager hibernateManager;

    /**
     * 
     * Constructor.
     * 
     * @param hibernateManager
     *            the hibernateManager or null for default manager.
     */
    public HibernateRepository(@CheckForNull final IHibernateManager hibernateManager) {
        if (hibernateManager != null) {
            this.hibernateManager = hibernateManager;
        } else {
            this.hibernateManager = HibernateManager.getInstance();
        }
    }

    @CheckForNull
    public final ChannelDBO climbeTreeDown2Channel(
            @SuppressWarnings("rawtypes") @Nonnull final AbstractNodeSharedImpl parentNode,
            @Nonnull final List<BigDecimal> ids, @Nonnull final String ioName) {
        ChannelDBO channel = null;
        if (ids.isEmpty()) {
            if (parentNode instanceof ChannelStructureDBO) {
                final ChannelStructureDBO parent = (ChannelStructureDBO) parentNode;
                channel = getChannelWithIOName(parent.getChildren(), ioName);
            }
        } else {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            final Set<AbstractNodeSharedImpl> nodes = parentNode.getChildren();
            final BigDecimal remove = ids.remove(ids.size() - 1);
            for (@SuppressWarnings("rawtypes")
            final AbstractNodeSharedImpl node : nodes) {
                if (node.getId() == remove.intValue()) {
                    channel = climbeTreeDown2Channel(node, ids, ioName);
                }
            }
        }
        return channel;
    }

    @CheckForNull
    private ChannelDBO getChannelWithIOName(@Nonnull final Set<ChannelDBO> nodes, @Nonnull final String ioName) {
        ChannelDBO channel = null;
        for (final ChannelDBO node : nodes) {
            if (node.getIoName().equals(ioName)) {
                channel = node;
            }
        }
        return channel;
    }

    @Override
    public final void close() {
        hibernateManager.closeSession();
    }

    /**
     * Get the Epics Address string to an IO Name. It the name not found return
     * the string '$$$ IO-Name NOT found! $$$'.
     * 
     * @param ioName
     *            the IO-Name.
     * @return the Epics Adress for the given IO-Name.
     */
    @Override
    @Nonnull
    public final String getEpicsAddressString(@Nonnull final String ioName) throws PersistenceException {
        final IHibernateCallback hibernateCallback = new EpicsAddressHibernateCallback(ioName);
        final String epicsAddressString = hibernateManager.executeAndCloseSession(hibernateCallback);
        return epicsAddressString == null ? "" : epicsAddressString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final List<String> getIoNames() throws PersistenceException {
        final IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @Override
            @SuppressWarnings("unchecked")
            @Nonnull
            public List<String> execute(@Nonnull final Session session) {
                final Query query = session.createQuery("select channel.ioName from " + ChannelDBO.class.getName()
                        + " as channel");
                final List<String> ioNames = query.list();
                return ioNames;
            }
        };
        final List<String> ioNameList = hibernateManager.executeAndCloseSession(hibernateCallback);
        return ioNameList == null ? new ArrayList<String>() : ioNameList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final List<String> getIoNames(@Nonnull final String iocName) throws PersistenceException {
        final IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @Override
            @SuppressWarnings("unchecked")
            @Nonnull
            public List<String> execute(@Nonnull final Session session) {
                // TODO: Der IOC name wird noch nicht mit abgefragt!
                final Query query = session.createQuery("select channel.ioName from " + ChannelDBO.class.getName()
                        + " as channel");
                final List<String> ioNames = query.list();
                return ioNames;
            }
        };
        List<String> ioNames = hibernateManager.executeAndCloseSession(hibernateCallback);
        if (ioNames == null) {
            ioNames = new ArrayList<String>();
        }
        return ioNames;
    }

    @CheckForNull
    @Nonnull
    public final List<Integer> getRootPath(final int id) throws PersistenceException {
        final IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @SuppressWarnings("unchecked")
            @Override
            @Nonnull
            public List<Integer> execute(@Nonnull final Session session) {
                int level = 0;
                int searchId = id;
                final String statment = "select node.parent_Id  from ddb_node node where node.id like ?";
                final List<Integer> rootPath = new ArrayList<Integer>();
                rootPath.add(searchId);
                final SQLQuery query = session.createSQLQuery(statment);
                while (searchId > 0) {
                    query.setInteger(0, searchId); // Zero-Based!
                    final BigDecimal uniqueResult = (BigDecimal) query.uniqueResult();
                    if (uniqueResult == null || level++ > 10) {
                        break;
                    }
                    searchId = uniqueResult.intValue();
                    rootPath.add(searchId);
                }
                return rootPath;
            }
        };
        final List<Integer> rootPath = hibernateManager.executeAndKeepSessionOpen(hibernateCallback);
        return rootPath == null ? new ArrayList<Integer>() : rootPath;
    }

    @Override
    @Nonnull
    public final String getShortChannelDesc(@Nonnull final String ioName) throws PersistenceException {
        final IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @Override
            @SuppressWarnings("unchecked")
            @Nonnull
            public String execute(@Nonnull final Session session) {
                final Query query = session.createQuery("select channel.description from " + ChannelDBO.class.getName()
                        + " as channel where channel.ioName like ?");
                query.setString(0, ioName); // Zero-Based!

                final List<String> descList = query.list();
                if (descList == null || descList.isEmpty()) {
                    return "";
                }
                final String string = descList.get(0);
                if (string == null || string.isEmpty()) {
                    return "";
                }
                final String[] split = string.split("[\r\n]");
                return split[0].length() > 40 ? split[0].substring(0, 40) : split[0];
            }
        };
        final String doInDevDBHibernateEager = hibernateManager.executeAndCloseSession(hibernateCallback);
        return doInDevDBHibernateEager == null ? "" : doInDevDBHibernateEager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isConnected() {
        return hibernateManager.isConnected();
    }

    @Override
    public <T> void refresh(final T object) throws PersistenceException {
        hibernateManager.executeAndKeepSessionOpen(new IHibernateCallback() {
            @SuppressWarnings("unchecked")
            @Override
            @Nonnull
            public T execute(@Nonnull final Session session) {
                session.evict(object);
                session.load(object, ((GSDFileDBO)object).getId());
                //session.refresh(object);
                return object;
            }

        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public final <T> List<T> load(@Nonnull final Class<T> clazz) throws PersistenceException {
        final IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @Override
            @SuppressWarnings("unchecked")
            @CheckForNull
            public List<T> execute(@Nonnull final Session session) {
                final Query query = session.createQuery("from " + clazz.getName());
                final List<T> nodes = query.list();
                return nodes.isEmpty() ? null : nodes;
            }
        };
        return hibernateManager.executeAndKeepSessionOpen(hibernateCallback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public final <T> T load(@Nonnull final Class<T> clazz, @Nonnull final Serializable id) throws PersistenceException {
        final IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @Override
            @SuppressWarnings("unchecked")
            @CheckForNull
            public T execute(@Nonnull final Session session) {
                final List<T> nodes = session.createQuery("select c from " + clazz.getName() + " c where c.id = " + id)
                        .list();
                return nodes.isEmpty() ? null : nodes.get(0);
            }
        };
        return hibernateManager.executeAndKeepSessionOpen(hibernateCallback);
    }

    @Override
    @CheckForNull
    public final ChannelDBO loadChannel(@Nullable final String ioName) throws PersistenceException {
        final IHibernateCallback hibernateCallback = new LoadChannelHibernateCallback(ioName);
        return hibernateManager.executeAndKeepSessionOpen(hibernateCallback);
    }

    @Override
    @CheckForNull
    public final Channel4ServicesDBO loadChannelWithInternId(@Nullable final String internId)
            throws PersistenceException {
        final IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @SuppressWarnings("unchecked")
            @Override
            @CheckForNull
            public Channel4ServicesDBO execute(@Nonnull final Session session) {
                if (internId == null) {
                    return null;
                }
                final Query createQuery = session.createQuery("select c from " + Channel4ServicesDBO.class.getName()
                        + " c where c.krykNo = '" + internId + "'");
                final Channel4ServicesDBO nodes = (Channel4ServicesDBO) createQuery.uniqueResult();
                return nodes;
            }
        };
        return hibernateManager.executeAndCloseSession(hibernateCallback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public final List<DocumentDBO> loadDocument() throws PersistenceException {
        return hibernateManager.executeAndUseDocumentSession(new IHibernateCallback() {
            @Override
            @SuppressWarnings("unchecked")
            @CheckForNull
            public List<DocumentDBO> execute(@Nonnull final Session session) {
                final Query query = session.createQuery("from " + DocumentDBO.class.getName()
                        + " where length(image) > 0");
                final List<DocumentDBO> nodes = query.list();

                return nodes.isEmpty() ? null : nodes;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public final List<PV2IONameMatcherModelDBO> loadPV2IONameMatcher(@Nullable final Collection<String> ioName)
            throws PersistenceException {
        final IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @SuppressWarnings("unchecked")
            @Override
            @CheckForNull
            public List<PV2IONameMatcherModelDBO> execute(@Nonnull final Session session) {
                if (ioName == null || ioName.isEmpty()) {
                    return null;
                }
                final StringBuilder statement = new StringBuilder("select pv from ").append(
                        PV2IONameMatcherModelDBO.class.getName()).append(" pv where ");
                boolean notFirst = false;
                for (final String string : ioName) {
                    if (notFirst) {
                        statement.append(" OR ");
                    }
                    statement.append(String.format("pv.epicsName = '%s'", string));
                    notFirst = true;
                }
                final Query createQuery = session.createQuery(statement.toString());
                final List<PV2IONameMatcherModelDBO> list = createQuery.list();
                return list;
            }
        };
        return hibernateManager.executeAndCloseSession(hibernateCallback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public final PV2IONameMatcherModelDBO loadIOName2PVMatcher(@Nullable final String ioName)
            throws PersistenceException {
        final IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @SuppressWarnings("unchecked")
            @Override
            @CheckForNull
            public PV2IONameMatcherModelDBO execute(@Nonnull final Session session) {
                if (ioName == null || ioName.isEmpty()) {
                    return null;
                }
                final StringBuilder statement = new StringBuilder("select pv from ").append(
                        PV2IONameMatcherModelDBO.class.getName()).append(" pv where ");
                statement.append(String.format("pv.ioName = '%s'", ioName));
                return (PV2IONameMatcherModelDBO) session.createQuery(statement.toString()).uniqueResult();
            }
        };
        return hibernateManager.executeAndCloseSession(hibernateCallback);
    }

    @Override
    @CheckForNull
    public final SensorsDBO loadSensor(@Nonnull final String ioName, @Nonnull final String selection)
            throws PersistenceException {
        final IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @Override
            @SuppressWarnings("unchecked")
            @CheckForNull
            public SensorsDBO execute(@Nonnull final Session session) {
                final String statment = "select" + " s" + " from " + SensorsDBO.class.getName() + " s" + ", "
                        + ChannelDBO.class.getName() + " c" + " where c.currentValue like s.id"
                        + " and c.ioName like '" + ioName + "'";
                final Query query = session.createQuery(statment);
                final List<SensorsDBO> sensors = query.list();
                return sensors == null || sensors.size() < 1 ? null : sensors.get(0);
            }
        };
        return hibernateManager.executeAndCloseSession(hibernateCallback);
    }

    @Override
    @Nonnull
    public final List<SensorsDBO> loadSensors(@Nonnull final String ioName) throws PersistenceException {
        final IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @Override
            @SuppressWarnings("unchecked")
            @Nonnull
            public List<SensorsDBO> execute(@Nonnull final Session session) {
                final Query query = session.createQuery("from " + SensorsDBO.class.getName()
                        + " as sensors where sensors.ioName like ?");
                query.setString(0, ioName); // Zero-Based!

                final List<SensorsDBO> sensors = query.list();
                return sensors;
            }
        };
        final List<SensorsDBO> sensors = hibernateManager.executeAndCloseSession(hibernateCallback);
        return sensors == null ? new ArrayList<SensorsDBO>() : sensors;
    }

    @Override
    @Nonnull
    public final ModuleList loadModules(@Nonnull final GsdFileId gsdFileId) throws PersistenceException {
        
        Preconditions.checkNotNull(gsdFileId, "gsdFileId must not be null");
        
        final IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @Override
            @SuppressWarnings("unchecked")
            @Nonnull
            public ModuleList execute(@Nonnull final Session session) {

                final Query query = session.createQuery("from " + GSDModuleDBO.class.getName()
                        + " as m where m.GSDFile.id = :gsdFileId");
                query.setParameter("gsdFileId", gsdFileId.getValue());
                List<GSDModuleDBO> result = query.list();

                List<ModuleId> configuredModules = new ArrayList<ModuleId>();
                               
                final Query configuredModulesQuery = session
                        //@formatter:off
                        .createSQLQuery("select distinct ddb_gsd_module.id from ddb_gsd_module, ddb_channel_prototype where " + 
                                        "  ddb_gsd_module.id = ddb_channel_prototype.gsdmodule_id" +
                                        "  and gsdfile_id = :gsdFileId");
                                        //@formatter:on
                
                configuredModulesQuery.setParameter("gsdFileId", gsdFileId.getValue());
                
                for (Object moduleId : configuredModulesQuery.list()) {
                    configuredModules.add(new ModuleId((BigDecimal) moduleId));
                }
                           
                List<GSDModuleDBOReadOnly> resultReadOnly = new ArrayList<GSDModuleDBOReadOnly>();
                
                for (GSDModuleDBO gsdModuleDBO : result) {
                    resultReadOnly.add(gsdModuleDBO);
                }
                
                ModuleList moduleList = new ModuleList(resultReadOnly, new ConfiguredModuleList(configuredModules));
                moduleList.sort();

                return moduleList;
            }
        };
        
        return hibernateManager.executeAndCloseSession(hibernateCallback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void removeGSDFiles(@Nonnull final GSDFileDBO gsdFile) throws PersistenceException {
        hibernateManager.executeAndKeepSessionOpen(new IHibernateCallback() {

            @SuppressWarnings("unchecked")
            @Override
            @CheckForNull
            public Object execute(@Nonnull final Session session) {
                session.delete(gsdFile);
                return null;
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <T extends DBClass> void removeNode(@Nonnull final T dbClass) throws PersistenceException {
        hibernateManager.executeAndKeepSessionOpen(new IHibernateCallback() {

            @Override
            @SuppressWarnings("unchecked")
            @CheckForNull
            public Object execute(@Nonnull final Session session) {
                session.delete(dbClass);
                return null;
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final DocumentDBO save(@Nonnull final DocumentDBO document) throws PersistenceException {

        hibernateManager.executeAndUseDocumentSession(new IHibernateCallback() {

            @SuppressWarnings("unchecked")
            @Override
            @Nonnull
            public DocumentDBO execute(@Nonnull final Session session) {
                session.saveOrUpdate(document);
                session.flush();
                return document;
            }

        });
        return document;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final GSDFileDBO save(@Nonnull final GSDFileDBO gsdFile) throws PersistenceException {

        hibernateManager.executeAndKeepSessionOpen(new IHibernateCallback() {

            @SuppressWarnings("unchecked")
            @Override
            @Nonnull
            public GSDFileDBO execute(@Nonnull final Session session) {
                session.saveOrUpdate(gsdFile);
                return gsdFile;
            }

        });
        return gsdFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final <T extends DBClass> T saveOrUpdate(@Nonnull final T dbClass) throws PersistenceException {
        try {
            hibernateManager.executeAndKeepSessionOpen(new IHibernateCallback() {

                @SuppressWarnings("unchecked")
                @Override
                @Nonnull
                public T execute(@Nonnull final Session session) {
                    session.saveOrUpdate(dbClass);
                    return dbClass;
                }

            });
            return dbClass;
        } catch (final HibernateException he) {
            LOG.warn("Save or update failed", he);
            final PersistenceException persistenceException = new PersistenceException(he);
            throw persistenceException;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final DocumentDBO update(@Nonnull final DocumentDBO document) throws PersistenceException {

        hibernateManager.executeAndKeepSessionOpen(new IHibernateCallback() {

            @SuppressWarnings("unchecked")
            @Override
            @Nonnull
            public DocumentDBO execute(@Nonnull final Session session) {
                document.setUpdateDate(new Date());
                session.update(document);
                session.flush();
                return document;
            }

        });
        return document;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final <T extends DBClass> T update(@Nonnull final T dbClass) throws PersistenceException {

        hibernateManager.executeAndKeepSessionOpen(new IHibernateCallback() {

            @Override
            @SuppressWarnings("unchecked")
            @Nonnull
            public T execute(@Nonnull final Session session) {
                dbClass.setUpdatedOn(new Date());
                session.update(dbClass);
                session.flush();
                return dbClass;
            }

        });
        return dbClass;
    }


}
