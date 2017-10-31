package org.wso2.carbon.la.integration.common.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.Instance;
import org.wso2.carbon.automation.engine.context.beans.Tenant;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.la.integration.common.utils.exception.LAHttpClientException;

import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by nalaka on 4/4/16.
 */
public class LAIntegrationBaseTest {
    private static final Log log = LogFactory.getLog(LAIntegrationBaseTest.class);
    protected AutomationContext LAServer;
    protected Tenant tenantInfo;
    protected Instance instance;
    protected String backendURL;
    protected String webAppURL;
    protected LoginLogoutClient loginLogoutClient;
    protected User userInfo;
    private LAHttpClient laHttpClient;

    protected void init() throws Exception {
        init(TestUserMode.SUPER_TENANT_ADMIN);
    }

    protected void init(TestUserMode testUserMode) throws Exception {
        LAServer = new AutomationContext("LA", testUserMode);
        loginLogoutClient = new LoginLogoutClient(LAServer);
        backendURL = LAServer.getContextUrls().getBackEndUrl();
        webAppURL = LAServer.getContextUrls().getWebAppURL();
        userInfo = LAServer.getContextTenant().getContextUser();
        instance=LAServer.getInstance();
        laHttpClient = new LAHttpClient(instance,userInfo);
    }
    protected LAHttpClient getMLHttpClient() {
        return this.laHttpClient;
    }

    protected void init(String domainKey, String userKey) throws Exception {
        LAServer = new AutomationContext("LA", "la001", domainKey, userKey);
        loginLogoutClient = new LoginLogoutClient(LAServer);
        backendURL = LAServer.getContextUrls().getBackEndUrl();
        webAppURL = LAServer.getContextUrls().getWebAppURL();
    }

    protected void init(String domainKey, String instance, String userKey) throws Exception {
        LAServer = new AutomationContext("LA", instance, domainKey, userKey);
        loginLogoutClient = new LoginLogoutClient(LAServer);
        backendURL = LAServer.getContextUrls().getBackEndUrl();
        webAppURL = LAServer.getContextUrls().getWebAppURL();
    }

    protected String getSessionCookie() throws Exception {
        return loginLogoutClient.login();
    }

    protected String getResourceContent(Class testClass, String resourcePath) throws Exception {
        String content = "";
        URL url = testClass.getClassLoader().getResource(resourcePath);
        if (url != null) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(
                    new File(url.toURI()).getAbsolutePath()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content += line;
            }
            return content;
        }else {
            throw new Exception("No resource found in the given path : "+ resourcePath);
        }
    }

    protected void uploadSampleData (String path) throws IOException, LAHttpClientException {
        JsonParser jsonParser=new JsonParser();
        JsonArray jsonArray = (JsonArray) jsonParser.parse(new FileReader(laHttpClient.getResourceAbsolutePath(path)));
        CloseableHttpResponse response;
        for (Object rawEvent:jsonArray) {
          response=laHttpClient.uploadSampleLogData(rawEvent);
            assertEquals(Response.Status.OK.getStatusCode(),response.getStatusLine().getStatusCode());
            log.info("Upload Data"+response);
        }
    }
}
