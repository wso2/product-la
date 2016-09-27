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
package org.wso2.carbon.la.alert.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.analytics.api.AnalyticsDataAPI;
import org.wso2.carbon.event.publisher.core.EventPublisherService;
import org.wso2.carbon.event.stream.core.EventStreamService;
import org.wso2.carbon.la.alert.domain.LAAlertConstants;
import org.wso2.carbon.la.alert.domain.ScheduleAlertController;
import org.wso2.carbon.la.alert.impl.ScheduleAlertControllerImpl;
import org.wso2.carbon.la.alert.util.LAAlertServiceValueHolder;
import org.wso2.carbon.ntask.core.service.TaskService;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.registry.core.service.TenantRegistryLoader;

/**
 * @scr.component name="la.alert" immediate="true"
 * @scr.reference name="ntask.TaskService"
 * interface="org.wso2.carbon.ntask.core.service.TaskService"
 * cardinality="1..1" policy="dynamic" bind="setTaskService"
 * unbind="unsetTaskService"
 * @scr.reference name="publisher.EventPublisherService"
 * interface="org.wso2.carbon.event.publisher.core.EventPublisherService"
 * cardinality="1..1" policy="dynamic" bind="setEventPublisherService"
 * unbind="unsetEventPublisherService"
 * @scr.reference name="stream.EventStreamService"
 * interface="org.wso2.carbon.event.stream.core.EventStreamService"
 * cardinality="1..1" policy="dynamic" bind="setEventStreamService"
 * unbind="unsetEventStreamService"
 * @scr.reference name="registry.service"
 * interface="org.wso2.carbon.registry.core.service.RegistryService"
 * cardinality="1..1" policy="dynamic" bind="setRegistryService"
 * unbind="unsetRegistryService"
 * @scr.reference name="tenant.registryloader"
 * interface="org.wso2.carbon.registry.core.service.TenantRegistryLoader"
 * cardinality="1..1" policy="dynamic" bind="setTenantRegistryLoader"
 * unbind="unsetTenantRegistryLoader"
 * * @scr.reference name="org.wso2.carbon.analytics.api.AnalyticsDataAPI"
 * interface="org.wso2.carbon.analytics.api.AnalyticsDataAPI"
 * cardinality="1..1" policy="dynamic" bind="setAnalyticsDataAPI"
 * unbind="unsetAnalyticsDataAPI"
 */
public class LAAlertComponent {

    private static final Log log = LogFactory.getLog(LAAlertComponent.class);

    protected void activate(ComponentContext componentContext) {
        BundleContext bundleContext = componentContext.getBundleContext();
        try {
            ScheduleAlertControllerImpl scheduleAlertControllerImpl = new ScheduleAlertControllerImpl();
            bundleContext.registerService(ScheduleAlertController.class.getName(), scheduleAlertControllerImpl, null);
            LAAlertServiceValueHolder.getInstance().getTaskService().registerTaskType(LAAlertConstants.SCHEDULE_ALERT_TASK_TYPE);
            if (log.isDebugEnabled()) {
                log.debug("Data Services task bundle is activated ");
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    protected void deactivate(ComponentContext componentContext) {
        if (log.isDebugEnabled()) {
            log.debug("log analyzer core component deactivated");
        }
    }

    protected void setTaskService(TaskService taskService) {
        LAAlertServiceValueHolder.getInstance().setTaskService(taskService);
    }

    protected void unsetTaskService(TaskService taskService) {
        LAAlertServiceValueHolder.getInstance().setTaskService(null);
    }

    protected void setEventPublisherService(EventPublisherService eventPublisherService) {
        LAAlertServiceValueHolder.getInstance().setEventPublisherService(eventPublisherService);
    }

    protected void unsetEventPublisherService(EventPublisherService eventPublisherService) {
        LAAlertServiceValueHolder.getInstance().setEventPublisherService(null);
    }

    protected void setEventStreamService(EventStreamService eventStreamService) {
        LAAlertServiceValueHolder.getInstance().setEventStreamService(eventStreamService);
    }

    protected void unsetEventStreamService(EventStreamService eventStreamService) {
        LAAlertServiceValueHolder.getInstance().setEventStreamService(null);

    }

    protected void setRegistryService(RegistryService registryService) {
        LAAlertServiceValueHolder.getInstance().setRegistryService(registryService);
    }

    protected void unsetRegistryService(RegistryService registryService) {
        LAAlertServiceValueHolder.getInstance().setRegistryService(null);
    }

    protected void setTenantRegistryLoader(TenantRegistryLoader tenantRegistryLoader) {
        LAAlertServiceValueHolder.getInstance().setTenantRegistryLoader(tenantRegistryLoader);
    }

    protected void unsetTenantRegistryLoader(TenantRegistryLoader tenantRegistryLoader) {
        LAAlertServiceValueHolder.getInstance().setTenantRegistryLoader(null);
    }

    protected void setAnalyticsDataAPI(AnalyticsDataAPI analyticsDataAPI) {
        LAAlertServiceValueHolder.getInstance().setAnalyticsDataAPI(analyticsDataAPI);
    }

    protected void unsetAnalyticsDataAPI(AnalyticsDataAPI analyticsDataAPI) {
        LAAlertServiceValueHolder.getInstance().setAnalyticsDataAPI(null);
    }
}
