package restapi;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;

import forummacro.forum.ao.ThreadRepository;
import restapi.Serializables.SerializableState;
import restapi.Serializables.SerializableThread;
import restapi.exceptions.EmptyInputException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Calendar;
import java.util.Date;

/**
 * This class contains all rest API calls.
 *
 * @author Nico Smolders
 * @author Jur Braam
 */
@Path("/")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class ThreadRestService {

    private ThreadRepository threadRepo;
    private Sanitizer sanitizer;

    private void checkEmptyInput(String input) {
        if(input.isEmpty()){
            throw new EmptyInputException("Your input is empty");
        }
        ;
    }

    /**
     * The constructor, gets the threadRepo where threads are saved in.
     *
     * @param threadRepo
     */
    public ThreadRestService(ThreadRepository threadRepo, Sanitizer sanitizer) {
        this.sanitizer = sanitizer;
        this.threadRepo = threadRepo;
    }

    /**
     * This is the rest API call to get all threads.
     *
     * @return all threads.
     */
    @GET
    @Path("threads/{forumID}")
    public Response getAllThreads(@PathParam("forumID") int forumID) {
        return Response.ok(SerializableThread.build(threadRepo.getAllThreads(forumID))).build();
    }

    /**
     * This is the rest API call to get a specific thread.
     *
     * @return a thread.
     */
    @GET
    @Path("threads/{forumID}/{threadID}")
    public Response getThread(@PathParam("forumID") int forumID, @PathParam("threadID") int threadID) {
        return Response.ok(SerializableThread.build(threadRepo.getThread(forumID, threadID))).build();
    }

    /**
     * This is the rest API call to save a new thread.
     *
     * @param title
     * @param description
     * @param sticky
     * @return a new thread with the given parameters
     */

    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Path("threads")
    public Response saveThread(@FormParam(value = "title") String title,
                               @FormParam(value = "description") String description,
                               @FormParam(value = "sticky") boolean sticky,
                               @FormParam(value = "forumID") int forumID) {
        System.out.println("forumID: " + forumID);
        String safeTitle = sanitizer.sanitizeInput(title);
        String safeDescription = sanitizer.sanitizeInput(description);
        checkEmptyInput(safeTitle);
        checkEmptyInput(safeDescription);
        Date date = Calendar.getInstance().getTime();
        String userKey = AuthenticatedUserThreadLocal.get().getKey().getStringValue();
        return Response.ok(
                SerializableThread.build(threadRepo.add(safeTitle, safeDescription,
                        sticky, date, forumID, userKey))
        ).build();
    }

    /**
     * This is the rest API call to delete a thread with the given id.
     *
     * @param threadID
     * @return a response
     */
    @DELETE
    @Path("threads/{threadID}")
    public Response deleteThread(@PathParam("threadID") int threadID) {
        String userKey = AuthenticatedUserThreadLocal.get().getKey().getStringValue();
        threadRepo.delete(threadID, userKey);
        return Response.ok().build();
    }
    
    @GET
    @Path("threads/getState/{threadID}")
    public Response getState(@PathParam("threadID") int threadID) {
    	return Response.ok(
    			SerializableState.build(threadRepo.getState(threadID))
    			).build();
    }
    
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Path("threads/setState")
    public Response setState(@FormParam(value = "threadID") int threadID, @FormParam(value = "closed") boolean closed) {
    	String userKey = AuthenticatedUserThreadLocal.get().getKey().getStringValue();
    	return Response.ok(
    			SerializableState.build(threadRepo.setState(threadID, closed, userKey))
    			).build();
    }
}
