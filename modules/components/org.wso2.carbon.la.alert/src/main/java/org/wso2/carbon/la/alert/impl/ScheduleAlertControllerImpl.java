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

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.analytics.api.AnalyticsDataAPI;
import org.wso2.carbon.analytics.datasource.commons.AnalyticsSchema;
import org.wso2.carbon.analytics.datasource.commons.ColumnDefinition;
import org.wso2.carbon.analytics.datasource.commons.exception.AnalyticsException;
import org.wso2.carbon.analytics.datasource.core.util.GenericUtils;
import org.wso2.carbon.la.alert.domain.LAAlertConstants;
import org.wso2.carbon.la.alert.beans.ScheduleAlertBean;
import org.wso2.carbon.la.alert.domain.ScheduleAlertController;
import org.wso2.carbon.la.alert.exception.AlertConfigurationException;
import org.wso2.carbon.la.alert.exception.AlertPublisherException;
import org.wso2.carbon.la.alert.exception.ScheduleAlertException;
import org.wso2.carbon.la.alert.util.AlertConfigurationUtils;
import org.wso2.carbon.la.alert.util.AlertPublisherUtils;
import org.wso2.carbon.la.alert.util.AlertTaskSchedulingUtils;
import org.wso2.carbon.la.alert.util.LAAlertServiceValueHolder;
import org.wso2.carbon.ntask.common.TaskException;
import org.wso2.carbon.registry.core.Collection;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.registry.core.utils.RegistryUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is controller class of schedule alerting of Log analyzer.
 */
public class ScheduleAlertControllerImpl implements ScheduleAlertController {

    private static final Log log = LogFactory.getLog(ScheduleAlertControllerImpl.class);

    /**
     * This is the controller method of creating schedule alert.
     *
     * @param scheduleAlertBean Alert info for scheduling.
     * @param userName          User Name of this alert scheduler.
     * @param tenantId          User tenantId of this alert scheduler.
     * @throws ScheduleAlertException
     */
    public void createScheduleAlert(ScheduleAlertBean scheduleAlertBean, String userName, int tenantId)
            throws ScheduleAlertException {
        try {
            if (!isScheduleAlertAlreadyExist(tenantId, scheduleAlertBean.getAlertName())) {
                AlertConfigurationUtils.saveAlertConfiguration(scheduleAlertBean, tenantId);
                AlertPublisherUtils.createAlertPublisher(scheduleAlertBean.getAlertName(),
                        scheduleAlertBean.getAlertActionType(),
                        scheduleAlertBean.getAlertActionProperties());
                AlertTaskSchedulingUtils.scheduleAlertTask(scheduleAlertBean, userName);
            } else {
                log.error("Unable to create alert: " + scheduleAlertBean.getAlertName() + " is already exist.");
                throw new ScheduleAlertException("Unable to create alert: " + scheduleAlertBean.getAlertName()
                        + " is already exist.");
            }
        } catch (AlertPublisherException | TaskException | RegistryException | AlertConfigurationException e) {
            log.error("Unable to create alert: " + e.getMessage(), e);
            throw new ScheduleAlertException("Unable to create alert: " + scheduleAlertBean.getAlertName()
                    + " ERROR: " + e.getMessage());
        }
    }

    /**
     * This is the controller method of updating the schedule alert.
     *
     * @param scheduleAlertBean Task info for set task properties.
     * @param userName          User Name of this alert task scheduler.
     * @param tenantId          Tenant ID of this alert task scheduler.
     * @throws ScheduleAlertException
     */
    public void updateScheduleAlertTask(ScheduleAlertBean scheduleAlertBean, String userName, int tenantId)
            throws ScheduleAlertException {
        try {
            if (isScheduleAlertAlreadyExist(tenantId, scheduleAlertBean.getAlertName())) {
                AlertConfigurationUtils.updateAlertConfiguration(scheduleAlertBean, tenantId);
                AlertPublisherUtils.updateAlertPublisher(scheduleAlertBean.getAlertName(),
                        scheduleAlertBean.getAlertActionType(), scheduleAlertBean.getAlertActionProperties());
                AlertTaskSchedulingUtils.deleteScheduleTask(scheduleAlertBean.getAlertName());
                AlertTaskSchedulingUtils.scheduleAlertTask(scheduleAlertBean, userName);
            } else {
                log.error("Unable to update alert: " + scheduleAlertBean.getAlertName() + " does not exist.");
                throw new ScheduleAlertException("Unable to create alert: " + scheduleAlertBean.getAlertName()
                        + " does not exist.");
            }
        } catch (RegistryException | TaskException | AlertPublisherException | AlertConfigurationException e) {
            log.error("Unable to update Alert: " + e.getMessage(), e);
            throw new ScheduleAlertException("Unable to update Alert:" + scheduleAlertBean.getAlertName()
                    + "ERROR: " + e.getMessage());
        }
    }

