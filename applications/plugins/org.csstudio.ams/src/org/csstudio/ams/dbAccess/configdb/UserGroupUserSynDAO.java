
/*
 * Copyright (c) 2013 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.ams.dbAccess.configdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.DAO;

/**
 * @author mmoeller
 * @since 03.07.2013
 */
public class UserGroupUserSynDAO extends DAO {

    public static UserGroupUserTObject select(Connection con, int userGroupRef, int userRef)
            throws SQLException {

        final String query = "SELECT iUserGroupRef,iUserRef,iPos,sActive,cActiveReason,tTimeChange"
                           + " FROM AMS_UserGroup_User_Syn WHERE iUserGroupRef = ? and iUserRef = ?";

        ResultSet rs = null;
        PreparedStatement st = null;

        try {
            st = con.prepareStatement(query);
            st.setInt(1, userGroupRef);
            st.setInt(2, userRef);
            rs = st.executeQuery();

            if(rs.next()) {
                return new UserGroupUserTObject(rs.getInt(1),
                                                rs.getInt(2),
                                                rs.getInt(3),
                                                rs.getShort(4),
                                                rs.getString(5),
                                                getUtilDate(rs, 6));
            }

            return null;
        } catch(SQLException ex) {
            Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
            throw ex;
        } finally {
            close(st,rs);
        }
    }

    /**
     * Used by <code>Distributor.changeStatus()</code> only.
     * Updates <code>sActive,cActiveReason,tTimeChange</code>
     * by checking if <code>tTimeChange</code> has not changed.
     *
     * @param con
     * @param ugu
     * @return
     * @throws SQLException
     * @see #updateInTransaction(Connection, int, List)
     */
    public static boolean update(Connection con, UserGroupUserTObject ugu) throws SQLException {

        final String query = "UPDATE AMS_UserGroup_User_Syn SET sActive=?,cActiveReason=?,tTimeChange=?"
            + " WHERE iUserGroupRef = ? AND iUserRef = ? AND tTimeChange = ?";

        PreparedStatement st = null;

        try {

            Date sysDate = new Date();

            st = con.prepareStatement(query);
            st.setShort(    1, ugu.getActive());
            st.setString(   2, ugu.getActiveReason());
            setUtilDate(st, 3, sysDate);

            st.setInt(      4, ugu.getUserGroupRef());
            st.setInt(      5, ugu.getUserRef());
            setUtilDate(st, 6, ugu.getTimeChange());

            int ret = st.executeUpdate();
            if (ret > 0) {
                // set only if update o.k.
                ugu.setTimeChange(sysDate);
            }

            return ret > 0;
        } catch(SQLException ex) {
            Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
            throw ex;
        } finally {
            close(st,null);
        }
    }
}
