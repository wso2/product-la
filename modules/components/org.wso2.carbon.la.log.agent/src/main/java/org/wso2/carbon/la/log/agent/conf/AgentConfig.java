package org.wso2.carbon.la.log.agent.conf;

import org.wso2.carbon.la.log.agent.data.LogOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by malith on 11/23/15.
 */
public class AgentConfig {

    private String agentId;

    private List<LogGroup> logGroups = new ArrayList<LogGroup>();

    private LogOutput logOutput;

    public List<LogGroup> getLogGroups() {
        return logGroups;
    }

    public void setLogGroups(List<LogGroup> logGroups) {
        this.logGroups = logGroups;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public LogOutput getLogOutput() {
        return logOutput;
    }

    public void setLogOutput(LogOutput logOutput) {
        this.logOutput = logOutput;
    }

}

