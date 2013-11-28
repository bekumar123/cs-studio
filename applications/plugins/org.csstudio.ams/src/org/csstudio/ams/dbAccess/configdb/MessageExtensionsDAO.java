package org.csstudio.ams.dbAccess.configdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.DAO;

public class MessageExtensionsDAO extends DAO {

	private static final String TABLE_NAME_EXTENSIONS = "AMS_MSG_EXTENSIONS";
	private static final String TABLE_NAME_PVS = "AMS_MSG_EXT_PVS";


	public static void removeAll(Connection con) throws SQLException {
		removeAll(con, "");
	}

	public static void removeAllBackupFromMasterDB(Connection con) throws SQLException {
		removeAll(con, AmsConstants.DB_BACKUP_SUFFIX);
	}

	private static void removeAll(Connection con, String suffix) throws SQLException {

		final String queryPvDelete = "DELETE FROM " + TABLE_NAME_PVS + suffix;
		final String queryExtensionDelete = "DELETE FROM " + TABLE_NAME_EXTENSIONS + suffix;
		PreparedStatement stPv = null;
		PreparedStatement stExt = null;
		
		try
		{
			stPv = con.prepareStatement(queryPvDelete);
			stPv.executeUpdate();
			stExt = con.prepareStatement(queryExtensionDelete);
			stExt.executeUpdate();
		}
		catch(SQLException ex)
		{
			Log.log(Log.FATAL, ex);
			throw ex;
		}
		finally
		{
			close(stPv,null);
			close(stExt,null);
		}		
	}

	public static void copyMessageExtensions(Connection masterDB, Connection targetDB) throws SQLException {
		copyMessageExtensions(masterDB, targetDB, DB_BACKUP_SUFFIX);
	}

	public static void copyMessageExtensions(Connection masterDB, Connection targetDB,
			String masterDbSuffix) throws SQLException {
		copyMessageExtensions(masterDB, targetDB, masterDbSuffix, "");
	}


	private static void copyMessageExtensions(Connection masterDB, Connection targetDB,
			String masterSuffix, String targetSuffix) throws SQLException {
		final String readPvQuery = "SELECT ID,CPVNAME,IGROUPREF FROM " 
				+ TABLE_NAME_PVS + masterSuffix;
		final String writePvQuery = "INSERT INTO " + TABLE_NAME_PVS + targetSuffix
				+ " (ID,CPVNAME,IGROUPREF) VALUES(?,?,?)";
		
		ResultSet resultSet = null;
		PreparedStatement readPvStatement = null;
		PreparedStatement writePvStatement = null;
		
		try {
			readPvStatement = masterDB.prepareStatement(readPvQuery);			
			writePvStatement = targetDB.prepareStatement(writePvQuery);
			
			resultSet = readPvStatement.executeQuery();
			
			while (resultSet.next()) {
				writePvStatement.setInt(1, resultSet.getInt(1));
				writePvStatement.setString(2, resultSet.getString(2));
				writePvStatement.setInt(3, resultSet.getInt(3));
				
				writePvStatement.executeUpdate();
			}
		} catch (SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed: " + readPvQuery, ex);
			throw ex;
		} finally {
			close(readPvStatement, resultSet);
			close(writePvStatement, null);
		}
		
		final String readExtensionsQuery = "SELECT IDREF,CMESSAGEKEY,CMESSAGEVALUE FROM " 
				+ TABLE_NAME_EXTENSIONS + masterSuffix;
		final String writeExtensionsQuery = "INSERT INTO " + TABLE_NAME_EXTENSIONS + targetSuffix
				+ " (IDREF,CMESSAGEKEY,CMESSAGEVALUE) VALUES(?,?,?)";
		
		ResultSet extensionsResultSet = null;
		PreparedStatement readExtensionStatement = null;
		PreparedStatement writeExtensionStatement = null;
		
		try {
			readExtensionStatement = masterDB.prepareStatement(readExtensionsQuery);			
			writeExtensionStatement = targetDB.prepareStatement(writeExtensionsQuery);
			
			extensionsResultSet = readExtensionStatement.executeQuery();
			
			while (extensionsResultSet.next()) {
				writeExtensionStatement.setInt(1, extensionsResultSet.getInt(1));
				writeExtensionStatement.setString(2, extensionsResultSet.getString(2));
				writeExtensionStatement.setString(3, extensionsResultSet.getString(3));
				
				writeExtensionStatement.executeUpdate();
			}
		} catch (SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed: " + readExtensionsQuery, ex);
			throw ex;
		} finally {
			close(readExtensionStatement, extensionsResultSet);
			close(writeExtensionStatement, null);
		}
	}

	/**
	 * Lädt alle Nachrichten-Erweiterungen aus der Datenbank als Map von PV-Name zu Key-Value-Paaren. Die Facility-Informationen werden hier nicht berücksichtigt.
	 * @param connection DB-Connection
	 */
	public static HashMap<String, Map<String,String>> loadAllMessageExtensions(Connection connection) {
		HashMap<String, Map<String, String>> result = new HashMap<String, Map<String,String>>();
		try {
			PreparedStatement prepareStatement = connection.prepareStatement("SELECT CPVNAME,CMESSAGEKEY,CMESSAGEVALUE FROM " + TABLE_NAME_EXTENSIONS
					+ " LEFT JOIN " + TABLE_NAME_PVS + " ON " + TABLE_NAME_EXTENSIONS + ".IDREF=" + TABLE_NAME_PVS + ".ID");
			ResultSet resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				String pvName = resultSet.getString(1);
				String messageKey = resultSet.getString(2);
				String messageValue = resultSet.getString(3);
				addMessageExtension(result, pvName, messageKey, messageValue);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	private static void addMessageExtension(HashMap<String, Map<String, String>> map, String pvName, String messageKey, String messageValue) {
		if (!map.containsKey(pvName)) {
			map.put(pvName, new HashMap<String, String>());
		}
		map.get(pvName).put(messageKey, messageValue);
	}


}
