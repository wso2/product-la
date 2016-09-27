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
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.la.integration.common.utils;

public class LAIntegrationTestConstants {

    public static final String DATASETS_PATH = "alert/schedule/";
    public static final String LOG_DATASET_SAMPLE = DATASETS_PATH+"sampleLogData.json";

    // Constants related to REST calls
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BASIC = "Basic ";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    public static final String ORIGIN_HEADER = "Origin";
    public static final String ORIGIN_HEADER_VALUE = "http://example.com";


    public static final String ANALYTICS_ENDPOINT_URL = "https://localhost:10143/analytics/";
    public static final String ANALYTICS_TABLES_ENDPOINT_URL = "https://localhost:10143/analytics/tables/";
    public static final String ANALYTICS_SEARCH_ENDPOINT_URL = "https://localhost:10143/analytics/search";
    public static final String ANALYTICS_SEARCH_COUNT_ENDPOINT_URL = "https://localhost:10143/analytics/search_count";
    public static final String ANALYTICS_RECORDS_ENDPOINT_URL = "https://localhost:10143/analytics/records/";
    public static final String ANALYTICS_DRILLDOWN_ENDPOINT_URL = "https://localhost:10143/analytics/drilldown";
    public static final String ANALYTICS_DRILLDOWNCOUNT_ENDPOINT_URL = "https://localhost:10143/analytics/drillDownScoreCount";
    public static final String ANALYTICS_DRILLDOWNCATEGORIES_ENDPOINT_URL = "https://localhost:10143/analytics/facets";
    public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String TABLE_EXISTS = "table_exists?table=";
	public static final String BASE64_ADMIN_ADMIN = "Basic YWRtaW46YWRtaW4=";
    public static final String BASE64_TENANT_ADMIN_ADMIN = "Basic YWRtaW5Ad3NvMi5jb206YWRtaW4=";
	public static final String ANALYTICS_WAITFOR_INDEXING_ENDPOINT_URL = "https://localhost:10143/analytics/indexing_done";
	public static final String ANALYTICS_REINDEX_ENDPOINT_URL = "https://localhost:10143/analytics/tables/";
    public static final String SCHEMA = "/schema";
    public static final String ANALYTICS_JS_ENDPOINT = "https://localhost:10143/portal/apis/analytics";
}
