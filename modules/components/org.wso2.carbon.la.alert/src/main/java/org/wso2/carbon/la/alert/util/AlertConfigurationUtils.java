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

import com.google.gson.Gson;
import org.wso2.carbon.la.alert.domain.LAAlertConstants;
import org.wso2.carbon.la.alert.beans.ScheduleAlertBean;
import org.wso2.carbon.la.alert.exception.AlertConfigurationException;
import org.wso2.carbon.registry.core.Collection;
import org.wso2.carbon.registry.core.RegistryConstants;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.session.UserRegistry;

/**
 * This class represent utility methods that use by ScheduleAlertControllerImpl for registry operations.
 */
public class AlertConfigurationUtils {

    /**
     * This method save the configurations of Alert at the config Registry.
     *
     * @param scheduleAlertBean Alert info for save on Registry.
     * @param tenantId          user tenantId for get Tenant Registry.
     * @throws RegistryException
     */
    public static void saveAlertConfiguration(ScheduleAlertBean scheduleAlertBean, int tenantId) throws RegistryException, AlertConfigurationException {
        UserRegistry userRegistry = LAAlertServiceValueHolder.getInstance().getTenantConfigRegistry(tenantId);
        createConfigurationCollection(userRegistry);
        String configurationLocation = getConfigurationLocation(scheduleAlertBean.getAlertName());
        if (!userRegistry.resourceExists(configurationLocation)) {
            Resource resource = createAlertResource(userRegistry, scheduleAlertBean);
            if (resource != null) {
                userRegistry.put(configurationLocation, resource);
            } else {
                throw new AlertConfigurationException("Unable to save alert configuration, Alert resource is empty");
            }
        } else {
            throw new AlertConfigurationException("Alert configuration file is already exist in registry.");
        }
    }

    /**
     * This method update the configuration of an alert at config registry.
     *
     * @param scheduleAlertBean Alert info for update on Registry.
     * @param tenantId          User tenantId for get Tenant Registry.
     * @throws RegistryException
     */
    public static void updateAlertConfiguration(ScheduleAlertBean scheduleAlertBean, int tenantId) throws RegistryException, AlertConfigurationException {
        UserRegistry userRegistry = LAAlertServiceValueHolder.getInstance().getTenantConfigRegistry(tenantId);
        String fileLocation = getConfigurationLocation(scheduleAlertBean.getAlertName());
        if (userRegistry.resourceExists(fileLocation)) {
            Resource resource = createAlertResource(userRegistry, scheduleAlertBean);
            if (resource != null) {
                userRegistry.put(fileLocation, resource);
            } else {
                throw new AlertConfigurationException("Unable to save alert configuration, Alert resource is empty.");
            }
        } else {
            throw new AlertConfigurationException("Alert configuration file is not exist in registry.");
        }
    }

    /**
     * This method is to delete alert configuration file from registry.
     *
     * @param alertName Alert name for identify the configuration file.
     * @param tenantId  TenantId to get  tenant Config Registry.
     * @throws RegistryException
     */
    public static void deleteAlertConfiguration(String alertName, int tenantId) throws RegistryException, AlertConfigurationException {
        UserRegistry userRegistry = LAAlertServiceValueHolder.getInstance().getTenantConfigRegistry(tenantId);
        String fileLocation = getConfigurationLocation(alertName);
        if (userRegistry.resourceExists(fileLocation)) {
            userRegistry.delete(fileLocation);
        } else {
            throw new AlertConfigurationException("Unable to delete alert configuration, Alert configuration file is not exist.");
        }
    }

    /**
     * This method create collection and put it to the registry location. saveConfiguration method use this for save
     * alert configuration.
     *
     * @param userRegistry -Tenant Config Registry
     */
    public static void createConfigurationCollection(UserRegistry userRegistry) throws RegistryException {
        if (!userRegistry.resourceExists(LAAlertConstants.ALERT_CONFIGURATION_LOCATION)) {
            Collection collection = userRegistry.newCollection();
            userRegistry.put(LAAlertConstants.ALERT_CONFIGURATION_LOCATION, collection);
        }
    }

    /**
     * This method returns the created alert specific path.
     *
     * @param alertName -alert Name to create alert file path
     * @return String alert file path
     */
    public static String getConfigurationLocation(String alertName) {
        return LAAlertConstants.ALERT_CONFIGURATION_LOCATION + RegistryConstants.PATH_SEPARATOR + alertName +
                LAAlertConstants.CONFIGURATION_EXTENSION_SEPARATOR + LAAlertConstants.CONFIGURATION_EXTENSION;
    }

    /**
     * This method create new resource and put alert configuration json in to that resource. This resource will use
     * by saveAlertConfiguration and updateAlertConfiguration methods.
     *
     * @param userRegistry      User registry object for create resource.
     * @param scheduleAlertBean Alert info for create json file.
     * @return Resource which contains alert info JSON.
     * @throws RegistryException
     */
    private static Resource createAlertResource(UserRegistry userRegistry, ScheduleAlertBean scheduleAlertBean) throws RegistryException {
        Resource resource = userRegistry.newResource();
        Gson gson = new Gson();
        String json = gson.toJson(scheduleAlertBean);
        resource.setContent(json);
        resource.setMediaType(LAAlertConstants.CONFIGURATION_MEDIA_TYPE);
        return resource;
    }
}
