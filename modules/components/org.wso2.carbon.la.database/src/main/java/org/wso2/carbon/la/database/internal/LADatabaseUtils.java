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

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.la.database.exceptions.DatabaseHandlerException;

import java.io.Reader;
import java.io.StringWriter;
import java.sql.*;

/**
 * This class contains utility methods for database resources.
 */
public class LADatabaseUtils {
    
    private static final Log log = LogFactory.getLog(LADatabaseUtils.class);

    /*
     * private Constructor to prevent any other class from instantiating.
     */
    private LADatabaseUtils() {
    }

    /**
     * Close a given set of database resources.
     *
     * @param connection Connection to be closed
     * @param preparedStatement PeparedStatement to be closed
     * @param resultSet ResultSet to be closed
     */
    public static void closeDatabaseResources(Connection connection, PreparedStatement preparedStatement,
            ResultSet resultSet) throws DatabaseHandlerException {
        // Close the resultSet
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.error("Could not close result set: " + e.getMessage(), e);
            }
        }
        // Close the statement
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                log.error("Database error. Could not close statement: " + e.getMessage(), e);
            }
        }
        // Close the connection
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("Database error. Could not close statement: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Close a given set of database resources.
     *
     * @param connection Connection to be closed
     * @param preparedStatement PeparedStatement to be closed
     */
    public static void closeDatabaseResources(Connection connection, PreparedStatement preparedStatement)
            throws DatabaseHandlerException {
        closeDatabaseResources(connection, preparedStatement, null);
    }

    /**
     * Close a given set of database resources.
     *
     * @param connection Connection to be closed
     */
    public static void closeDatabaseResources(Connection connection) throws DatabaseHandlerException {
        closeDatabaseResources(connection, null, null);
    }

    /**
     * Close a given set of database resources.
     *
     * @param preparedStatement PeparedStatement to be closed
     */
    public static void closeDatabaseResources(PreparedStatement preparedStatement) throws DatabaseHandlerException {
        closeDatabaseResources(null, preparedStatement, null);
    }
    
    /**
     * Close a given set of database resources.
     * 
     * @param preparedStatement Prepared statement to be closed
     * @param resultSet Result set to be closed
     * @throws DatabaseHandlerException
     */
    public static void closeDatabaseResources(PreparedStatement preparedStatement,ResultSet resultSet) throws DatabaseHandlerException {
        closeDatabaseResources(null, preparedStatement, resultSet);
    }

    /**
     * Roll-backs a connection.
     *
     * @param dbConnection Connection to be rolled-back
     */
    public static void rollBack(Connection dbConnection) throws DatabaseHandlerException {
        try {
            if (dbConnection != null) {
                dbConnection.rollback();
            }
        } catch (SQLException e) {
            throw new DatabaseHandlerException("An error occurred while rolling back transactions: " + e.getMessage(),
                    e);
        } finally {
            // Close the database resources.
            LADatabaseUtils.closeDatabaseResources(dbConnection);
        }
    }

    /**
     * Enables the auto-commit of a connection.
     *
     * @param dbConnection Connection of which the auto-commit should be enabled
     */
    public static void enableAutoCommit(Connection dbConnection) {
        try {
            if (dbConnection != null) {
                dbConnection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            log.warn("An error occurred while enabling autocommit: " + e.getMessage(), e);
        }
    }

    /**
     * Get String from Clob
     * @param clob {@link Clob} object
     * @return String representation of clob
     * @throws DatabaseHandlerException
     */
    public static String toString(Clob clob) throws DatabaseHandlerException {
        Reader in;
        try {
            in = clob.getCharacterStream();
            StringWriter w = new StringWriter();
            IOUtils.copy(in, w);
            return w.toString();
        } catch (Exception e) {
            throw new DatabaseHandlerException("Failed to convert clob to string");
        }
    }
    
}
