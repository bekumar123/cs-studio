/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.archive.common.service.mysqlimpl.sample;

import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.COLUMN_AVG;
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.COLUMN_CHANNEL_ID;
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.COLUMN_MAX;
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.COLUMN_MIN;
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.COLUMN_SERVERTY;
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.COLUMN_STATUS;
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.COLUMN_TIME;
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.TAB_SAMPLE_M;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;

import com.google.common.base.Joiner;


/**
 * Batch queue handler for reduced data samples for minutes.
 *
 * @author bknerr
 * @since 20.07.2011
 */
public class MinuteReducedDataSampleBatchQueueHandler extends
                                                     AbstractReducedDataSampleBatchQueueHandler<MinuteReducedDataSample> {
     /**
     * Constructor.
     */
    public MinuteReducedDataSampleBatchQueueHandler(@Nonnull final String database) {
        super(MinuteReducedDataSample.class,
              createMinuteSqlStatementString(database),
              new LinkedBlockingQueue<MinuteReducedDataSample>());
    }

    @Nonnull
    private static String createMinuteSqlStatementString(@Nonnull final String database) {
        return createSqlStatementString(database, TAB_SAMPLE_M);
    }

    protected static final String VALUES_WILDCARD = "(?, ?, ?, ?, ?,?,?)";


    @Nonnull
    protected static String createSqlStatementString(@Nonnull final String database,
                                                     @Nonnull final String table) {
        final String sql =
                "INSERT IGNORE INTO " + database + "." + table +
                " (" + Joiner.on(",").join(COLUMN_CHANNEL_ID, COLUMN_TIME, COLUMN_AVG, COLUMN_MIN, COLUMN_MAX, COLUMN_STATUS,COLUMN_SERVERTY) +
                ") VALUES " + VALUES_WILDCARD;
            return sql;
    }

    /**wenhua neue spalt in DB
     * {@inheritDoc}
     */
    @Override
    protected void fillStatement(@Nonnull final PreparedStatement stmt,
                                 @Nonnull final MinuteReducedDataSample element)
                                 throws ArchiveDaoException{
        try{
        stmt.setInt(1, element.getChannelId().intValue());
        stmt.setLong(2, element.getTimestamp().getNanos());

        stmt.setDouble(3, element.getAvg());
        stmt.setDouble(4, element.getMin());
        stmt.setDouble(5, element.getMax());
        stmt.setInt(6, element.getStatus());
        stmt.setInt(7, element.getSeverty());
        }catch(final SQLException e){
            throw new ArchiveDaoException("Filling or adding of batch to prepared statement failed for " + element.getChannelId()+ element.getAvg() , e);
        }
    }

}
