package org.wso2.carbon.la.restapi.providers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.la.alert.exception.ScheduleAlertException;
import org.wso2.carbon.la.restapi.beans.ResponseBean;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class ScheduleAlertExceptionMapper implements ExceptionMapper <ScheduleAlertException> {

    /** The logger. */
    private static final Log logger = LogFactory.getLog(ScheduleAlertExceptionMapper.class);


    @Override
    public Response toResponse(ScheduleAlertException exception) {
        ResponseBean errorResponse = new ResponseBean();
        errorResponse.setStatus(Constants.Status.FAILED);
        errorResponse.setMessage(exception.getMessage());
        logger.error("Error on Schedule Alert", exception);
        return Response.serverError().entity(errorResponse).build();
    }
}
