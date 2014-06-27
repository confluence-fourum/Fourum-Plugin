package restapi.Serializables;

import forummacro.forum.ao.AttachmentAO;
import forummacro.forum.ao.PostAO;
import forummacro.forum.ao.QuoteAO;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Cees Mandjes on 28-5-2014.
 */

@XmlRootElement
public class SerializablePost {

    private String message;
    private int id;
    private Date creationDate;
    private Date lastEditedDate;
    private String userKey;
    private SerializableAttachment[] attachments;
    private SerializableQuote quote;

    private SerializablePost(String message, int id, Date creationDate, Date lastEditedDate, String userKey, SerializableAttachment[] attachments, SerializableQuote quote) {
        this.message = message;
        this.id = id;
        this.creationDate = creationDate;
        this.lastEditedDate = lastEditedDate;
        this.userKey = userKey;
        this.attachments = attachments;
        this.quote = quote;
    }

    public static SerializablePost build(PostAO itemAO) {
        AttachmentAO[] attachments = itemAO.getAttachments();
        QuoteAO quote = itemAO.getQuote();
        return new SerializablePost(itemAO.getMessage(), itemAO.getID(),
                itemAO.getCreationDate(), itemAO.getLastEditedDate(), itemAO.getUserKey(),
                SerializableAttachment.build(attachments),
                SerializableQuote.build(quote));
    }

    public static SerializablePost[] build(List<PostAO> items) {
        List<SerializablePost> serializablePosts = new ArrayList<SerializablePost>();
        for (PostAO item : items) {
            serializablePosts.add(SerializablePost.build(item));
        }
        return serializablePosts.toArray(new SerializablePost[]{});
    }

    @XmlElement
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @XmlElement
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date date) {
        this.creationDate = date;
    }

    @XmlElement
    public Date getLastEditedDate() {
        return lastEditedDate;
    }

    public void setLastEditedDate(Date date) {
        this.lastEditedDate = date;
    }

    @XmlElement
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    @XmlElement
    public SerializableAttachment[] getAttachments() {
        return attachments;
    }

    public void setAttachments(SerializableAttachment[] attachments) {
        this.attachments = attachments;
    }

    @XmlElement
    public SerializableQuote getQuote() {
        return quote;
    }

    public void setQuote(SerializableQuote quote) {
        this.quote = quote;
    }


}
