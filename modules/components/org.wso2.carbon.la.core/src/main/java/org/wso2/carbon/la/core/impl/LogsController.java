/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.la.core.impl;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.wso2.carbon.analytics.api.AnalyticsDataAPI;
import org.wso2.carbon.analytics.datasource.commons.AnalyticsSchema;
import org.wso2.carbon.analytics.datasource.commons.ColumnDefinition;
import org.wso2.carbon.analytics.datasource.commons.exception.AnalyticsException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.event.stream.core.EventStreamService;
import org.wso2.carbon.la.commons.constants.LAConstants;
import org.wso2.carbon.la.commons.domain.LogGroup;
import org.wso2.carbon.la.commons.domain.LogStream;
import org.wso2.carbon.la.core.exceptions.LogsControllerException;
import org.wso2.carbon.la.core.utils.LACoreServiceValueHolder;
import org.wso2.carbon.la.database.DatabaseService;
import org.wso2.carbon.la.database.exceptions.DatabaseHandlerException;

import java.util.*;

public class LogsController {

    private static final Log log = LogFactory.getLog(LogsController.class);

    private DatabaseService databaseService;

    public LogsController() {
        databaseService = LACoreServiceValueHolder.getInstance().getDatabaseService();

    }

    public int createLogGroup(LogGroup logGroup) throws LogsControllerException {
        try {
            return databaseService.createLogGroup(logGroup);
        } catch (DatabaseHandlerException e) {
            throw new LogsControllerException(e.getMessage(), e);
        }
    }

    public void deleteLogGroup(String name, int tenantId, String username) throws LogsControllerException {
        try {
            databaseService.deleteLogGroup(name, tenantId, username);
            if (log.isDebugEnabled()) {
                log.debug("Log Group deleted : " + name);
            }
        } catch (DatabaseHandlerException e) {
            throw new LogsControllerException(e.getMessage(), e);
        }
    }

    public List<String> getAllLogGroupNames(int tenantId, String username) throws LogsControllerException {
        try {
            List<String> logGroupList = databaseService.getAllLogGroupNames(tenantId, username);
            return logGroupList;
        } catch (DatabaseHandlerException e) {
            throw new LogsControllerException(e.getMessage(), e);
        }
    }

    public void createLogStream(LogStream logStream) throws LogsControllerException {
        try {
            databaseService.createLogStream(logStream);
            if (log.isDebugEnabled()) {
                log.debug("Log Stream created : " + logStream.getName());
            }
        } catch (DatabaseHandlerException e) {
            throw new LogsControllerException(e.getMessage(), e);
        }
    }

    public void deleteLogStream(String name, int logGroupId) throws LogsControllerException {
        try {
            databaseService.deleteLogStream(name, logGroupId);
            if (log.isDebugEnabled()) {
                log.debug("Log stream deleted : " + name + " log groupId : " + logGroupId);
            }
        } catch (DatabaseHandlerException e) {
            throw new LogsControllerException(e.getMessage(), e);
        }
    }

    public List<String> getAllLogStreamNames(int logGroupId) throws LogsControllerException {
        try {
            List<String> logStreamList = databaseService.getAllLogStreamNamesOfLogGroup(logGroupId);
            return logStreamList;
        } catch (DatabaseHandlerException e) {
            throw new LogsControllerException(e.getMessage(), e);
        }
    }

    public List<String> getStreamMetaData(String logStream, int tenantId, String username) throws LogsControllerException {
        try {
            List<String> logStreamList = databaseService.getStreamMetaData(logStream, tenantId, username);
            return logStreamList;
        } catch (DatabaseHandlerException e) {
            throw new LogsControllerException(e.getMessage(), e);
        }
    }

    /**
     * @param rawEvent
     * @param tenantId
     * @param username
     * @throws LogsControllerException
     */
    public void publishLogEvent(Map<String, Object> rawEvent, int tenantId, String username) throws LogsControllerException {

        if (!rawEvent.containsKey(LAConstants.LOG_STREAM)) {
            if (log.isDebugEnabled()) {
                log.debug("logstream doesn't exist in the event, hence publishing to default stream");
            }
            rawEvent.put(LAConstants.LOG_STREAM, LAConstants.DEFAULT_STREAM);
        }

        long timestamp = extractTimeStamp(rawEvent);

        String logStreamId = (String) rawEvent.get(LAConstants.LOG_STREAM);
        rawEvent.remove(LAConstants.LOG_STREAM);
        rawEvent.remove(LAConstants.LOG_TIMESTAMP);
        rawEvent.remove(LAConstants.LOG_TIMESTAMP_LONG);

        //TODO: add tags support
        Map<String, String> filteredEvent = getStringStringMap(rawEvent);
        Map<String, ColumnDefinition> newArbitraryColumns = getNewArbitraryFields(filteredEvent, username);
        //Update table schema and Stream metadata if new fields are present in the event
        if (newArbitraryColumns.size() > 0) {
            updateSchema(newArbitraryColumns, username);
            updateLogStreamMetadata(logStreamId, newArbitraryColumns.keySet(), tenantId, username);
        }

        EventStreamService eventStreamService = LACoreServiceValueHolder.getInstance().getEventStreamService();
        if (eventStreamService != null) {
            Event logEvent = new Event();
            logEvent.setTimeStamp(System.currentTimeMillis());
            logEvent.setStreamId(LAConstants.LOG_ANALYZER_STREAM_ID);
            logEvent.setTimeStamp(timestamp);
            logEvent.setPayloadData(new Object[]{logStreamId});
            logEvent.setArbitraryDataMap(filteredEvent);
            eventStreamService.publish(logEvent);
            if (log.isDebugEnabled()) {
                log.debug("Successfully published event " + logEvent.toString());
            }
        }

    }

