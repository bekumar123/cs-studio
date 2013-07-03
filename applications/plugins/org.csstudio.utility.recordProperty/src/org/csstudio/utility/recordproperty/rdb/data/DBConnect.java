package org.csstudio.utility.recordproperty.rdb.data;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.csstudio.utility.recordproperty.rdb.config.IOracleSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enables opening and closing of the connection for the given {@link IOracleSettings}.
 *
 * @author Alen Vrecko, Rok Povsic
 */
public class DBConnect {

	private static final Logger LOG = LoggerFactory.getLogger(RecordPropertyGetRDB.class);
	
	private IOracleSettings settings;
	private Connection connection;
	
	public static String record = "alarmTest:RAMPA_calc";
		
	public DBConnect(IOracleSettings _settings) {
		settings = _settings;
	}
	
	public void openConnection() {
        try {
    		LOG.debug("open connection");

            DriverManager.registerDriver(settings.getDriver());
        } catch (SQLException e) {
        	LOG.error("Error register driver " + e.getMessage());
        	e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection(settings.getConnection(),
                    settings.getUsername(), settings.getPassword());
        } catch (SQLException e) {
        	LOG.error("Error get connection " + e.getMessage());
            e.printStackTrace();
        }
    }
	
	public void closeConnection() {

        try {
        	LOG.debug("close connection");
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {

            throw new RuntimeException("Fail to close connection ", e);
        }
    }
	
	public ResultSet executeQuery(String query) throws SQLException {
        if (connection == null) {
            throw new RuntimeException("Cannot execute query while connection is not establised");
        }

        return connection.createStatement().executeQuery(query);
    }

	public Connection getConnection() {
        return connection;
    }

    public IOracleSettings getSettings() {
        return settings;
    }
}
