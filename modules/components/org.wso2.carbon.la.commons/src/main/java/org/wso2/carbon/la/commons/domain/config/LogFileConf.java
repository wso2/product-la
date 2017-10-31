package org.wso2.carbon.la.commons.domain.config;

import java.util.Map;

/**
 * Log file upload configuration
 */
public class LogFileConf {
    public String logStream;
    public String fileName;
    public Map regExPatterns;
    public String delimiter;

    public Map getRegExPatterns() {
        return regExPatterns;
    }

    public void setRegExPatterns(Map regExPatterns) {
        this.regExPatterns = regExPatterns;
    }

    public String getLogStream() {
        return logStream;
    }

    public void setLogStream(String logStream) {
        this.logStream = logStream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
}
