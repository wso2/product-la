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

package org.wso2.carbon.la.alert.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.analytics.datasource.commons.exception.AnalyticsException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.utils.DataBridgeCommonsUtils;
import org.wso2.carbon.la.alert.domain.LAAlertConstants;
import org.wso2.carbon.la.alert.util.LAAlertServiceValueHolder;
import org.wso2.carbon.la.commons.domain.QueryBean;
import org.wso2.carbon.la.commons.domain.RecordBean;
import org.wso2.carbon.la.core.impl.SearchController;
import org.wso2.carbon.ntask.core.AbstractTask;

import javax.xml.bind.DatatypeConverter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This class contains execution flow of the schedule alert task.
 */
public class ScheduleAlertTask extends AbstractTask {
    private static final Log log = LogFactory.getLog(ScheduleAlertTask.class);
    Map<String, String> taskProperties;

    @Override
    public void execute() {
        taskProperties = this.getProperties();
        String alertName = taskProperties.get(LAAlertConstants.ALERT_NAME);
        String version = LAAlertConstants.ALERT_OUTPUT_STREAM_VERSION;
        String condition = taskProperties.get(LAAlertConstants.CONDITION);
        int conditionValue = Integer.valueOf(taskProperties.get(LAAlertConstants.CONDITION_VALUE));
        long timeStamp = System.currentTimeMillis();
        try {
            List<RecordBean> recordBeans = getSearchRecords(timeStamp);
            int recodeListSize = recordBeans.size();

            Object[] payload = getPayload(recordBeans, timeStamp);
            switch (condition) {
                case LAAlertConstants.CONDITION_GREATER_THAN:
                    if (recodeListSize > conditionValue) {
                        createEvent(alertName, version, timeStamp, payload);
                    }
                    break;
                case LAAlertConstants.CONDITION_LESS_THAN:
                    if (recodeListSize < conditionValue) {
                        createEvent(alertName, version, timeStamp, payload);
                    }
                    break;
                case LAAlertConstants.CONDITION_EQUALS:
                    if (recodeListSize == conditionValue) {
                        createEvent(alertName, version, timeStamp, payload);
                    }
                    break;
                case LAAlertConstants.CONDITION_GREATER_THAN_OR_EQUAL:
                    if (recodeListSize >= conditionValue) {
                        createEvent(alertName, version, timeStamp, payload);
                    }
                    break;
                case LAAlertConstants.CONDITION_LESS_THAN_OR_EQUAL:
                    if (recodeListSize <= conditionValue) {
                        createEvent(alertName, version, timeStamp, payload);
                    }
                    break;
                case LAAlertConstants.CONDITION_NOT_EQUALS:
                    if (recodeListSize != conditionValue) {
                        createEvent(alertName, version, timeStamp, payload);
                    }
                    break;
            }
        } catch (AnalyticsException e) {
            log.error("Unable to perform scheduled" + alertName + " task: " + e.getMessage(), e);
        }
    }

    /**
     * This method search scheduled query and return the results.
     *
     * @param currentTimeStamp Execution start time.
     * @return RecordBean List.
     * @throws AnalyticsException
     */
    private List<RecordBean> getSearchRecords(long currentTimeStamp) throws AnalyticsException {
        SearchController searchController = new SearchController();
        String username = taskProperties.get(LAAlertConstants.USER_NAME);
        long timeFrom = Long.valueOf(taskProperties.get(LAAlertConstants.TIME_FROM));
        long timeTo = Long.valueOf(taskProperties.get(LAAlertConstants.TIME_TO));
        if (Long.valueOf(taskProperties.get(LAAlertConstants.TIME_FROM)) != 0) {
            long timeDifference = Long.valueOf(taskProperties.get(LAAlertConstants.TIME_DIFF));
            timeFrom = currentTimeStamp - timeDifference;
            timeTo = currentTimeStamp;
        }
        taskProperties.put(LAAlertConstants.TIME_FROM, String.valueOf(timeFrom));
        taskProperties.put(LAAlertConstants.TIME_TO, String.valueOf(timeTo));
        QueryBean queryBean = getQueryBean();
        return searchController.search(queryBean, username);
    }

