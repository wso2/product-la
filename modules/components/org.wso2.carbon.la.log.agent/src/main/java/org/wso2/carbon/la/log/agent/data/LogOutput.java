package org.wso2.carbon.la.log.agent.data;

import org.wso2.carbon.la.log.agent.conf.ServerConfig;

/**
 * Created by malith on 11/24/15.
 */
public class LogOutput {

    private String logOutputType;

    private ServerConfig serverConfig;

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public String getLogOutputType() {
        return logOutputType;
    }

    public void setLogOutputType(String logOutputType) {
        this.logOutputType = logOutputType;
    }

}
