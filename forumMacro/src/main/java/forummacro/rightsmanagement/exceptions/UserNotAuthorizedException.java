package forummacro.rightsmanagement.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by Rik on 09/05/2014.
 */
public class UserNotAuthorizedException extends WebApplicationException {
    public UserNotAuthorizedException() {
        super(Response.status(Response.Status.UNAUTHORIZED).build());
    }

    public UserNotAuthorizedException(String message) {
        super(Response.status(Response.Status.UNAUTHORIZED).
                entity(message).type("text/plain").build());
    }
}
