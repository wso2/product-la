package org.wso2.carbon.la.log.agent.filters;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by malith on 11/23/15.
 */
public class RegExFilter extends AbstractFilter {

    public RegExFilter(String filterType, Map<String,String> matches) {
        super(filterType, matches);
    }

    @Override
    public void process(String logLine, Map<String, String> matchedValues) {
        for (Map.Entry<String, String> match : getMatches().entrySet())
        {
            String value = processRegEx(logLine, match.getValue());
            if(value!=null) {
                matchedValues.put(match.getKey(), value);
            }
        }
    }

    private String processRegEx(String logLine, String regEx){
        String re = ".*?";	// Non-greedy match on filler
        String value = null;
        Pattern p = Pattern.compile(re + regEx,Pattern.CASE_INSENSITIVE | Pattern.DOTALL); //no need to compile always
        Matcher m = p.matcher(logLine);
        if (m.find())
        {
            value=m.group(1).toString();
        }
        return  value;
    }
}
