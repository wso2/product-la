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

package org.wso2.carbon.la.alert.domain;

import org.wso2.carbon.registry.core.RegistryConstants;

public class LAAlertConstants {
    public static final String TABLE_NAME = "tableName";
    public static final String QUERY = "query";
    public static final String USER_NAME = "userName";
    public static final String TIME_FROM = "timeFrom";
    public static final String TIME_DIFF = "timeDifference";
    public static final String TIME_TO = "timeTo";
    public static final String START = "start";
    public static final String LENGTH = "length";
    public static final String FIELDS = "fields";
    public static final String ALERT_NAME = "alertName";
    public static final String CONDITION = "condition";
    public static final String CONDITION_VALUE = "conditionValue";
    public static final String MESSAGE="message";
    public static final String ALERT_CONFIGURATION_LOCATION = "repository" + RegistryConstants.PATH_SEPARATOR
            + "components" + RegistryConstants.PATH_SEPARATOR
            + "org.wso2.carbon.la.alert";
    public static final String CONFIGURATION_EXTENSION_SEPARATOR = ".";
    public static final String CONFIGURATION_EXTENSION = "json";
    public static final String CONFIGURATION_MEDIA_TYPE = "application/json";

    public static final String SCHEDULE_ALERT_TASK_TYPE = "LAScheduleAlertTask";
    public static final String LOG_ANALYZER_STREAM_NAME = "loganalyzer";
    public static final String CONDITION_GREATER_THAN = "gt";
    public static final String CONDITION_LESS_THAN = "lt";
    public static final String CONDITION_GREATER_THAN_OR_EQUAL = "gteq";
    public static final String CONDITION_LESS_THAN_OR_EQUAL = "lteq";
    public static final String CONDITION_EQUALS = "eq";
    public static final String CONDITION_NOT_EQUALS = "nteq";
    public static final String ALERT_OUTPUT_STREAM_VERSION = "1.0.0";
    public static final String TRIGGER_DATE_TIME_FORMAT = "dd-MMMMM-yyyy hh:mm aaa";
    public static final String STREAM_VERSION = "1.0.0";
    public static final String PROPERTY_ENABLE= "enable";
    public static final String PROPERTY_DISABLE = "disable";
    public static final String PUBLISHER_MAPPING_TYPE= "text";
    public static final String STREAM_FIELD_ALERT_NAME = "alert_name";
    public static final String STREAM_FIELD_TRIGGER_TIME= "trigger_time";
    public static final String STREAM_FIELD_URL = "url";
    public static final String STREAM_FIELD_VALUES = "values";
    public static final String STREAM_FIELD_COUNT = "count";

}
