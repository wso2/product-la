package org.wso2.carbon.la.log.agent;

import org.wso2.carbon.la.log.agent.conf.AgentConfig;
import org.wso2.carbon.la.log.agent.conf.LogGroup;
import org.wso2.carbon.la.log.agent.conf.ServerConfig;
import org.wso2.carbon.la.log.agent.data.LogInput;
import org.wso2.carbon.la.log.agent.data.LogOutput;
import org.wso2.carbon.la.log.agent.filters.AbstractFilter;
import org.wso2.carbon.la.log.agent.filters.RegExFilter;

import java.util.*;
import java.util.regex.*;

public class test {
    public static void main(String[] args)
    {
        String txt="TID: [0] [BAM] [2015-10-22 |14:58:20,964] INFO WARN " +
                "{org.wso2.carbon.core.internal.CarbonCoreActivator} | - \"\\t\" Starting WSO2 Carbon... {org.wso2.carbon.core.internal.CarbonCoreActivator}";

//        String re1=".*?";	// Non-greedy match on filler
//        String re2="(INFO|DEBUG|ERROR|WARN|ALL|TRACE|OFF|FATAL)";
//
//        Pattern p = Pattern.compile(re1+re2,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
//        Matcher m = p.matcher(txt);
//        if (m.find())
//        {
//            String var1=m.group(1);
//            System.out.print("("+var1.toString()+")"+"\n");
//        }


/*
 * This is a java example source code that shows how to use useDelimiter(String pattern)
 * method of Scanner class. We use the string ; as delimiter
 * to use in tokenizing a String input declared in Scanner constructor
 */


                // Initialize Scanner object
                Scanner scan = new Scanner(txt);
                // initialize the string delimiter
                scan.useDelimiter("\\t");
        //scan.useDelimiter(",");
                // Printing the delimiter used
                System.out.println("The delimiter use is " + scan.delimiter());
                // Printing the tokenized Strings
                while(scan.hasNext()){
                    System.out.println(":)" + scan.next().trim() + ":)");
                }
                // closing the scanner stream
                scan.close();

    }

//    /**
//     * Example main. Beware: the watch listens on a whole folder, not on a single
//     * file. Any update on a file within the folder will trigger a read-update.
//     *
//     * @param args
//     * @throws Exception
//     */
//    public static void main(String[] args) throws Exception {
//        ServerConfig serverConfig = new ServerConfig("ssl://10.100.0.88:7711", "tcp://10.100.0.88:7611", "admin", "admin");
//
//        LogInput logInput=new LogInput();
//        logInput.setFilePath("/home/malith/Products/WSO2/BAM/2-5-0/wso2bam-2.5.0/repository/logs/wso2carbon.log");
//
//        LogOutput logOutput=new LogOutput();
//        logOutput.setServerConfig(serverConfig);
//
//        Map<String, String> matches = new HashMap<String, String>();
//
//        matches.put("level", "(INFO|DEBUG|ERROR|WARN|ALL|TRACE|OFF|FATAL)");
//
//        RegExFilter regExFilter=new RegExFilter("RegEx", matches);
//
//        List<AbstractFilter> filters = new ArrayList<AbstractFilter>();
//
//        filters.add(regExFilter);
//
//        LogGroup logGroup = new LogGroup();
//
//        logGroup.setFilters(filters);
//
//        logGroup.setGroupName("org.wso2.sample.logs.group.1");
//
//        logGroup.setVersion("1.0.0");
//
//        logGroup.setLogInput(logInput);
//
//        List<LogGroup> logGroups = new ArrayList<LogGroup>();
//
//        logGroups.add(logGroup);
//
//        AgentConfig agentConfig = new AgentConfig();
//
//
//
//        agentConfig.setLogGroups(logGroups);
//
//        agentConfig.setLogOutput(logOutput);
//
//
//        AgentFactory agentFactory=new AgentFactory();
//
//        agentFactory.init(agentConfig);
//
//        //LogReader t = new LogReader(new File(fn), true, serverConfig);
//
//        //t.start();
//    }
}
