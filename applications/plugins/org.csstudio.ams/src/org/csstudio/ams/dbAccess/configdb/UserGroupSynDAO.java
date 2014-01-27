
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
import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.DAO;

/**
 * @author mmoeller
 * @since 03.07.2013
 */
public class UserGroupSynDAO extends DAO {

    public static UserGroupTObject select(Connection con, int userGroupID) throws SQLException {

        final String query = "SELECT iUserGroupId,iGroupRef,cUserGroupName,"
        		           + "sMinGroupMember,iTimeOutSec,sActive"
                           + " FROM AMS_UserGroup_Syn WHERE iUserGroupId = ?";

        ResultSet rs = null;
        PreparedStatement st = null;

        try {
            st = con.prepareStatement(query);
            st.setInt(1, userGroupID);
            rs = st.executeQuery();

            if(rs.next()) {
                return new UserGroupTObject(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getShort(4), rs.getInt(5), rs.getInt(6));
            }

            return null;
        } catch(SQLException ex) {
            Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
            throw ex;
        } finally {
            close(st,rs);
        }
    }
}
