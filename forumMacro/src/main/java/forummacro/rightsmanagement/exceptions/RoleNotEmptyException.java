package forummacro.rightsmanagement.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by Joost Elshof on 13-5-14.
 */
public class RoleNotEmptyException extends WebApplicationException {
    public RoleNotEmptyException() {
        super(Response.status(Response.Status.CONFLICT).build());
    }

    public RoleNotEmptyException(String message) {
        super(Response.status(Response.Status.CONFLICT).
                entity(message).type("text/plain").build());
    }
}
