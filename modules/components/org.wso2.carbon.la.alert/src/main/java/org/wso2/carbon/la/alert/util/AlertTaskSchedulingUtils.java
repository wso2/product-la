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

package org.wso2.carbon.la.alert.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.la.alert.domain.LAAlertConstants;
import org.wso2.carbon.la.alert.beans.ScheduleAlertBean;
import org.wso2.carbon.la.alert.impl.ScheduleAlertTask;
import org.wso2.carbon.ntask.common.TaskException;
import org.wso2.carbon.ntask.core.TaskInfo;
import org.wso2.carbon.ntask.core.TaskManager;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represent utility methods that used by ScheduleAlertControllerImpl for alert task scheduling operations.
 */
public class AlertTaskSchedulingUtils {

    public static final Log log = LogFactory.getLog(AlertTaskSchedulingUtils.class);

    /**
     * This method is to schedule alert task on Task Manager.
     *
     * @param scheduleAlertBean Alert info for schedule task.
     * @param userName          UserName of the task scheduler.
     * @throws TaskException
     */
    public static void scheduleAlertTask(ScheduleAlertBean scheduleAlertBean, String userName) throws TaskException {
        TaskManager taskManager = LAAlertServiceValueHolder.getInstance().getTaskService().getTaskManager
                (LAAlertConstants.SCHEDULE_ALERT_TASK_TYPE);
        TaskInfo scheduleAlertTaskInfo = getScheduleAlertTaskInfo(scheduleAlertBean, userName);
        taskManager.registerTask(scheduleAlertTaskInfo);
        taskManager.rescheduleTask(scheduleAlertTaskInfo.getName());
    }

    /**
     * This method is for delete scheduled task on Task Manager.
     *
     * @param alertName AlertName is task name use to delete.
     * @throws TaskException
     */
    public static void deleteScheduleTask(String alertName) throws TaskException {
        TaskManager taskManager = LAAlertServiceValueHolder.getInstance().getTaskService().getTaskManager
                (LAAlertConstants.SCHEDULE_ALERT_TASK_TYPE);
        taskManager.deleteTask(alertName);
    }

    /**
     * This method create TaskInfo and scheduleAlertTask method use this TaskInfo to schedule task.
     *
     * @param scheduleAlertBean Alert info for scheduling.
     * @param userName          User name of the alert scheduler.
     * @return TaskInfo
     */
    private static TaskInfo getScheduleAlertTaskInfo(ScheduleAlertBean scheduleAlertBean, String userName) {
        String taskName = scheduleAlertBean.getAlertName();
        TaskInfo.TriggerInfo triggerInfo = new TaskInfo.TriggerInfo(scheduleAlertBean.getCronExpression());
        Map<String, String> taskProperties = getTaskProperties(scheduleAlertBean, userName);
        return new TaskInfo(taskName, ScheduleAlertTask.class.getName(), taskProperties, triggerInfo);
    }

    /**
     * This method create task properties for schedule alert task.
     *
     * @param scheduleAlertBean Alert info for scheduling.
     * @param userName          user name of the alert scheduler.
     * @return TaskProperties Map Object
     */
    private static Map<String, String> getTaskProperties(ScheduleAlertBean scheduleAlertBean, String userName) {
        Map<String, String> taskProperties = new HashMap<>();
        long timeDiff = scheduleAlertBean.getTimeTo() - scheduleAlertBean.getTimeFrom();
        taskProperties.put(LAAlertConstants.TABLE_NAME, scheduleAlertBean.getTableName());
        taskProperties.put(LAAlertConstants.QUERY, scheduleAlertBean.getQuery());
        taskProperties.put(LAAlertConstants.USER_NAME, userName);
        taskProperties.put(LAAlertConstants.TIME_DIFF, String.valueOf(timeDiff));
        taskProperties.put(LAAlertConstants.TIME_FROM, String.valueOf(scheduleAlertBean.getTimeFrom()));
        taskProperties.put(LAAlertConstants.TIME_TO, String.valueOf(scheduleAlertBean.getTimeTo()));
        taskProperties.put(LAAlertConstants.START, String.valueOf(scheduleAlertBean.getStart()));
        taskProperties.put(LAAlertConstants.LENGTH, String.valueOf(scheduleAlertBean.getLength()));
        taskProperties.put(LAAlertConstants.ALERT_NAME, scheduleAlertBean.getAlertName());
        taskProperties.put(LAAlertConstants.CONDITION, scheduleAlertBean.getCondition());
        taskProperties.put(LAAlertConstants.CONDITION_VALUE, String.valueOf(scheduleAlertBean.getConditionValue()));
        if (!scheduleAlertBean.getFields().isEmpty()) {
            Map<String, String> fields = scheduleAlertBean.getFields();
            StringBuilder fieldString = new StringBuilder();
            for (String field : fields.values()) {
                fieldString.append(field).append(",");
            }
            fieldString.deleteCharAt(fieldString.length() - 1);
            taskProperties.put(LAAlertConstants.FIELDS, fieldString.toString());
        }
        return taskProperties;
    }
}
