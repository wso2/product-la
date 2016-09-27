package org.wso2.carbon.la.core.exceptions;

public class LogsControllerException extends Exception{
    private static final long serialVersionUID = -8346783974372279074L;

    public LogsControllerException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogsControllerException(String message) {
        super(message);
    }

    public LogsControllerException(Throwable cause) {
        super(cause);
    }
}
