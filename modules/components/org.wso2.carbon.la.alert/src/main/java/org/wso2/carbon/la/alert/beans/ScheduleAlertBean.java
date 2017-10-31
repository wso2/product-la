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

package org.wso2.carbon.la.alert.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ScheduleAlertBean {
    @XmlElement(name = "alertName")
    private String alertName;

    @XmlElement(name = "description", required = false)
    private String description;

    @XmlElement(name = "tableName", required = false)
    private String tableName;

    @XmlElement(name = "query")
    private String query;

    @XmlElement(name = "start", required = false)
    private int start;

    @XmlElement(name = "timeFrom")
    private long timeFrom;

    @XmlElement(name = "timeTo")
    private long timeTo;

    @XmlElement(name = "length", required = false)
    private int length;

    @XmlElement(name = "cronExpression")
    private String cronExpression;

    @XmlElement(name = "condition")
    private String condition;

    @XmlElement(name = "conditionValue")
    private int conditionValue;

    @XmlElement(name = "alertActionType")
    private String alertActionType;

    @XmlElement(name = "fields", required = false)
    private Map<String, String> fields;

    @XmlElement(name = "alertActionProperties")
    private Map<String, String> alertActionProperties;

    public ScheduleAlertBean() {
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {

        this.tableName = tableName;
    }
    public String getAlertName() {
        return alertName;
    }

    public void setAlertName(String alertName) {
        this.alertName = alertName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public long getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(long timeFrom) {
        this.timeFrom = timeFrom;
    }

    public long getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(long timeTo) {
        this.timeTo = timeTo;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getConditionValue() {
        return conditionValue;
    }

    public void setConditionValue(int conditionValue) {
        this.conditionValue = conditionValue;
    }

    public String getAlertActionType() {

        return alertActionType;
    }

    public void setAlertActionType(String alertActionType) {
        this.alertActionType = alertActionType;
    }

    public Map<String, String> getAlertActionProperties() {

        return alertActionProperties;
    }

    public void setAlertActionProperties(Map<String, String> alertActionProperties) {
        this.alertActionProperties = alertActionProperties;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public Map<String, String> getFields() {
        return fields;
    }

}
