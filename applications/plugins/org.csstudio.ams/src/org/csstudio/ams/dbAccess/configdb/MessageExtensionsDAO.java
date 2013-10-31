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

	private static final String TABLE_NAME = "AMS_MSG_EXTENSIONS";


	public static void removeAll(Connection con) throws SQLException {
		removeAll(con, "");
	}

	public static void removeAllBackupFromMasterDB(Connection con) throws SQLException {
		removeAll(con, AmsConstants.DB_BACKUP_SUFFIX);
	}

	private static void removeAll(Connection con, String suffix) throws SQLException {

		final String query = "DELETE FROM " + TABLE_NAME + suffix;
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			st.executeUpdate();
		}
		catch(SQLException ex)
		{
			Log.log(Log.FATAL, ex);
			throw ex;
		}
		finally
		{
			close(st,null);
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
		final String readQuery = "SELECT CPVNAME,CMESSAGEKEY,CMESSAGEVALUE FROM " 
				+ TABLE_NAME + masterSuffix;
		final String writeQuery = "INSERT INTO " + TABLE_NAME + targetSuffix
				+ " (CPVNAME,CMESSAGEKEY,CMESSAGEVALUE) VALUES(?,?,?)";
		
		ResultSet resultSet = null;
		PreparedStatement readStatement = null;
		PreparedStatement writeStatement = null;
		
		try {
			readStatement = masterDB.prepareStatement(readQuery);			
			writeStatement = targetDB.prepareStatement(writeQuery);
			
			resultSet = readStatement.executeQuery();
			
			while (resultSet.next()) {
				writeStatement.setString(1, resultSet.getString(1));
				writeStatement.setString(2, resultSet.getString(2));
				writeStatement.setString(3, resultSet.getString(3));
				
				writeStatement.executeUpdate();
			}
		} catch (SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed: " + readQuery, ex);
			throw ex;
		} finally {
			close(readStatement, resultSet);
			close(writeStatement, null);
		}
	}

	
	public static HashMap<String, Map<String,String>> loadAllMessageExtensions(Connection connection) {
		HashMap<String, Map<String, String>> result = new HashMap<String, Map<String,String>>();
		try {
			PreparedStatement prepareStatement = connection.prepareStatement("SELECT CPVNAME,CMESSAGEKEY,CMESSAGEVALUE FROM " + TABLE_NAME);
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
