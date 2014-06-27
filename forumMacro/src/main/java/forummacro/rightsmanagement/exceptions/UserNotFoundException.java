package forummacro.rightsmanagement.exceptions;

import com.sun.jersey.api.Responses;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by Joost Elshof on 9-5-14.
 */
public class UserNotFoundException extends WebApplicationException {
    public UserNotFoundException() {
        super(Responses.notFound().build());
    }

    public UserNotFoundException(String message) {
        super(Response.status(Responses.NOT_FOUND).
                entity(message).type("text/plain").build());
    }
}
