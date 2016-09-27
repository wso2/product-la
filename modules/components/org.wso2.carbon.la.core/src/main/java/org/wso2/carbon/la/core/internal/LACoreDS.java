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

package org.wso2.carbon.la.core.internal;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.analytics.api.AnalyticsDataAPI;
import org.wso2.carbon.event.stream.core.EventStreamService;
import org.wso2.carbon.la.core.utils.LACoreServiceValueHolder;
import org.wso2.carbon.la.database.DatabaseService;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.ConfigurationContextService;
import org.wso2.carbon.utils.NetworkUtils;

import java.io.File;

/**
 * @scr.component name="la.core" immediate="true"
 * @scr.reference name="databaseService" interface="org.wso2.carbon.la.database.DatabaseService" cardinality="1..1"
 *                policy="dynamic" bind="setDatabaseService" unbind="unsetDatabaseService"
 * @scr.reference name="configurationcontext.service" interface="org.wso2.carbon.utils.ConfigurationContextService"
 *                cardinality="1..1" policy="dynamic" bind="setConfigurationContextService"
 *                unbind="unsetConfigurationContextService"
 * @scr.reference name="org.wso2.carbon.event.stream.core.EventStreamService"
 *                interface="org.wso2.carbon.event.stream.core.EventStreamService"
 *                cardinality="1..1" policy="dynamic" bind="setEventStreamService"
 *                unbind="unsetEventStreamService"
 * @scr.reference name="org.wso2.carbon.analytics.api.AnalyticsDataAPI"
 *                interface="org.wso2.carbon.analytics.api.AnalyticsDataAPI"
 *                cardinality="1..1" policy="dynamic" bind="setAnalyticsDataAPI"
 *                unbind="unsetAnalyticsDataAPI"
 */
public class LACoreDS {

    private static final Log log = LogFactory.getLog(LACoreDS.class);

    protected void activate(ComponentContext context) {
        // Hostname
        String hostName = "localhost";
        String tempFolderLocation = CarbonUtils.getCarbonHome() + File.separator + "repository" + File.separator + "data" + File.separator + "analyzer-logs";
        try {
            hostName = NetworkUtils.getMgtHostName();
            File tempDir = new File(tempFolderLocation);
            if (!tempDir.exists()) {
                FileUtils.forceMkdir(tempDir);
            }
        } catch (Exception ignored) {
        }

        // HTTPS port
        String mgtConsoleTransport = CarbonUtils.getManagementTransport();
        ConfigurationContextService configContextService = LACoreServiceValueHolder.getInstance()
                .getConfigurationContextService();
        int httpsPort = CarbonUtils.getTransportPort(configContextService, mgtConsoleTransport);
        int httpsProxyPort = CarbonUtils.getTransportProxyPort(configContextService.getServerConfigContext(),
                mgtConsoleTransport);
        // set the la.url property which will be used to print in the console by the loganalyzer jaggery app.
        configContextService.getServerConfigContext().setProperty("la.url",
                "https://" + hostName + ":" + (httpsProxyPort != -1 ? httpsProxyPort : httpsPort) + "/loganalyzer");

        if(log.isDebugEnabled()){
            log.debug("log anlyzer Core component activated successfully.");
        }
    }

    protected void deactivate(ComponentContext componentContext){
        if(log.isDebugEnabled()){
            log.debug("log analyzer core component deactivated");
        }
    }

    protected void setConfigurationContextService(ConfigurationContextService configurationContextService) {
        LACoreServiceValueHolder.getInstance().registerConfigurationContextService(configurationContextService);
    }

    protected void unsetConfigurationContextService(ConfigurationContextService configurationContextService) {
        LACoreServiceValueHolder.getInstance().registerConfigurationContextService(null);
    }

    protected void setDatabaseService(DatabaseService databaseService) {
        LACoreServiceValueHolder.getInstance().registerDatabaseService(databaseService);
    }

    protected void unsetDatabaseService(DatabaseService databaseService) {
        LACoreServiceValueHolder.getInstance().registerDatabaseService(databaseService);
    }

    protected void setEventStreamService(EventStreamService eventStreamService) {
        LACoreServiceValueHolder.getInstance().setEventStreamService(eventStreamService);
    }

    protected void unsetEventStreamService(EventStreamService eventStreamService) {
        LACoreServiceValueHolder.getInstance().setEventStreamService(null);
    }

    protected void setAnalyticsDataAPI(AnalyticsDataAPI analyticsDataAPI){
        LACoreServiceValueHolder.getInstance().setAnalyticsDataAPI(analyticsDataAPI);
    }

    protected void unsetAnalyticsDataAPI(AnalyticsDataAPI analyticsDataAPI){
        LACoreServiceValueHolder.getInstance().setAnalyticsDataAPI(null);
    }
}
