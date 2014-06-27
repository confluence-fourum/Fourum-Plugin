package forummacro.rightsmanagement.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by Joost Elshof on 12-5-14.
 */
public class UserAlreadyExistsException extends WebApplicationException {
    public UserAlreadyExistsException() {
        super(Response.status(Response.Status.CONFLICT).build());
    }

    public UserAlreadyExistsException(String message) {
        super(Response.status(Response.Status.CONFLICT).
                entity(message).type("text/plain").build());
    }
}
