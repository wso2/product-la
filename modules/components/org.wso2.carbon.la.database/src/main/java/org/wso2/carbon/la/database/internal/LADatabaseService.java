/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.la.database.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.wso2.carbon.la.commons.constants.LAConstants;
import org.wso2.carbon.la.commons.domain.LogGroup;
import org.wso2.carbon.la.commons.domain.LogStream;
import org.wso2.carbon.la.commons.domain.config.LAConfiguration;
import org.wso2.carbon.la.database.DatabaseService;
import org.wso2.carbon.la.database.exceptions.DatabaseHandlerException;
import org.wso2.carbon.la.database.exceptions.LAConfigurationParserException;
import org.wso2.carbon.la.database.internal.constants.SQLQueries;
import org.wso2.carbon.la.database.internal.ds.LocalDatabaseCreator;

import java.sql.*;
import java.util.*;

public class LADatabaseService implements DatabaseService {

    private static final Log logger = LogFactory.getLog(LADatabaseService.class);
    private LADataSource dbh;
    private LAConfiguration laConfig;
    private static final String DB_CHECK_SQL = "SELECT * FROM ML_PROJECT";

    public LADatabaseService() {

        LAConfigurationParser mlConfigParser = new LAConfigurationParser();
        try {
            laConfig = mlConfigParser.getLAConfiguration(LAConstants.LOG_ANALYZER_XML);
        } catch (LAConfigurationParserException e) {
            String msg = "Failed to parse machine-learner.xml file.";
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        }

        try {
            dbh = new LADataSource(laConfig.getDatasourceName());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

        String value = System.getProperty("setup");
        if (value != null) {
            LocalDatabaseCreator databaseCreator = new LocalDatabaseCreator(dbh.getDataSource());
            try {
                if (!databaseCreator.isDatabaseStructureCreated(DB_CHECK_SQL)) {
                    databaseCreator.createRegistryDatabase();
                } else {
                    logger.info("Machine Learner database already exists. Not creating a new database.");
                }
            } catch (Exception e) {
                String msg = "Error in creating the Machine Learner database";
                throw new RuntimeException(msg, e);
            }
        }
    }

    public LAConfiguration getLaConfiguration() {
        return laConfig != null ? laConfig : new LAConfiguration();
    }

    public void shutdown() throws DatabaseHandlerException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dbh.getDataSource().getConnection();
            statement = connection.prepareStatement("SHUTDOWN");
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseHandlerException("An error has occurred while shutting down the database: "
                    + e.getMessage(), e);
        } finally {
            // Close the database resources.
            LADatabaseUtils.closeDatabaseResources(connection, statement);
        }
    }

    @Override
    public int createLogGroup(LogGroup logGroup) throws DatabaseHandlerException {
        Connection connection = null;
        PreparedStatement createLogGroupStatement = null;
        int tenantId = logGroup.getTenantId();
        String username = logGroup.getUsername();
        String logGroupName = logGroup.getName();

        if (getLogGroup(logGroup.getName(), tenantId, username) != null) {
            throw new DatabaseHandlerException(String.format("Log Group [name] %s already exists.", logGroupName));
        }
        try {
            connection = dbh.getDataSource().getConnection();
            connection.setAutoCommit(false);
            createLogGroupStatement = connection.prepareStatement(SQLQueries.CREATE_LOG_GROUP, Statement.RETURN_GENERATED_KEYS);
            createLogGroupStatement.setString(1, logGroupName);
            createLogGroupStatement.setInt(2, tenantId);
            createLogGroupStatement.setString(3, username);
            int affectedRow = createLogGroupStatement.executeUpdate();
            connection.commit();

            if (affectedRow == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = createLogGroupStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Successfully created log group: " + logGroupName);
                    }
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Log Group creation failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LADatabaseUtils.rollBack(connection);
            throw new DatabaseHandlerException("Error occurred while inserting details of log group: " + logGroupName
                    + " to the database: " + e.getMessage(), e);
        } finally {
            // enable auto commit
            LADatabaseUtils.enableAutoCommit(connection);
            // close the database resources
            LADatabaseUtils.closeDatabaseResources(connection, createLogGroupStatement);
        }
    }

    @Override
    public void deleteLogGroup(String name, int tenantId, String username) throws DatabaseHandlerException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dbh.getDataSource().getConnection();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(SQLQueries.DELETE_LOG_GROUP);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, tenantId);
            preparedStatement.setString(3, username);
            preparedStatement.execute();
            connection.commit();
            if (logger.isDebugEnabled()) {
                logger.debug("Successfully deleted the log group: " + name);
            }
        } catch (SQLException e) {
            LADatabaseUtils.rollBack(connection);
            throw new DatabaseHandlerException("Error occurred while deleting the log group: " + name + ": "
                    + e.getMessage(), e);
        } finally {
            // enable auto commit
            LADatabaseUtils.enableAutoCommit(connection);
            // close the database resources
            LADatabaseUtils.closeDatabaseResources(connection, preparedStatement);
        }
    }

    @Override
    public LogGroup getLogGroup(String name, int tenantId, String username) throws DatabaseHandlerException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result;

        try {
            connection = dbh.getDataSource().getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(SQLQueries.GET_LOG_GROUP);
            statement.setString(1, name);
            statement.setInt(2, tenantId);
            statement.setString(3, username);
            result = statement.executeQuery();

            if (result.next()) {
                LogGroup logGroup = new LogGroup();
                logGroup.setName(result.getString(1));
                logGroup.setTenantId(result.getInt(2));
                logGroup.setUsername(result.getString(3));
                return logGroup;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseHandlerException("Error occurred while getting details of log group: " + name
                    + " to the database: " + e.getMessage(), e);
        } finally {
            // close the database resources
            LADatabaseUtils.closeDatabaseResources(connection, statement);
        }
    }

    @Override
    public List<String> getAllLogGroupNames(int tenantId, String username) throws DatabaseHandlerException {
        Connection connection = null;
        ResultSet result = null;
        PreparedStatement statement = null;
        List<String> groupnames = new ArrayList<>();
        try {
            connection = dbh.getDataSource().getConnection();
            statement = connection.prepareStatement(SQLQueries.GET_ALL_LOG_GROUP_NAMES);
            statement.setInt(1, tenantId);
            statement.setString(2, username);
            result = statement.executeQuery();
            while (result.next()) {
                groupnames.add(result.getString(1));
            }
            return groupnames;
        } catch (SQLException e) {
            throw new DatabaseHandlerException(" An error has occurred while extracting log group names for user : " + username, e);
        } finally {
            // Close the database resources.
            LADatabaseUtils.closeDatabaseResources(connection, statement, result);
        }
    }

    @Override
    public List<LogGroup> getAllLogGroups() throws DatabaseHandlerException {
        return null;
    }

    @Override
    public void createLogStream(LogStream logStream) throws DatabaseHandlerException {
        Connection connection = null;
        PreparedStatement createLogGroupStatement = null;
        String logStreamName = logStream.getName();
        try {
            connection = dbh.getDataSource().getConnection();
            connection.setAutoCommit(false);
            createLogGroupStatement = connection.prepareStatement(SQLQueries.CREATE_LOG_STREAM);
            createLogGroupStatement.setInt(2, logStream.getLogGroupId());
            createLogGroupStatement.setString(1, logStream.getName());
            createLogGroupStatement.execute();
            connection.commit();
            if (logger.isDebugEnabled()) {
                logger.debug("Successfully created log stream : " + logStreamName + "under groupId :"
                        + logStream.getLogGroupId());
            }
        } catch (SQLException e) {
            LADatabaseUtils.rollBack(connection);
            throw new DatabaseHandlerException("Error occurred while inserting details of log stream: " + logStreamName
                    + " to the database: " + e.getMessage(), e);
        } finally {
            // enable auto commit
            LADatabaseUtils.enableAutoCommit(connection);
            // close the database resources
            LADatabaseUtils.closeDatabaseResources(connection, createLogGroupStatement);
        }
    }

    @Override
    public void deleteLogStream(String name, int logGroupId) throws DatabaseHandlerException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dbh.getDataSource().getConnection();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(SQLQueries.DELETE_LOG_STREAM);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, logGroupId);
            preparedStatement.execute();
            connection.commit();
            if (logger.isDebugEnabled()) {
                logger.debug("Successfully deleted the log stream : " + name);
            }
        } catch (SQLException e) {
            LADatabaseUtils.rollBack(connection);
            throw new DatabaseHandlerException("Error occurred while deleting the log stream: " + name + ": "
                    + e.getMessage(), e);
        } finally {
            // enable auto commit
            LADatabaseUtils.enableAutoCommit(connection);
            // close the database resources
            LADatabaseUtils.closeDatabaseResources(connection, preparedStatement);
        }
    }

    @Override
    public List<String> getAllLogStreamNamesOfLogGroup(int logGroupId) throws DatabaseHandlerException {
        Connection connection = null;
        ResultSet result = null;
        PreparedStatement statement = null;
        List<String> groupnames = new ArrayList<>();
        try {
            connection = dbh.getDataSource().getConnection();
            statement = connection.prepareStatement(SQLQueries.GET_ALL_LOG_STREAM_NAMES);
            statement.setInt(1, logGroupId);
            result = statement.executeQuery();
            while (result.next()) {
                groupnames.add(result.getString(1));
            }
            return groupnames;
        } catch (SQLException e) {
            throw new DatabaseHandlerException(" An error has occurred while extracting log stream names for log " +
                    "groupId : " + logGroupId, e);
        } finally {
            // Close the database resources.
            LADatabaseUtils.closeDatabaseResources(connection, statement, result);
        }
    }

    @Override
    public boolean isStreamExists(String logStream, int tenantId, String username) throws DatabaseHandlerException {
        Connection connection = null;
        ResultSet result = null;
        PreparedStatement statement = null;
        try {
            connection = dbh.getDataSource().getConnection();
            statement = connection.prepareStatement(SQLQueries.GET_LOG_STREAM_ID);
            statement.setString(1, logStream);
            statement.setInt(2, tenantId);
            statement.setString(3, username);
            result = statement.executeQuery();
            while (result.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseHandlerException(" An error has occurred while extracting log stream names for log " , e);
        } finally {
            // Close the database resources.
            LADatabaseUtils.closeDatabaseResources(connection, statement, result);
        }
        return false;
    }

    @Override
    public List<String> getStreamMetaData(String logStream, int tenantId, String username) throws DatabaseHandlerException {
        Connection connection = null;
        ResultSet result = null;
        PreparedStatement statement = null;
        List<String> fieldList = null;
        try {
            connection = dbh.getDataSource().getConnection();
            statement = connection.prepareStatement(SQLQueries.GET_LOG_STREAM_FIELDS);
            statement.setString(1, logStream);
            statement.setInt(2, tenantId);
            statement.setString(3, username);
            result = statement.executeQuery();
            while (result.next()) {
                fieldList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(result.getString(1));
                for (int i=0; i<jsonArray.length(); i++) {
                    fieldList.add( jsonArray.getString(i) );
                }
            }
            return fieldList;
        } catch (SQLException e) {
            throw new DatabaseHandlerException(" An error has occurred while extracting log stream names for log " , e);
        } catch (JSONException e) {
            throw new DatabaseHandlerException(" A JSON error has occurred while extracting log stream names for log " , e);
        } finally {
            // Close the database resources.
            LADatabaseUtils.closeDatabaseResources(connection, statement, result);
        }
    }

    @Override
    public void insertLogStreamMetadata(String logStream, Set<String> fields, int tenantId, String username)
            throws DatabaseHandlerException {
        Connection connection = null;
        PreparedStatement updateLogStreamFields = null;
        try {
            connection = dbh.getDataSource().getConnection();
            connection.setAutoCommit(false);
            updateLogStreamFields = connection.prepareStatement(SQLQueries.INSERT_LOG_STREAM_FIELDS);
            updateLogStreamFields.setString(1, fields.toString());
            updateLogStreamFields.setString(2, logStream);
            updateLogStreamFields.setInt(3, tenantId);
            updateLogStreamFields.setString(4, username);
            updateLogStreamFields.execute();
            connection.commit();
            if (logger.isDebugEnabled()) {
                logger.debug("Successfully inserted the log stream");
            }
        } catch (SQLException e) {
            LADatabaseUtils.rollBack(connection);
            throw new DatabaseHandlerException("Error occurred while inserting the field list of log stream: "
                    + e.getMessage(), e);
        } finally {
            // enable auto commit
            LADatabaseUtils.enableAutoCommit(connection);
            // close the database resources
            LADatabaseUtils.closeDatabaseResources(connection, updateLogStreamFields);
        }
    }

    @Override
    public void updateLogStreamMetadata(String logStreamId, Set<String> fields, int tenantId, String username)
            throws DatabaseHandlerException {
        Connection connection = null;
        PreparedStatement updateLogStreamFields = null;
        try {
            connection = dbh.getDataSource().getConnection();
            connection.setAutoCommit(false);
            updateLogStreamFields = connection.prepareStatement(SQLQueries.UPDATE_LOG_STREAM_FIELDS);
            updateLogStreamFields.setString(1, fields.toString());
            updateLogStreamFields.setString(2, logStreamId);
            updateLogStreamFields.setInt(3, tenantId);
            updateLogStreamFields.setString(4, username);
            updateLogStreamFields.execute();
            connection.commit();
            if (logger.isDebugEnabled()) {
                logger.debug("Successfully updated the log stream");
            }
        } catch (SQLException e) {
            LADatabaseUtils.rollBack(connection);
            throw new DatabaseHandlerException("Error occurred while updating the field list of log stream: "
                    + e.getMessage(), e);
        } finally {
            // enable auto commit
            LADatabaseUtils.enableAutoCommit(connection);
            // close the database resources
            LADatabaseUtils.closeDatabaseResources(connection, updateLogStreamFields);
        }
    }
}
