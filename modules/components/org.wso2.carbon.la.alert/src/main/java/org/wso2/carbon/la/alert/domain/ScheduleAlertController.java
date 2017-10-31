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

import org.wso2.carbon.la.alert.beans.ScheduleAlertBean;
import org.wso2.carbon.la.alert.exception.ScheduleAlertException;

import java.util.List;

public interface ScheduleAlertController {
    /**
     * Register alert task
     *
     * @param scheduleAlertBean Task info for scheduling
     * @param userName   User Name of this alert task scheduler
     * @param tenantId   user tenantId of this alert scheduler
     */
    void createScheduleAlert(ScheduleAlertBean scheduleAlertBean, String userName, int tenantId) throws ScheduleAlertException;

    /**
     * Update Alert Task
     *
     * @param scheduleAlertBean Task info for set task properties
     * @param userName   User Name of this alert task scheduler
     * @param tenantId   Tenant ID of this alert task scheduler
     */
    void updateScheduleAlertTask(ScheduleAlertBean scheduleAlertBean, String userName, int tenantId) throws ScheduleAlertException;

    /**
     * Delete Alert Task
     *
     * @param alertName Task info for set task properties
     * @param tenantId   Tenant ID of this alert task scheduler
     */
    void deleteScheduledAlert(String alertName, int tenantId) throws ScheduleAlertException;

    /**
     * Get all Alert configuration
     *
     * @param tenantId   Tenant ID of this alert task scheduler
     */

    /**
     * Get all Alert configuration.
     * @param tenantId - Tenant Id of alert configurations
     * @return ScheduleAlertBean List
     * @throws ScheduleAlertException
     */
    List<ScheduleAlertBean> getAllAlertConfigurations(int tenantId) throws ScheduleAlertException;

    /**
     * Get alert configuration.
     * @param alertName -alert name of the configuration
     * @param tenantId - tenant Id of the alert configuration
     * @return ScheduleAlertBean
     * @throws ScheduleAlertException
     */
    ScheduleAlertBean getAlertConfiguration(String alertName, int tenantId) throws ScheduleAlertException;
}
