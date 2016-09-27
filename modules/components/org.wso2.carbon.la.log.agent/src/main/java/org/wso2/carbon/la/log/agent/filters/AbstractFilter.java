package org.wso2.carbon.la.log.agent.filters;

import javax.xml.soap.SAAJResult;
import java.util.Map;

/**
 * Created by malith on 11/23/15.
 */
public abstract class AbstractFilter {
    protected String filterType;
    protected Map<String,String> matches;


    public AbstractFilter(String filterType, Map<String,String> matches) {
        this.filterType = filterType;
        this.matches = matches;
    }

    public Map<String, String> getMatches() {
        return matches;
    }

    public String getFilterType() {
        return filterType;
    }

    public abstract void process(String logLine, Map<String, String> matchedValues);
}