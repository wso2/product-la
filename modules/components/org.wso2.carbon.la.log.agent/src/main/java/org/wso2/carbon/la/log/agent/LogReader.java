package org.wso2.carbon.la.log.agent;

import java.io.*;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.wso2.carbon.databridge.commons.utils.DataBridgeCommonsUtils;
import org.wso2.carbon.la.log.agent.conf.LogGroup;
import org.wso2.carbon.la.log.agent.conf.ServerConfig;
import org.wso2.carbon.la.log.agent.data.LogEvent;
import org.wso2.carbon.la.log.agent.data.LogPublisher;
import org.wso2.carbon.la.log.agent.filters.AbstractFilter;
import org.wso2.carbon.la.log.agent.util.PublisherUtil;

/**
 * Reads log line by line
 */
public class LogReader {

    private static final Logger logger = Logger.getLogger("LogReader");
    private final File file;
    private long offset = 0;
    private int lineCount = 0;
    private boolean ended = false;
    private WatchService watchService = null;
    ArrayDeque<String> lines = new ArrayDeque<String>();
    private boolean readFromTop = true;
    private LogPublisher logPublisher;
    private LogGroup logGroup;
    private String streamId;

    /**
     * Allows output of a file that is being updated by another process.
     *
     * @param logGroup
     * @param logPublisher
     */
    public LogReader(LogPublisher logPublisher, LogGroup logGroup) throws FileNotFoundException {
        this.file = new File(logGroup.getLogInput().getFilePath());
        setStreamId(logGroup);
        this.logGroup=logGroup;
        this.logPublisher=logPublisher;
    }

    /**
     * Start watch.
     */
    public void start() {
        updateOffset();
        // listens for FS events
        new Thread(new LogWatcher()).start();

        new Thread(new TailLog()).start();

    }

    /**
     * Stop watch.
     */
    public void stop() {
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException ex) {
                logger.info("Error closing watch service");
            }
            watchService = null;
        }
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(LogGroup logGroup) {
        this.streamId =DataBridgeCommonsUtils.generateStreamId(logGroup.getGroupName(),logGroup.getVersion()); ;
    }

    private synchronized void updateOffset() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            br.skip(offset);
            while (true) {
                String line = br.readLine();
                if (line != null) {
                    if (isReadFromTop()) {
                        lines.push(line);
                    }
                    // this may need tweaking if >1 line terminator char
                    offset += line.length() + 1;
                } else {
                    break;
                }
            }
            br.close();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error reading", ex);
        }
    }

    /**
     * @return true if lines are available to read
     */
    public boolean linesAvailable() {
        return !lines.isEmpty();
    }

    /**
     * @return next unread line
     */
    public synchronized String getLine() {
        if (lines.isEmpty()) {
            return null;
        } else {
            lineCount++;
            return lines.removeLast();
        }
    }

    /**
     * @return true if no more lines will ever be available,
     * because stop() has been called or the timeout has expired
     */
    public boolean hasEnded() {
        return ended;
    }

    /**
     * @return next line that will be returned; zero-based
     */
    public int getLineNumber() {
        return lineCount;
    }


    private class TailLog implements Runnable {
        public void run() {
            while (!hasEnded()) {
                while (linesAvailable()) {
                    //System.out.println(getLineNumber() + ": " + getLine());
                    try {
                        String logLine = getLine();
                        if (logLine != null && logLine != "") {
                            logPublisher.publish(constructLogEvent(logLine), streamId);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class LogWatcher implements Runnable {
        private final Path path = file.toPath().getParent();

        @Override
        public void run() {
            try {
                setReadFromTop(true);
                watchService = path.getFileSystem().newWatchService();
                path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                while (true) {
                    WatchKey watchKey = watchService.take();
                    if (!watchKey.reset()) {
                        stop();
                        break;
                    } else if (!watchKey.pollEvents().isEmpty()) {
                        updateOffset();
                    }
                    Thread.sleep(500);
                }
            } catch (InterruptedException ex) {
                logger.info("Tail interrupted");
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Tail failed", ex);
            } catch (ClosedWatchServiceException ex) {
                // no warning required - this was a call to stop()
            }
            ended = true;
        }
    }


    public boolean isReadFromTop() {
        return readFromTop;
    }

    public void setReadFromTop(boolean readFromTop) {
        this.readFromTop = readFromTop;
    }


    private LogEvent constructLogEvent(String logLine) {
        LogEvent logEvent = new LogEvent();

        try {
            logEvent.setHost(PublisherUtil.getLocalAddress().getHostAddress());
            logEvent.setMessage(logLine);
            logEvent.setExtractedValues(applyFilters(logGroup.getFilters(), logLine));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return logEvent;
    }

    private Map<String, String> applyFilters(List<AbstractFilter> filters,String logLine){
        Map<String, String> values = new HashMap<String, String>();

        for(AbstractFilter ab : filters){
            ab.process(logLine, values);
        }

        return values;
    }
}
