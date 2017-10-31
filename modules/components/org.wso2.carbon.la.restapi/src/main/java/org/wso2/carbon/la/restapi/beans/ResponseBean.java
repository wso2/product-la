package org.wso2.carbon.la.restapi.beans;

import javax.xml.bind.annotation.*;

/**
 * Created by nalaka on 4/29/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "status", "message" })
@XmlRootElement(name = "response")
public class ResponseBean {

    /** The status. */
    @XmlElement(required = true)
    private String status;

    /** The message. */
    @XmlElement(required = false)
    private String message;

    /**
     * Instantiates a new response bean.
     */
    public ResponseBean() {
    }

    /**
     * Instantiates a new response bean.
     * @param status the status
     */
    public ResponseBean(String status) {
        this.status = status;
    }

    /**
     * Gets the status.
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     * @param status the new status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the message.
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     * @param message the new message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}