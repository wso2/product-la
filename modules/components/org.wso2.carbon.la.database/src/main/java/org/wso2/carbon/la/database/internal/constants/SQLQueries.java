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

package org.wso2.carbon.la.database.internal.constants;

/**
 * A utility class to store SQL prepared statements
 */
public class SQLQueries {

    /*Log group related queries*/
    public static final String CREATE_LOG_GROUP = "INSERT INTO LA_LOG_GROUPS(NAME,TENANT_ID,USER_NAME) VALUES(?, ?, ?)";

    public static final String GET_LOG_GROUP = "SELECT * FROM LA_LOG_GROUPS WHERE NAME=? AND TENANT_ID=? AND USER_NAME=?";

    public static final String GET_ALL_LOG_GROUP_NAMES = "SELECT NAME FROM LA_LOG_GROUPS WHERE TENANT_ID=? AND USER_NAME=?";

    public static final String DELETE_LOG_GROUP = "DELETE FROM LA_LOG_GROUPS WHERE NAME=? AND TENANT_ID=? AND USER_NAME=?";

    /*Log Stream related queries*/
    public static final String CREATE_LOG_STREAM = "INSERT INTO LA_LOG_STREAMS(LOG_GROUP_ID, NAME) VALUES(?, ?)";

    public static final String GET_LOG_STREAMS = "SELECT * FROM LA_LOG_STREAMS WHERE NAME=? AND LOG_GROUP_ID=?";

    public static final String GET_ALL_LOG_STREAM_NAMES = "SELECT NAME FROM LA_LOG_STREAMS WHERE LOG_GROUP_ID=?";

    public static final String DELETE_LOG_STREAM = "DELETE FROM LA_LOG_STREAMS WHERE NAME=? AND LOG_GROUP_ID=?";

    public static final String GET_LOG_STREAM_ID = "SELECT ID FROM LA_LOG_STREAM_METADATA WHERE STREAM_ID=? AND TENANT_ID=? AND USER_NAME=?";

    public static final String GET_LOG_STREAM_FIELDS = "SELECT FIELDS FROM LA_LOG_STREAM_METADATA WHERE STREAM_ID=? AND TENANT_ID=? AND USER_NAME=?";

    public static final String INSERT_LOG_STREAM_FIELDS = "INSERT INTO LA_LOG_STREAM_METADATA (FIELDS, STREAM_ID, TENANT_ID, USER_NAME) VALUES (?,?,?,?)";

    public static final String UPDATE_LOG_STREAM_FIELDS = "UPDATE LA_LOG_STREAM_METADATA SET FIELDS=? WHERE STREAM_ID=? AND TENANT_ID=? AND USER_NAME=?";

    /*
     * private Constructor to prevent any other class from instantiating.
     */
    private SQLQueries() {
    }
}
