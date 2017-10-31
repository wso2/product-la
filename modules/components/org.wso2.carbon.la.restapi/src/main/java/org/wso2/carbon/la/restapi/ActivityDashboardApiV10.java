/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.la.restapi;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.wso2.carbon.analytics.activitydashboard.admin.ActivityDashboardAdminService;
import org.wso2.carbon.analytics.activitydashboard.admin.ActivityDashboardException;
import org.wso2.carbon.analytics.activitydashboard.admin.bean.ActivitySearchRequest;
import org.wso2.carbon.analytics.activitydashboard.commons.InvalidExpressionNodeException;
import org.wso2.carbon.analytics.activitydashboard.commons.Query;
import org.wso2.carbon.analytics.activitydashboard.commons.SearchExpressionTree;
import org.wso2.carbon.analytics.datasource.core.util.GenericUtils;

@Path("/activities")
public class ActivityDashboardApiV10 {

    @GET
    @Path("getActivities")
    @Produces("application/json")
    @Consumes("application/json")
    public Response getAllActivities( @QueryParam("fromTime") long fromTime, @QueryParam("toTime") long toTime,
                                      @QueryParam("searchQuery") String searchQuery) {
        String [] activities;
        try {
            ActivityDashboardAdminService service = new ActivityDashboardAdminService();
            ActivitySearchRequest activitySearchRequest = new ActivitySearchRequest();

            //todo change
            String expressionNodeId = "1";
            String tableName = "tableName";

            Query query = new Query(expressionNodeId, tableName, searchQuery);
            SearchExpressionTree searchExpressionTree = new SearchExpressionTree();
            searchExpressionTree.putExpressionNode(query);
            activitySearchRequest.setFromTime(fromTime);
            activitySearchRequest.setToTime(toTime);
            activitySearchRequest.setSearchTreeExpression(GenericUtils.serializeObject(searchExpressionTree));
            activities = service.getActivities(activitySearchRequest);
        } catch (ActivityDashboardException e) {
            return Response.serverError().build();
        } catch (InvalidExpressionNodeException e) {
            return Response.serverError().build();
        }
        return Response.ok(activities).build();
    }

}