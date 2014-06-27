package restapi.exceptions;

import com.sun.jersey.api.Responses;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by Rik on 25/06/2014.
 */
public class EmptyInputException extends WebApplicationException {
    public EmptyInputException() {
        super(Responses.notAcceptable().build());
    }

    public EmptyInputException(String message){
        super(Response.status(Responses.NOT_ACCEPTABLE).
                entity(message).type("text/plain").build());
    }
}
