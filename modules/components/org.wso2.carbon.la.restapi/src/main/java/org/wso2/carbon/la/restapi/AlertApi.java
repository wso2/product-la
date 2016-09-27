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

package org.wso2.carbon.la.restapi;

import org.wso2.carbon.analytics.datasource.commons.exception.AnalyticsException;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.la.alert.beans.ScheduleAlertBean;
import org.wso2.carbon.la.alert.exception.ScheduleAlertException;
import org.wso2.carbon.la.alert.impl.ScheduleAlertControllerImpl;
import org.wso2.carbon.la.commons.constants.LAConstants;
import org.wso2.carbon.registry.core.exceptions.RegistryException;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

@Path("/alert")
public class AlertApi {
    ScheduleAlertControllerImpl scheduleAlertControllerImpl;

    public AlertApi() {
        scheduleAlertControllerImpl = new ScheduleAlertControllerImpl();
    }

    @POST
    @Path("/save")
    @Consumes("application/json")
    @Produces("application/json")
    public Response saveAlert(ScheduleAlertBean scheduleAlertBean) throws ScheduleAlertException {
        PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        String username = carbonContext.getUsername();
        int tenantId = carbonContext.getTenantId();
        scheduleAlertBean.setTableName(LAConstants.LOG_ANALYZER_STREAM_NAME);
        scheduleAlertBean.setStart(0);
        scheduleAlertBean.setLength(100);
        scheduleAlertControllerImpl.createScheduleAlert(scheduleAlertBean, username, tenantId);
        return Response.ok().build();
    }

    @GET
    @Path("getAllScheduleAlerts")
    @Produces("application/json")
    public Response getAllScheduleAlerts() throws ScheduleAlertException {
        PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        int tenantId = carbonContext.getTenantId();
        List<ScheduleAlertBean> scheduleAlertBeanList = scheduleAlertControllerImpl.getAllAlertConfigurations(tenantId);
        return Response.ok(scheduleAlertBeanList.toArray()).build();
    }

    @GET
    @Path("getColumns")
    @Produces("application/json")
    public Response getAllColumns() throws AnalyticsException {
        PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        int tenantId = carbonContext.getTenantId();
        Set<String> keys = scheduleAlertControllerImpl.getTableColumns(tenantId);
        return Response.ok(keys.toArray()).build();
    }

    @DELETE
    @Path("/delete/{alertName}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response deleteScheduleAlert(@PathParam("alertName") String alertName) throws ScheduleAlertException {
        PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        int tenantId = carbonContext.getTenantId();
        scheduleAlertControllerImpl.deleteScheduledAlert(alertName, tenantId);
        return Response.ok().build();
    }

    @GET
    @Path("getAlertContent/{alertName}")
    @Produces("application/json")
    public ScheduleAlertBean getAlertContent(@PathParam("alertName") String alertName) throws RegistryException, ScheduleAlertException {
        PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        int tenantId = carbonContext.getTenantId();
        return scheduleAlertControllerImpl.getAlertConfiguration(alertName, tenantId);
    }

    @PUT
    @Path("/update")
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateAlertContent(ScheduleAlertBean scheduleAlertBean) throws ScheduleAlertException {
        PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        String username = carbonContext.getUsername();
        int tenantId = carbonContext.getTenantId();
        scheduleAlertBean.setTableName(LAConstants.LOG_ANALYZER_STREAM_NAME);
        scheduleAlertBean.setStart(0);
        scheduleAlertBean.setLength(100);
        scheduleAlertControllerImpl.updateScheduleAlertTask(scheduleAlertBean, username, tenantId);
        return Response.ok().build();
    }
}
