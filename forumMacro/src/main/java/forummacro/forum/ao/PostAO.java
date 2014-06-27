package forummacro.forum.ao;

import forummacro.rightsmanagement.ao.UserAO;
import net.java.ao.Entity;
import net.java.ao.OneToMany;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;

import java.util.Date;

/**
 * Created by Cees Mandjes on 26-5-2014.
 * @author Cees Mandjes
 * @author Nico Smolders
 */
public interface PostAO extends Entity {
    @NotNull
    @StringLength(value = StringLength.UNLIMITED)
    public String getMessage();

    public void setMessage(String message);

    @NotNull
    public Date getCreationDate();

    public void setCreationDate(Date date);

    @NotNull
    public Date getLastEditedDate();

    public void setLastEditedDate(Date date);

    public ThreadAO getThread();

    public void setThread(ThreadAO thread);

    public UserAO getUser();

    public void setUser(UserAO user);

    @NotNull
    public String getUserKey();

    public void setUserKey(String userKey);

    @OneToMany
    public AttachmentAO[] getAttachments();

    public QuoteAO getQuote();

    public void setQuote(QuoteAO quote);
}
