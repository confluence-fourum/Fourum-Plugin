package forummacro.rightsmanagement.exceptions;

import com.sun.jersey.api.Responses;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by Joost Elshof on 9-5-14.
 */
public class PermissionNotFoundException extends WebApplicationException {
    public PermissionNotFoundException() {
        Responses.notFound().build();
    }

    public PermissionNotFoundException(String message) {
        super(Response.status(Responses.NOT_FOUND).
                entity(message).type("text/plain").build());
    }
}
