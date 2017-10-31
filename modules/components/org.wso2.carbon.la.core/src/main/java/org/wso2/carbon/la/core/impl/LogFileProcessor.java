package org.wso2.carbon.la.core.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.la.commons.domain.config.LogFileConf;
import org.wso2.carbon.la.core.utils.LogPatternExtractor;
import org.wso2.carbon.utils.CarbonUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

public class LogFileProcessor {
    private static final Log log = LogFactory.getLog(LogFileProcessor.class);
    static final String tempFolderLocation = CarbonUtils.getCarbonHome() + File.separator + "repository" + File.separator +
            "data" + File.separator + "analyzer-logs";
    private LogsController logsController;

    public LogFileProcessor() {
        logsController = new LogsController();
    }

    public void processLogfile(LogFileConf logFileConf) {
        PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        int tenantId = carbonContext.getTenantId();
        String userName = carbonContext.getUsername();
        LogReader logReader = new LogReader(logFileConf, tenantId, userName);
        new Thread(logReader).start();
    }

    private class LogReader implements Runnable {
        LogFileConf logFileConf;
        int tenantId;
        String username;

        public LogReader(LogFileConf logFileConf, int tenantId, String userName) {
            this.logFileConf = logFileConf;
            this.tenantId = tenantId;
            this.username = userName;
        }

        @Override
        public void run() {
            try {
                PrivilegedCarbonContext.startTenantFlow();
                PrivilegedCarbonContext privilegedCarbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                privilegedCarbonContext.setTenantId(tenantId);
                String tenantDomain = MultitenantUtils.getTenantDomain(username);

                privilegedCarbonContext.setTenantDomain(tenantDomain);
                String logFileDir = logFileConf.getLogStream();

                if (logFileDir != "") {
                    logFileDir = logFileDir.replace(',', '_');
                }

                File file = new File(tempFolderLocation + File.separator + logFileDir + File.separator + logFileConf.getFileName());
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    while (true) {
                        String line = br.readLine();
                        if (line != null) {
                            Map<String, Object> values = createLogEvent(line, logFileConf);
                            logsController.publishLogEvent(values, tenantId, username);
                        } else {
                            break;
                        }
                    }
                    br.close();
                    //remove the temp log file
                    FileUtils.deleteDirectory(new File(tempFolderLocation + File.separator + logFileDir));//TODO : delete file and dir
                } catch (Exception ex) {
                    log.error("Error reading", ex);
                }

            } finally {
                PrivilegedCarbonContext.endTenantFlow();
            }
        }
    }

    private Map<String, Object> createLogEvent(String logLine, LogFileConf logFileConf) {
        String streamId = "[";
        Map<String, Object> logEvent = new HashMap();
        String[] logStreams = logFileConf.getLogStream().split(",");
        if (logStreams.length > 0) {
            for (String subStream : logStreams)
                streamId = streamId + "'" + subStream.trim() + "'" + ",";
        }
        streamId = streamId.substring(0, (streamId.length() - 1)) + "]";
        if (logFileConf.getDelimiter() != null) {
            logEvent = LogPatternExtractor.processDelimiter(logLine, logFileConf.getDelimiter());
        } else if (logFileConf.getRegExPatterns() != null) {
            logEvent = LogPatternExtractor.processRegEx(logLine, logFileConf.getRegExPatterns());
        }
        //set @logstream
        logEvent.put("@logstream", streamId);
        //set @filename
        logEvent.put("@filename", logFileConf.getFileName());
        //set log message
        logEvent.put("message", logLine.trim());
        return logEvent;
    }

}
