/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.kuali.student.kplus2.databus.adapters;

import org.kuali.student.kplus2.databus.decorators.${service_class}UnsupportedOperationImpl;
import org.kuali.student.kplus2.databus.common.JdbcConnectionProvider;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.student.kplus2.databus.server.config.ServerConfig;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.MetaInfo;
import org.kuali.student.r2.common.dto.StatusInfo;
import org.kuali.student.r2.common.dto.ValidationResultInfo;
import org.kuali.student.r2.common.exceptions.AlreadyExistsException;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.exceptions.ReadOnlyException;
import org.kuali.student.r2.common.exceptions.VersionMismatchException;
import org.kuali.student.r2.common.util.RichTextHelper;
import org.kuali.student.r2.core.atp.dto.AtpAtpRelationInfo;
import org.kuali.student.r2.core.atp.dto.AtpInfo;
import org.kuali.student.r2.core.atp.dto.MilestoneInfo;
import ${service_package};
import org.kuali.student.r2.core.class1.type.dto.TypeInfo;
import ${service_constants_package};
import org.kuali.student.r2.core.search.dto.SearchRequestInfo;
import org.kuali.student.r2.core.search.dto.SearchResultInfo;

public class ${service_class}JenzabarImpl extends ${service_class}UnsupportedOperationImpl
        implements ${service_class}, JdbcConnectionProvider {

    public ${service_class}JenzabarImpl() {
    }

    /**
     * place for schools to add their own initialization code
     */
    public void init() {

    }

    /**
     * Expose the database connection
     *
     * @throws org.kuali.student.r2.common.exceptions.OperationFailedException
     */
    @Override
    public java.sql.Connection getJDBCConnection()
            throws OperationFailedException {
        Properties localProps = ServerConfig.getServerProperties();
        // pass dbProps to getConnection()
        Properties dbProps = new Properties();
        dbProps.setProperty("user", localProps.getProperty("jdbc.user"));
        dbProps.setProperty("password", localProps.getProperty("jdbc.password"));
        String dbURL = localProps.getProperty("jdbc.url");

        try {
            // Load the Connector/J driver
            Class.forName(localProps.getProperty("jdbc.driver"));
            Connection conn = DriverManager.getConnection(dbURL, dbProps);
            return conn;
        } catch (ClassNotFoundException e) {
            throw new OperationFailedException("unable to load jdbc driver", e);
        } catch (SQLException e) {
            System.err.println("jdbc.user=" + dbProps.getProperty("user"));
            System.err.println ("dbURL=" + dbURL);
            System.err.println ("SQLException.message=" + e.getMessage());
            System.err.println ("SQLException.sqlState=" + e.getSQLState());
            System.err.println(e.getStackTrace());
            throw new OperationFailedException("unable to get db connection" + e.getMessage(), e);
        }
    }

    protected String getAdminOrgId () {
        return null;
    }
    
    
    protected AtpInfo resultSet2AtpInfo(ResultSet rs) throws OperationFailedException {
        try {
            AtpInfo atpInfo = new AtpInfo();
            String sess = this.getSession(rs);
            atpInfo.setId(rs.getString("PROG")+"."+rs.getString("YR")+"."+sess);
            atpInfo.setCode(rs.getString("YR")+sess);
            atpInfo.setName(sess +" of " +rs.getString("YR"));
            atpInfo.setAdminOrgId (rs.getString("PROG"));
            atpInfo.setTypeKey(this.code2Type(sess));
            
            atpInfo.setStateKey(${service_class}Constants.ATP_OFFICIAL_STATE_KEY);
            atpInfo.setDescr(new RichTextHelper().fromPlain(sess+" of " +rs.getString("YR")));
            atpInfo.setStartDate(rs.getDate("BEG_DATE"));
            atpInfo.setEndDate(rs.getDate("end_date"));
            atpInfo.setMeta(new MetaInfo());
            atpInfo.getMeta().setCreateId("UNKNOWN_USER");
            atpInfo.getMeta().setUpdateId("UNKNOWN_USER");
            atpInfo.getMeta().setCreateTime(rs.getDate("last_add_date"));
            atpInfo.getMeta().setUpdateTime(rs.getDate("last_drop_date"));
            atpInfo.getMeta().setVersionInd(rs.getDate("last_drop_date") != null ? rs.getDate("last_drop_date").getTime() + "" : "");
            return atpInfo;
        } catch (SQLException ex) {
            throw new OperationFailedException(ex);
        }
    }

    protected String code2Type(String code) {
        if (code.equals("FA")) {
            return ${service_class}Constants.ATP_FALL_TYPE_KEY;
        }
        if (code.equals("WI")) {
            return ${service_class}Constants.ATP_WINTER_TYPE_KEY;
        }
        if (code.equals("SP")) {
            return ${service_class}Constants.ATP_SPRING_TYPE_KEY;
        }
        if (code.equals("SU")) {
            return ${service_class}Constants.ATP_SUMMER_TYPE_KEY;
        }
        return ${service_class}Constants.ATP_ADHOC_TYPE_KEY;
    }

    protected String getAtpSQL() {
        String query = "SELECT * "
                +"FROM acad_cal_rec"
                + " WHERE prog= ? and yr = ? and sess=?";
        return query;
    }

    @Override
    public AtpInfo getAtp(String atpId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        AtpInfo atpInfo;
        
        Connection conn = this.getJDBCConnection();
        if(!atpIdFormatValid(atpId))
        	throw new OperationFailedException("Check atpId format "+atpId);
        String query = this.getAtpSQL();
        try {
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, atpId.substring(0, 4)); //prog
            pStmt.setString(2, atpId.substring(atpId.indexOf('.') +1, atpId.lastIndexOf('.'))); //yr
            pStmt.setString(3, atpId.substring(atpId.lastIndexOf('.')+1)); //sess
            ResultSet rs = pStmt.executeQuery();
            if (!rs.next()) {
                throw new DoesNotExistException(atpId);
            }
            atpInfo = this.resultSet2AtpInfo(rs);
            if (rs.next()) {
                throw new OperationFailedException(atpId + " selected more than one row");
            }
            conn.close();
        } catch (SQLException e) {
            throw new OperationFailedException("Failed to get atp.", e);
        } finally {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new OperationFailedException("Failed to close.", e);
            }
        }
        return atpInfo;
    }
    
    private boolean atpIdFormatValid(String atpId) {
    	if(atpId.indexOf('.') == -1)
    		return false;
    	String[] splits = atpId.split("\\.");
    	if(splits.length != 3)
    		return false;
    	return true;
    }

    protected String convertAtpTypeKey2codePattern(String atpTypeKey) throws OperationFailedException {
        if (atpTypeKey.equals(${service_class}Constants.ATP_FALL_TYPE_KEY)) {
            return "FA%";
        }
        if (atpTypeKey.equals(${service_class}Constants.ATP_WINTER_TYPE_KEY)) {
            return "WI%";
        }
        if (atpTypeKey.equals(${service_class}Constants.ATP_SPRING_TYPE_KEY)) {
            return "SP%";
        }
        if (atpTypeKey.equals(${service_class}Constants.ATP_SUMMER_TYPE_KEY)) {
            return "SU%";
        }
        throw new OperationFailedException("Unsupported atpTypeKey " + atpTypeKey);
    }

    private String getSession(ResultSet rs) throws SQLException {
        String sess = rs.getString("SESS");
        if (sess != null) {
            sess = sess.trim();
        }
        return sess;
    }
    
    //
    // TODO service methods
    //

    @Override
    public List<TypeInfo> getSearchTypes(ContextInfo contextInfo)
            throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public TypeInfo getSearchType(String searchTypeKey, ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public SearchResultInfo search(SearchRequestInfo searchRequestInfo, ContextInfo contextInfo)
            throws MissingParameterException,
            InvalidParameterException,
            OperationFailedException,
            PermissionDeniedException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    

}