    private Long extractTimeStamp(Map<String, Object> rawEvent) throws LogsControllerException {
        long logTimeStamp = 0;
        if (rawEvent.containsKey(LAConstants.LOG_TIMESTAMP)) {
            String timeStamp = (String) rawEvent.get(LAConstants.LOG_TIMESTAMP);
            try {
                DateTime dateTime = new DateTime(timeStamp);
                logTimeStamp = dateTime.getMillis();
            } catch (IllegalArgumentException e) {
                throw new LogsControllerException("Invalid timestamp format : " + timeStamp
                        + "user ISO8601 standard compatible timestamp", e);
            }
        } else if (rawEvent.containsKey(LAConstants.LOG_TIMESTAMP_LONG)) {
            try {
                logTimeStamp = (long) rawEvent.get(LAConstants.LOG_TIMESTAMP_LONG);
            } catch (ClassCastException e) {
                throw new LogsControllerException("Class cast exception occurred while converting LOG_TIMESTAMP_LONG"
                        , e);
            }
        } else {
            logTimeStamp = System.currentTimeMillis();
        }
        return logTimeStamp;
    }

    private void updateLogStreamMetadata(String logStreamId, Set<String> strings, int tenantId, String username)
            throws LogsControllerException {
        try {
            if (databaseService.isStreamExists(logStreamId, tenantId, username)) {
                databaseService.updateLogStreamMetadata(logStreamId, strings, tenantId, username);
            } else {
                databaseService.insertLogStreamMetadata(logStreamId, strings, tenantId, username);
            }
        } catch (DatabaseHandlerException e) {
            throw new LogsControllerException(e.getMessage(), e);
        }
    }

    private Map<String, String> getStringStringMap(Map<String, Object> rawEvent) throws LogsControllerException {
        Map<String, String> filteredEvent = new HashMap<>();
        for (Map.Entry<String, Object> entry : rawEvent.entrySet()) {
            try {
                filteredEvent.put(entry.getKey(), entry.getValue().toString());
            } catch (ClassCastException e) {
                throw new LogsControllerException("Unsupported datatype present in the log event" + e.getMessage(), e);
            }
        }
        return filteredEvent;
    }

    /**
     * Update the stream schema upon new fields
     *
     * @param newArbitraryColumns
     * @param username
     * @throws LogsControllerException
     */
    private void updateSchema(Map<String, ColumnDefinition> newArbitraryColumns, String username) throws LogsControllerException {
        AnalyticsDataAPI analyticsDataAPIService = LACoreServiceValueHolder.getInstance().getAnalyticsDataAPI();
        AnalyticsSchema analyticsSchema;
        try {
            analyticsSchema = analyticsDataAPIService.getTableSchema(username, LAConstants.LOG_ANALYZER_STREAM_NAME);
            Map<String, ColumnDefinition> previousColumns = analyticsSchema.getColumns();
            previousColumns.putAll(newArbitraryColumns);

            AnalyticsSchema updatedAnalyticsSchema = new AnalyticsSchema(new ArrayList(previousColumns.values()), null);
            analyticsDataAPIService.setTableSchema(username, LAConstants.LOG_ANALYZER_STREAM_NAME, updatedAnalyticsSchema);
            if (log.isDebugEnabled()) {
                log.debug("Log Analyzer schema updated successfully");
            }
        } catch (AnalyticsException e) {
            throw new LogsControllerException("Error occurred while updating loganalyzer schema " + e.getMessage(), e);
        }
    }

    /**
     * get the new arbitrary fields present in the stream
     *
     * @param rawEvent
     * @param username
     * @return
     * @throws LogsControllerException
     */
    private Map<String, ColumnDefinition> getNewArbitraryFields(Map<String, String> rawEvent, String username) throws LogsControllerException {
        Map<String, ColumnDefinition> newColumns = new HashMap<>();
        AnalyticsDataAPI analyticsDataAPIService = LACoreServiceValueHolder.getInstance().getAnalyticsDataAPI();
        try {
            AnalyticsSchema analyticsSchema = analyticsDataAPIService.getTableSchema(username, LAConstants.LOG_ANALYZER_STREAM_NAME);
            Map<String, ColumnDefinition> columns = analyticsSchema.getColumns();
            for (String arbitraryKey : rawEvent.keySet()) {
                String columnName = LAConstants.ARBITRARY_FIELD_PREFIX + arbitraryKey;
                // _timestam is a reserved field in DAS, we need to omit that colum nname
                if (!columns.containsKey(columnName) && !LAConstants.DAS_TIMESTAMP_FIELD.equalsIgnoreCase(columnName)) {
                    newColumns.put(columnName, new ColumnDefinition(columnName, AnalyticsSchema.ColumnType.STRING, true, false));
                }
            }
        } catch (AnalyticsException e) {
            throw new LogsControllerException("Error occurred while getting new arbitrary fields of the event " + e.getMessage(), e);
        }
        return newColumns;
    }
}
