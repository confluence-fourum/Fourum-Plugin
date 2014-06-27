package forummacro.forum.exceptions;

import com.sun.jersey.api.Responses;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ItemNotFoundException extends WebApplicationException {
    public ItemNotFoundException() {
        super(Responses.notFound().build());
    }

    public ItemNotFoundException(String message) {
        super(Response.status(Response.Status.NOT_FOUND).
                entity(message).type("text/plain").build());
    }
}