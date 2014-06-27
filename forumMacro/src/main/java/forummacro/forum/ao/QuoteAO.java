package forummacro.forum.ao;

import forummacro.rightsmanagement.ao.UserAO;
import net.java.ao.Entity;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;

import java.util.Date;


public interface QuoteAO extends Entity {

    @NotNull
    @StringLength(value = StringLength.UNLIMITED)
    public String getMessage();

    public void setMessage(String message);

    @NotNull
    public Date getQuoteDate();

    public void setQuoteDate(Date date);

    @NotNull
    public UserAO getUser();

    public void setUser(UserAO user);
}
