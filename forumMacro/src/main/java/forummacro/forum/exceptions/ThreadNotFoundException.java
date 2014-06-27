package forummacro.forum.exceptions;

import com.sun.jersey.api.Responses;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by Cees Mandjes on 27-5-2014.
 */
public class ThreadNotFoundException extends WebApplicationException {
    public ThreadNotFoundException() {
        super(Responses.notFound().build());
    }

    public ThreadNotFoundException(String message) {
        super(Response.status(Response.Status.NOT_FOUND).
                entity(message).type("text/plain").build());
    }
}