    /**
     * This method use to delete scheduled alert by given alertName.
     *
     * @param alertName AlertName for delete scheduled alert.
     * @param tenantId  Tenant ID for get alert configuration location.
     * @throws ScheduleAlertException
     */
    public void deleteScheduledAlert(String alertName, int tenantId) throws ScheduleAlertException {
        try {
            if (isScheduleAlertAlreadyExist(tenantId, alertName)) {
                AlertPublisherUtils.deleteAlertPublisher(alertName);
                AlertConfigurationUtils.deleteAlertConfiguration(alertName, tenantId);
                AlertTaskSchedulingUtils.deleteScheduleTask(alertName);
            } else {
                log.warn("Unable to delete alert: " + alertName + " does not exist.");
                throw new ScheduleAlertException("Unable to delete alert: " + alertName + " does not exist.");
            }
        } catch (RegistryException | AlertPublisherException | TaskException | AlertConfigurationException e) {
            log.error("Unable to delete alert: " + e.getMessage(), e);
            throw new ScheduleAlertException("Unable to create alert: " + alertName
                    + "ERROR: " + e.getMessage());
        }
    }

    /**
     * This method return all alert configurations.
     *
     * @param tenantId Tenant ID of this alert task scheduler.
     * @return List
     * @throws ScheduleAlertException
     */
    public List<ScheduleAlertBean> getAllAlertConfigurations(int tenantId) throws ScheduleAlertException {
        try {
            UserRegistry userRegistry = LAAlertServiceValueHolder.getInstance().getTenantConfigRegistry(tenantId);
            if (!userRegistry.resourceExists(LAAlertConstants.ALERT_CONFIGURATION_LOCATION)) {
                AlertConfigurationUtils.createConfigurationCollection(userRegistry);
            }
            Collection configurationCollection = (Collection) userRegistry.get(LAAlertConstants.ALERT_CONFIGURATION_LOCATION);
            String[] configs = configurationCollection.getChildren();
            List<ScheduleAlertBean> configurations = new ArrayList<>();
            if (configs != null) {
                for (String conf : configs) {
                    String content = RegistryUtils.decodeBytes((byte[]) userRegistry.get(conf).getContent());
                    configurations.add(getConfigurationContent(content));
                }
            }
            return configurations;
        } catch (RegistryException e) {
            log.error("Unable to get alert configuration from registry: " + e.getMessage(), e);
            throw new ScheduleAlertException("Unable to complete request: " + e.getMessage());
        }
    }

    /**
     * This method returns ScheduleAlertBean object which request by alert name.
     *
     * @param alertName Alert name for get the alert configuration file.
     * @param tenantId  TenantId of alert scheduler.
     * @return ScheduleAlertBean
     * @throws ScheduleAlertException
     */
    public ScheduleAlertBean getAlertConfiguration(String alertName, int tenantId) throws ScheduleAlertException {
        ScheduleAlertBean scheduleAlertBean;
        try {
            UserRegistry userRegistry = LAAlertServiceValueHolder.getInstance().getTenantConfigRegistry(tenantId);
            String fileLocation = AlertConfigurationUtils.getConfigurationLocation(alertName);
            if (userRegistry.resourceExists(fileLocation)) {
                scheduleAlertBean = getConfigurationContent(RegistryUtils.decodeBytes((byte[])
                        userRegistry.get(fileLocation).getContent()));
            } else {
                log.warn(alertName + " resource does not exist for tenantId: " + tenantId);
                throw new ScheduleAlertException(alertName + " does not exist.");
            }
        } catch (RegistryException e) {
            log.error("Unable to complete request: " + e.getMessage(), e);
            throw new ScheduleAlertException("Unable to complete request: " + e.getMessage(), e);
        }
        return scheduleAlertBean;
    }

    /**
     * Create ScheduleAlertBean by using json alertContent.
     *
     * @param alertContent JSON string of alert configuration.
     * @return ScheduleAlertBean
     */
    private ScheduleAlertBean getConfigurationContent(String alertContent) {
        Gson gson = new Gson();
        return gson.fromJson(alertContent, ScheduleAlertBean.class);
    }

    /**
     * Get all column names from LOGANALYZER table schema.
     *
     * @param tenantId TenantId of column requester.
     * @return Set of column names.
     * @throws AnalyticsException
     */
    public Set<String> getTableColumns(int tenantId) throws AnalyticsException {
        AnalyticsDataAPI analyticsDataAPI = LAAlertServiceValueHolder.getInstance().getAnalyticsDataAPI();
        AnalyticsSchema analyticsSchema = analyticsDataAPI.getTableSchema(tenantId,
                GenericUtils.streamToTableName(LAAlertConstants.LOG_ANALYZER_STREAM_NAME));
        Map<String, ColumnDefinition> columns = analyticsSchema.getColumns();
        return columns.keySet();
    }

    /**
     * Validate new alert Name is already exist or not.
     *
     * @param tenantId     TenantId of alert Creator.
     * @param newAlertName New alertName for check either exist or not.
     * @return boolean
     */
    public boolean isScheduleAlertAlreadyExist(int tenantId, String newAlertName) throws RegistryException {
        UserRegistry userRegistry = LAAlertServiceValueHolder.getInstance().getTenantConfigRegistry(tenantId);
        String fileLocation = AlertConfigurationUtils.getConfigurationLocation(newAlertName);
        if (userRegistry.resourceExists(fileLocation)) {
            return true;
        }
        return false;
    }
}
