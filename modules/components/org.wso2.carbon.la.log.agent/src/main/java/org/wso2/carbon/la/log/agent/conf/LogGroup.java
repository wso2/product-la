package org.wso2.carbon.la.log.agent.conf;

import org.wso2.carbon.la.log.agent.data.LogInput;
import org.wso2.carbon.la.log.agent.filters.AbstractFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by malith on 11/23/15.
 */
public class LogGroup {

    private String groupName;

    private String version;

    private List<AbstractFilter> filters = new ArrayList<AbstractFilter>();

    private LogInput logInput;

    public List<AbstractFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<AbstractFilter> filters) {
        this.filters = filters;
    }

    public LogInput getLogInput() {
        return logInput;
    }

    public void setLogInput(LogInput logInput) {
        this.logInput = logInput;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