    /**
     * This method build Query Bean object which used to search by getSearchRecord method.
     *
     * @return QueryBean
     */
    private QueryBean getQueryBean() {
        QueryBean queryBean = new QueryBean();
        queryBean.setTableName(taskProperties.get(LAAlertConstants.TABLE_NAME));
        queryBean.setQuery(taskProperties.get(LAAlertConstants.QUERY));
        queryBean.setTimeFrom(Long.valueOf(taskProperties.get(LAAlertConstants.TIME_FROM)));
        queryBean.setTimeTo(Long.valueOf(taskProperties.get(LAAlertConstants.TIME_TO)));
        queryBean.setStart(Integer.valueOf(taskProperties.get(LAAlertConstants.START)));
        queryBean.setLength(Integer.valueOf(taskProperties.get(LAAlertConstants.LENGTH)));
        return queryBean;
    }

    /**
     * This method build the payload for put in to the LA output stream.
     *
     * @param recordBeans Results of the search.
     * @param timeStamp   Execution start time.
     * @return Object[] payload
     */
    private Object[] getPayload(List<RecordBean> recordBeans, long timeStamp) {
        StringBuilder resultBuilder = new StringBuilder();
        String[] fields = null;
        if (taskProperties.containsKey(LAAlertConstants.FIELDS)) {
            fields = taskProperties.get(LAAlertConstants.FIELDS).split(",");
        }
        if (taskProperties.containsKey(LAAlertConstants.FIELDS)) {
            for (RecordBean record : recordBeans) {
                Map<String, Object> recordValues = record.getValues();
                String dataOb = "<div>";
                if (fields != null) {
                    for (String field : fields) {
                        dataOb += field + ":" + recordValues.get(field) + ",";
                    }
                    dataOb += "</div>";
                    resultBuilder.append(dataOb);
                }
            }
        }
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(LAAlertConstants.TRIGGER_DATE_TIME_FORMAT);
        Date date = new Date(timeStamp);
        String dateTime = dateTimeFormat.format(date);
        int resultCount = recordBeans.size();
        String url = getBuildURL();
        return new Object[]{taskProperties.get(LAAlertConstants.ALERT_NAME), dateTime, url, resultBuilder, (long) resultCount};
    }

    /**
     * This method create an event and put in to the LA output stream.
     *
     * @param alertName Name of the alert (output stream name).
     * @param version   Output stream version.
     * @param timeStamp Current timestamp.
     * @param payload   Payload of the event.
     */
    private void createEvent(String alertName, String version, long timeStamp, Object[] payload) {
        Event event = new Event(DataBridgeCommonsUtils.generateStreamId(alertName, version), timeStamp, null, null, payload);
        LAAlertServiceValueHolder.getInstance().getEventStreamService().publish(event);
    }

    /**
     * This method build a URL string according to the current search.
     *
     * @return String URL
     */
    private String getBuildURL() {
        String base64EncodeQuery = DatatypeConverter.printBase64Binary(taskProperties.get(LAAlertConstants.QUERY).getBytes());
        String base64EncodeTimeFrom = DatatypeConverter.printBase64Binary(taskProperties.get(LAAlertConstants.TIME_FROM).getBytes());
        String base64EncodeTimeTo = DatatypeConverter.printBase64Binary(taskProperties.get(LAAlertConstants.TIME_TO).getBytes());
        return "https://10.100.4.179:9443/loganalyzer/site/search/search.jag?" + "query=" + base64EncodeQuery +
                "&" + "timeFrom=" + base64EncodeTimeFrom + "&" + "timeTo=" + base64EncodeTimeTo;
    }
}


