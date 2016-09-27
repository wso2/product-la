package org.wso2.carbon.la.alert.exception;

/**
 * Created by nalaka on 5/30/16.
 */
public class AlertConfigurationException extends Exception {

    private static final long serialVersionUID = 5640682721304960320L;

    public AlertConfigurationException(String msg) {
        super(msg);
    }

    public AlertConfigurationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
