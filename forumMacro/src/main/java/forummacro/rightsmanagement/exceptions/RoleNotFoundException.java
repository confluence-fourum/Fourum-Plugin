package forummacro.rightsmanagement.exceptions;

import com.sun.jersey.api.Responses;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by Rik on 09/05/2014.
 */
public class RoleNotFoundException extends WebApplicationException {
    public RoleNotFoundException() {
        super(Responses.notFound().build());
    }

    public RoleNotFoundException(String message) {
        super(Response.status(Responses.NOT_FOUND).
                entity(message).type("text/plain").build());
    }
}
