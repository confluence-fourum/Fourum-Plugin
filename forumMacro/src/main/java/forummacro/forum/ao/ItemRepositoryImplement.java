package forummacro.forum.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import forummacro.forum.exceptions.ItemNotFoundException;
import forummacro.forum.exceptions.QuoteNotFoundException;
import forummacro.forum.exceptions.ThreadIsClosedException;
import forummacro.forum.exceptions.ThreadNotFoundException;
import forummacro.rightsmanagement.PermissionConfig;
import forummacro.rightsmanagement.RightsManagement;
import net.java.ao.DBParam;
import restapi.Serializables.SerializableAttachment;

import java.util.Date;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Cees Mandjes
 * @author Nico Smolders
 */
public class ItemRepositoryImplement implements ItemRepository {


    private final ActiveObjects ao;
    private final RightsManagement rightsManagement;

    /**
     * The constructor
     *
     * @param ao
     * @param rightsManagement
     */
    public ItemRepositoryImplement(ActiveObjects ao,
                                   RightsManagement rightsManagement) {
        this.rightsManagement = rightsManagement;
        this.ao = ao;
    }

    /**
     * Gets all the items of the thread
     */
    @Override
    public List<PostAO> getAllItems(int threadID) {
        ThreadAO thread = ao.get(ThreadAO.class, threadID);
        if (thread == null) {
            throw new ThreadNotFoundException();
        } else {
            return newArrayList(thread.getPosts());
        }
    }

    @Override
    public PostAO getItem(int itemID) {
        PostAO post = ao.get(PostAO.class, itemID);
        if (post == null) {
            throw new ItemNotFoundException();
        } else {
            return post;
        }
    }

    /**
     * Deletes a item with the given id
     *
     * @param itemID
     * @param userKey
     */
    @Override
    public void delete(int itemID, String userKey) {
        PostAO post = getItem(itemID);
        checkAnyOwnRights(post, userKey, PermissionConfig.get("deleteOwnItem"), PermissionConfig.get("deleteAnyItem"));
        ao.delete(post);
    }

    @Override
    public AttachmentAO[] getAttachments(int itemID) {
        return getItem(itemID).getAttachments();
    }

    @Override
    public void addAttachment(int itemID, Date date, String name, String url, String userKey) {
        PostAO post = getItem(itemID);
        checkAnyOwnRights(post, userKey, PermissionConfig.get("addOwnAttachment"), PermissionConfig.get("addAnyAttachment"));
        final AttachmentAO attachment = ao.create(AttachmentAO.class, new DBParam("NAME", name), new DBParam("URL", url), new DBParam("POST_ID", itemID));
        attachment.save();
        post.setLastEditedDate(date);
        post.save();
    }

    /**
     * Adds a item
     *
     * @param message
     * @param date
     * @param threadID
     * @param threadID
     */
    @Override
    public PostAO add(String message, Date date, int threadID, String userKey) {
        ThreadAO threadAO = ao.get(ThreadAO.class, threadID);
        if (!threadAO.getState().getClosed()){
            int forumID = ao.get(ThreadAO.class, threadID).getForum().getID();
            rightsManagement.isAuthorized(userKey, PermissionConfig.PERMISSIONS.get("createItem"), forumID);
            final PostAO item = ao.create(PostAO.class, new DBParam("MESSAGE",
                            message), new DBParam("CREATION_DATE", date), new DBParam("LAST_EDITED_DATE", date),
                    new DBParam("USER_KEY", userKey));
            ThreadAO thread = ao.get(ThreadAO.class, threadID);
            setLastChangedDate(date, thread);
            item.setThread(thread);
            item.save();
            return item;
        }
        else
        {
            throw new ThreadIsClosedException();
        }
    }

    /**
     * Adds an item with attachments
     *
     * @param message
     * @param date
     * @param threadID
     * @param attachments
     * @param threadID
     */
    @Override
    public PostAO add(String message, Date date, int threadID, String userKey,
                      List<SerializableAttachment> attachments) {
        PostAO newPost = add(message, date, threadID, userKey);
        for (SerializableAttachment attachment : attachments) {
            addAttachment(newPost.getID(), date, attachment.getName(), attachment.getUrl(), userKey);
        }
        return newPost;
    }

    @Override
    public PostAO add(String message, Date date, int threadID, String userKey,
                      List<SerializableAttachment> attachments, int quotedItemId) {
        PostAO newPost = add(message, date, threadID, userKey, attachments);
        createQuote(userKey, newPost.getID(), date, quotedItemId);
        return newPost;
    }

    @Override
    public PostAO add(String message, Date date, int threadID, String userKey, int quotedItemId) {
        PostAO newPost = add(message, date, threadID, userKey);
        createQuote(userKey, newPost.getID(), date, quotedItemId);
        return newPost;
    }

    private void setLastChangedDate(Date date, ThreadAO thread) {
        thread.setLastChangedDate(date);
        thread.save();
    }

    /**
     * @param itemID
     * @param newMessage
     * @param date
     * @param userKey
     * @return
     */
    @Override
    public PostAO saveItemMessage(int itemID, String newMessage, Date date, String userKey) {
        PostAO editedItem = getItem(itemID);
        checkAnyOwnRights(editedItem, userKey, PermissionConfig.get("editAnyItem"), PermissionConfig.get("editOwnItem"));
        editedItem.setMessage(newMessage);
        editedItem.setLastEditedDate(date);
        editedItem.save();
        return editedItem;
    }

    private void checkAnyOwnRights(PostAO item, String userKey, String permissionAny, String permissionOwn) {
        int forumID = item.getThread().getForum().getID();
        if (item.getUserKey() == userKey) {
            rightsManagement.isAuthorized(userKey, permissionOwn, forumID);
        } else {
            rightsManagement.isAuthorized(userKey, permissionAny, forumID);
        }
    }

    @Override
    public PostAO createQuote(String userKey, int itemID, Date date, int quotedItemId) {
        PostAO editedItem = getItem(itemID);
        checkAnyOwnRights(editedItem, userKey, PermissionConfig.get("addAnyQuote"), PermissionConfig.get("addOwnQuote"));
        PostAO quotedItem = getItem(quotedItemId);
        String quotedItemMessage = quotedItem.getMessage();
        int quotedItemUserId = rightsManagement.getUser(quotedItem.getUserKey(), quotedItem.getThread().getForum().getID()).getID();
        QuoteAO quoteAO = ao.create(QuoteAO.class, new DBParam("MESSAGE", quotedItemMessage),
                new DBParam("QUOTE_DATE", date),
                new DBParam("USER_ID", quotedItemUserId));
        quoteAO.save();
        editedItem.setQuote(quoteAO);
        editedItem.setLastEditedDate(date);
        editedItem.save();
        return editedItem;
    }

    @Override
    public PostAO deleteQuote(String userKey, int itemID, Date date){
        PostAO editedItem = getItem(itemID);
        checkAnyOwnRights(editedItem, userKey, PermissionConfig.get("deleteAnyQuote"), PermissionConfig.get("deleteOwnQuote"));
        QuoteAO quote = editedItem.getQuote();
        if(quote != null){
            editedItem.setQuote(null);
            editedItem.setLastEditedDate(date);
            editedItem.save();
            ao.delete(quote);
            return editedItem;
        }
        throw new QuoteNotFoundException();
    }
}
