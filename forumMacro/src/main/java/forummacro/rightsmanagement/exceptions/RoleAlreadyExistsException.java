package forummacro.rightsmanagement.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by Joost Elshof on 12-5-14.
 */
public class RoleAlreadyExistsException extends WebApplicationException {
    public RoleAlreadyExistsException() {
        super(Response.status(Response.Status.CONFLICT).build());
    }

    public RoleAlreadyExistsException(String message) {
        super(Response.status(Response.Status.CONFLICT).
                entity(message).type("text/plain").build());
    }
}
