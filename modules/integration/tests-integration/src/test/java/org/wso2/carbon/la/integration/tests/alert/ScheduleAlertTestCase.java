package org.wso2.carbon.la.integration.tests.alert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.la.integration.common.utils.LAHttpClient;
import org.wso2.carbon.la.integration.common.utils.LAIntegrationBaseTest;
import org.wso2.carbon.la.integration.common.utils.LAIntegrationTestConstants;

import javax.ws.rs.core.Response;

import static org.testng.AssertJUnit.assertEquals;


/**
 * Created by nalaka on 4/4/16.
 */
public class ScheduleAlertTestCase extends LAIntegrationBaseTest {
    public static final Log log=LogFactory.getLog(ScheduleAlertTestCase.class);
    private LAHttpClient laHttpClient;

    @BeforeClass(alwaysRun = true)
    protected void init()throws Exception{
        super.init();
        laHttpClient=getMLHttpClient();
            uploadSampleData(LAIntegrationTestConstants.LOG_DATASET_SAMPLE);
    }

    @Test(groups = "wso2.la", description = "Test Creating Schedule Alert")
    public void createScheduleAlertTestCase() throws Exception {
        log.info("Executing create Schedule Alert test case...");
        String payload="{\"alertName\":\"AlertTest\",\"description\":\"Test description\",\"query\":\"_level:WARN\",\"timeFrom\":\"0\",\"timeTo\":\"8640000000000000\",\"cronExpression\":\"0 0/1 * * * ?\",\"condition\":\"gt\",\"conditionValue\":\"3\",\"alertActionType\":\"logger\",\"fields\":{\"field0\":\"_timestamp2\",\"field1\":\"_level\"},\"alertActionProperties\":{\"uniqueId\":\"WARN ALert\",\"message\":\"TEST Alert WARN Count {{values}} {{count}}\"}}";

        CloseableHttpResponse response=laHttpClient.doHttpPost("/api/alert/save",payload);
        assertEquals("Unexpected response received", Response.Status.OK.getStatusCode(), response.getStatusLine().getStatusCode());
    }
}
