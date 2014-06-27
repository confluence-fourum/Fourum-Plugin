package restapi;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import forummacro.forum.ao.ItemRepository;
import restapi.Serializables.SerializableAttachment;
import restapi.Serializables.SerializablePost;
import restapi.exceptions.EmptyInputException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This class contains all rest API calls.
 *
 * @author Cees Mandjes
 * @author Jur Braam
 * @author Nico Smolders
 */

@Path("/posts")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class ItemRestService {
    private ItemRepository itemRepository;
    private Sanitizer sanitizer;

    /**
     * The constructor, gets the ItemRepository where items are saved in.
     */
    public ItemRestService(ItemRepository itemRepository, Sanitizer sanitizer) {
        this.itemRepository = itemRepository;
        this.sanitizer = sanitizer;
    }

    /**
     * This is the rest API call to get all items.
     *
     * @return all Items.
     */
    @GET
    @Path("items/{threadID}")
    public Response getAllItems(@PathParam("threadID") int threadID) {
        return Response.ok(SerializablePost.build(itemRepository.getAllItems(threadID))).build();
    }


    /**
     * This is the rest API call to get a specific Item.
     *
     * @return an item.
     */
    @GET
    @Path("item/{itemID}")
    public Response getItem(@PathParam("itemID") int itemID) {
        return Response.ok(SerializablePost.build(itemRepository.getItem(itemID))).build();
    }


    /**
     * This is the rest API call to save a new Item.
     *
     * @param message
     * @param threadID
     * @return a new item with the given parameters
     */
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
    @Path("items/withAttachments")
    public Response saveItemWithAttachments(@QueryParam(value = "message") String message,
                                            @QueryParam(value = "threadID") int threadID, @QueryParam(value = "attachments") String attachments) {
        Date date = Calendar.getInstance().getTime();
        String safeMessage = sanitizer.sanitizeInput(message);
        checkEmptyInput(safeMessage);
        String userKey = AuthenticatedUserThreadLocal.get().getKey().getStringValue();
        if (attachments.isEmpty()) {
            return getResponseItemAdd(threadID, date, safeMessage, userKey);
        } else {
            List<SerializableAttachment> safeAttachments = sanitizer.parseAttachmentJson(attachments);
            return Response.ok(
                    SerializablePost.build(itemRepository.add(safeMessage, date, threadID, userKey, safeAttachments))
            ).build();
        }
    }

    private void checkEmptyInput(String input) {
        if(input.isEmpty()){
            throw new EmptyInputException("Your input is empty");
        }
        ;
    }

    private Response getResponseItemAdd(int threadID, Date date, String safeMessage, String userKey) {
        return Response.ok(
                SerializablePost.build(itemRepository.add(safeMessage, date, threadID, userKey))
        ).build();
    }

    /**
     * This is the rest API call to save a new Item with attachments.
     *
     * @param message
     * @param threadID
     * @return a new item with the given parameters
     */
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Path("items")
    public Response saveItem(@FormParam(value = "message") String message,
                             @FormParam(value = "threadID") int threadID) {
        Date date = Calendar.getInstance().getTime();
        String safeMessage = sanitizer.sanitizeInput(message);
        checkEmptyInput(safeMessage);
        String userKey = AuthenticatedUserThreadLocal.get().getKey().getStringValue();
        return getResponseItemAdd(threadID, date, safeMessage, userKey);
    }

    /**
     * This is the rest API call to save a new Item with attachments.
     *
     * @param message
     * @param threadID
     * @param quotedItemID
     * @return a new item with the given parameters
     */
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Path("items/createItemWithQuote")
    public Response createItemWithQuote(@FormParam(value = "message") String message,
                                        @FormParam(value = "threadID") int threadID,
                                        @FormParam(value = "quotedItemID") int quotedItemID) {
        Date date = Calendar.getInstance().getTime();
        String safeMessage = sanitizer.sanitizeInput(message);
        checkEmptyInput(safeMessage);
        String userKey = AuthenticatedUserThreadLocal.get().getKey().getStringValue();
        return Response.ok(
                SerializablePost.build(itemRepository.add(safeMessage, date, threadID, userKey, quotedItemID))
        ).build();
    }

    /**
     * This is the rest API call to save an edited message.
     *
     * @param message
     * @param itemID
     * @return a new item with the given parameters
     */
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Path("saveItemMessage")
    public Response saveItemMessage(@FormParam(value = "itemID") int itemID,
                                    @FormParam(value = "message") String message) {
        Date date = Calendar.getInstance().getTime();
        String userKey = AuthenticatedUserThreadLocal.get().getKey().getStringValue();
        String safeMessage = sanitizer.sanitizeInput(message);
        checkEmptyInput(safeMessage);
        return Response.ok(
                SerializablePost.build(itemRepository.saveItemMessage(itemID, safeMessage, date, userKey))
        ).build();
    }

    /**
     * This is the rest API call to delete an Item with the given id.
     *
     * @return a response
     */
    @DELETE
    @Path("items/{itemID}")
    public Response deleteItem(@PathParam("itemID") int itemID) {
        String userKey = AuthenticatedUserThreadLocal.get().getKey().getStringValue();
        itemRepository.delete(itemID, userKey);
        return Response.ok().build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Path("items/{itemID}/addAttachment")
    public Response addAttachment(@PathParam("itemID") int itemID, @FormParam(value = "name") String name, @FormParam(value = "url") String url) {
        String safeName = sanitizer.sanitizeInput(name);
        String safeUrl = sanitizer.sanitizeInput(url);
        checkEmptyInput(safeName);
        checkEmptyInput(safeUrl);
        String userKey = AuthenticatedUserThreadLocal.get().getKey().getStringValue();
        Date date = Calendar.getInstance().getTime();
        itemRepository.addAttachment(itemID, date, safeName, safeUrl, userKey);
        return Response.ok(SerializablePost.build(itemRepository.getItem(itemID))).build();
    }

    @GET
    @Path("items/{itemID}/attachments")
    public Response getAttachments(@PathParam("itemID") int itemID) {
        return Response.ok(SerializableAttachment.build(itemRepository.getAttachments(itemID))).build();
    }

    /**
     * Creates a new quote within a post.
     *
     * @param itemID
     * @param quotedItemId
     * @return A response with the new created quote
     */
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Path("item/postQuote")
    public Response createQuote ( @FormParam(value = "itemID") int itemID,
                                  @FormParam(value = "quotedItemId") int quotedItemId)
    {
        String userKey = AuthenticatedUserThreadLocal.get().getKey().getStringValue();
        Date date = Calendar.getInstance().getTime();
        return Response.ok(
                SerializablePost.build(itemRepository.createQuote(userKey, itemID, date, quotedItemId))
        ).build();
    }

    /**
     * This is the rest API call to delete an Quote from an Item with the given id.
     *
     * @return a response
     */
    @DELETE
    @Path("items/deleteQuote/{itemID}")
    public Response deleteQuote(@PathParam("itemID") int itemID) {
        String userKey = AuthenticatedUserThreadLocal.get().getKey().getStringValue();
        Date date = Calendar.getInstance().getTime();
        itemRepository.deleteQuote(userKey, itemID, date);
        return Response.ok().build();
    }
}

