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

package org.wso2.carbon.la.core.utils;

import org.wso2.carbon.analytics.api.AnalyticsDataAPI;
import org.wso2.carbon.event.stream.core.EventStreamService;
import org.wso2.carbon.la.database.DatabaseService;
import org.wso2.carbon.utils.ConfigurationContextService;

public class LACoreServiceValueHolder {

    private static volatile LACoreServiceValueHolder laCoreServiceValueHolder;
    private DatabaseService databaseService;
    private ConfigurationContextService configurationContextService;
    private EventStreamService eventStreamService;
    private AnalyticsDataAPI analyticsDataAPI;

    public static LACoreServiceValueHolder getInstance(){
        if (laCoreServiceValueHolder == null) {
            synchronized (LACoreServiceValueHolder.class) {
                if (laCoreServiceValueHolder == null) {
                    laCoreServiceValueHolder = new LACoreServiceValueHolder();
                }
            }
        }
        return laCoreServiceValueHolder;
    }

    public void registerDatabaseService(DatabaseService databaseService){
        this.databaseService = databaseService;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public ConfigurationContextService getConfigurationContextService() {
        return configurationContextService;
    }

    public void registerConfigurationContextService(ConfigurationContextService configurationContextService) {
        this.configurationContextService = configurationContextService;
    }

    public void setEventStreamService(EventStreamService eventStreamService) {
        this.eventStreamService = eventStreamService;
    }

    public EventStreamService getEventStreamService() {
        return eventStreamService;
    }

    public AnalyticsDataAPI getAnalyticsDataAPI() {
        return analyticsDataAPI;
    }

    public void setAnalyticsDataAPI(AnalyticsDataAPI analyticsDataAPI) {
        this.analyticsDataAPI = analyticsDataAPI;
    }
}

