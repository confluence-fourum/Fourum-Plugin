package forummacro.forum.ao;

import com.atlassian.activeobjects.tx.Transactional;
import restapi.Serializables.SerializableAttachment;

import java.util.Date;
import java.util.List;

/**
 * Created by Cees Mandjes on 26-5-2014.
 */

@Transactional
public interface ItemRepository {
    List<PostAO> getAllItems(int threadID);

    PostAO getItem(int itemID);

    PostAO saveItemMessage(int itemID, String newMessage, Date creationDate, String userKey);

    AttachmentAO[] getAttachments(int itemID);

    void addAttachment(int itemID, Date date, String name, String url, String userKey);

    PostAO add(String message, Date date, int threadID, String userKey);

    PostAO add(String message, Date date, int threadID, String userKey,
               List<SerializableAttachment> attachments);

    PostAO add(String message, Date date, int threadID, String userKey,
                      List<SerializableAttachment> attachments, int quotedItemId);

    PostAO add(String message, Date date, int threadID, String userKey, int quotedItemId);

    void delete(int itemID, String userKey);

    PostAO createQuote(String userKey, int itemID, Date date, int quotedItemId);

    PostAO deleteQuote(String userKey, int itemID, Date date);
}
