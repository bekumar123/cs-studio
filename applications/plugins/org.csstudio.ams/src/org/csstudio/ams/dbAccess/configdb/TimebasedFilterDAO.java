package org.csstudio.ams.dbAccess.configdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.DAO;

public class TimebasedFilterDAO extends DAO {

	private static final String TABLE_NAME = "AMS_Filter_Timebased";


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

	public static void copyFilter(Connection masterDB, Connection targetDB) throws SQLException {
		copyFilter(masterDB, targetDB, DB_BACKUP_SUFFIX);
	}

	public static void copyFilter(Connection masterDB, Connection targetDB,
			String masterDbSuffix) throws SQLException {
		copyFilter(masterDB, targetDB, masterDbSuffix, "");
	}


	public static void backupFilter(Connection masterDB) throws SQLException {
		copyFilter(masterDB, masterDB, "", DB_BACKUP_SUFFIX);
	}
	
	private static void copyFilter(Connection masterDB, Connection targetDB,
			String masterSuffix, String targetSuffix) throws SQLException {
		final String readQuery = "SELECT IFILTERREF,ITIMEOUT,ISTARTFILTERCONDITIONREF,ISTOPFILTERCONDITIONREF FROM " 
				+ TABLE_NAME + masterSuffix;
		final String writeQuery = "INSERT INTO " + TABLE_NAME + targetSuffix
				+ " (IFILTERREF,ITIMEOUT,ISTARTFILTERCONDITIONREF,ISTOPFILTERCONDITIONREF) VALUES(?,?,?,?)";
		
		ResultSet resultSet = null;
		PreparedStatement readStatement = null;
		PreparedStatement writeStatement = null;
		
		try {
			readStatement = masterDB.prepareStatement(readQuery);			
			writeStatement = targetDB.prepareStatement(writeQuery);
			
			resultSet = readStatement.executeQuery();
			
			while (resultSet.next()) {
				writeStatement.setInt(1, resultSet.getInt(1));
				writeStatement.setInt(2, resultSet.getInt(2));
				writeStatement.setInt(3, resultSet.getInt(3));
				writeStatement.setInt(4, resultSet.getInt(4));
				
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

}
