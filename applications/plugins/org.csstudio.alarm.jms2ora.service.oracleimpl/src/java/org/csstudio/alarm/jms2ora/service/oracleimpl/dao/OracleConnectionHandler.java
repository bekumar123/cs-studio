
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
 *
 */

package org.csstudio.alarm.jms2ora.service.oracleimpl.dao;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import org.csstudio.alarm.jms2ora.service.ConnectionInfo;
import org.csstudio.alarm.jms2ora.service.MessageArchiveConnectionException;
import org.csstudio.alarm.jms2ora.service.oracleimpl.Activator;
import org.csstudio.alarm.jms2ora.service.oracleimpl.internal.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oracle.jdbc.OracleDriver;
import oracle.jdbc.pool.OracleDataSource;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 19.08.2011
 */
public class OracleConnectionHandler {
    
    /** The class logger */
    private static Logger LOG = LoggerFactory.getLogger(OracleConnectionHandler.class);
    
    /**  */
    private ConnectionInfo conInfo;

    /**  */
    private OracleDataSource dataSource;
    
    /**  */
    private OracleDriver driver;
    
    /**  */
    private final ThreadLocal<Connection> connection;
    
    private boolean _autoCommit;

    /**
     * Constructor.
     */
    public OracleConnectionHandler(boolean autoCommit) {
        
        _autoCommit = autoCommit;
        connection = new ThreadLocal<Connection>();
        connection.set(null);
        
        boolean driverFound = false;
        
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            String clazz = drivers.nextElement().getClass().getName();
            if (clazz.contains("OracleDriver")) {
                driverFound = true;
            }
        }
        
        if (driverFound == false) {
            try {
                driver = new OracleDriver();
                DriverManager.registerDriver(driver);
            } catch (SQLException sqle) {
                LOG.error("Cannot register OracleDriver.");
            }
        }

        IPreferencesService prefs = Platform.getPreferencesService();
        String dbUrl = prefs.getString(Activator.getPluginId(), PreferenceConstants.DATABASE_URL, "", null);
        String userName = prefs.getString(Activator.getPluginId(), PreferenceConstants.DATABASE_USER, "", null);
        String password = prefs.getString(Activator.getPluginId(), PreferenceConstants.DATABASE_PASSWORD, "", null);

        conInfo = new ConnectionInfo(userName, password, dbUrl);

        try {
            dataSource = new OracleDataSource();
            dataSource.setUser(userName);
            dataSource.setPassword(password);
            dataSource.setURL(dbUrl);
        } catch (SQLException sqle) {
            LOG.error("[*** SQLException ***]: {}", sqle.getMessage());
        }
    }

    private void connect() throws MessageArchiveConnectionException {
        
        Connection con = connection.get();
        
        if (con != null) {
            try {
                con.close();
            } catch (SQLException sqle) {
                LOG.warn("[*** SQLException ***]: {}", sqle.getMessage());
            }
            
            connection.set(null);
        }
        
        try {
            con = dataSource.getConnection();
            con.setAutoCommit(_autoCommit);
            connection.set(con);
        } catch (SQLException sqle) {
            connection.set(null);
            LOG.error("[*** SQLException ***]: {}", sqle.getMessage());
            throw new MessageArchiveConnectionException(sqle.getMessage());
        }
    }
    
    public void disconnect() {
        
        Connection con = connection.get();
        
        if (con != null) {
            try {
                con.close();
            } catch (SQLException sqle) {
                LOG.warn("[*** SQLException ***]: {}", sqle.getMessage());
            }
            
            connection.set(null);
        }
    }
    
    public Connection getConnection() throws MessageArchiveConnectionException {
        
        Connection con = connection.get();
        if (con == null) {
            connect();
            con = connection.get();
        }
        
        return con;
    }

    /**
     * Returns the ConnectionInfo object that contains the user name, the password and the database URL
     * 
     * @return ConnectionInfo
     */
    public ConnectionInfo getConnectionInfo() {
        return this.conInfo;
    }
}
