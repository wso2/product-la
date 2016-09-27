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
package org.wso2.carbon.la.database;

import org.wso2.carbon.la.commons.domain.LogGroup;
import org.wso2.carbon.la.commons.domain.LogStream;
import org.wso2.carbon.la.commons.domain.config.LAConfiguration;
import org.wso2.carbon.la.database.exceptions.DatabaseHandlerException;

import java.util.List;
import java.util.Set;

public interface DatabaseService {

    /**
     * Returns LA Configuration.
     */
    public LAConfiguration getLaConfiguration();

    /**
     * Executes the SHUTDOWN statement.
     *
     * @throws DatabaseHandlerException
     */
    void shutdown() throws DatabaseHandlerException;

    int createLogGroup(LogGroup logGroup) throws DatabaseHandlerException;

    void deleteLogGroup(String name, int tenantId, String username) throws DatabaseHandlerException;

    LogGroup getLogGroup(String name, int tenantId, String username) throws DatabaseHandlerException;

    List<String> getAllLogGroupNames(int tenantId, String username) throws DatabaseHandlerException;

    List<LogGroup> getAllLogGroups() throws DatabaseHandlerException;

    void createLogStream(LogStream logStream) throws DatabaseHandlerException;

    void deleteLogStream(String name, int logGroupId) throws DatabaseHandlerException;

    List<String> getAllLogStreamNamesOfLogGroup(int logGroupId) throws DatabaseHandlerException;

    boolean isStreamExists(String logStream, int tenantId, String username) throws DatabaseHandlerException;

    List<String> getStreamMetaData(String logStream, int tenantId, String username) throws DatabaseHandlerException;

    void insertLogStreamMetadata(String logStream, Set<String> fields, int tenantId, String username)
            throws DatabaseHandlerException;

    void updateLogStreamMetadata(String logStream, Set<String> fields, int tenantId, String username)
            throws DatabaseHandlerException;

}
