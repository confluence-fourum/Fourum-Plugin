package forummacro.forum.ao;

import forummacro.rightsmanagement.ao.UserAO;
import net.java.ao.Entity;
import net.java.ao.OneToMany;
import net.java.ao.OneToOne;
import net.java.ao.schema.NotNull;

import java.util.Date;

/**
 * Active Object for Thread Contains setters and getters for thread fields.
 * Thread is linked to forum with setForum
 * <p/>
 * getItems is an ArrayList that contains the active objects of Item with an
 * OneToMany relationship.
 *
 * @author Nico Smolders
 */
public interface ThreadAO extends Entity {
    @NotNull
    public String getTitle();
    public void setTitle(String title);

    @NotNull
    public String getDescription();
    public void setDescription(String description);

    @NotNull
    public boolean getSticky();
    public void setSticky(boolean sticky);

    @NotNull
    public Date getCreationDate();
    public void setCreationDate(Date date);

    public Date getLastChangedDate();
    public void setLastChangedDate(Date date);

    @OneToMany
    public PostAO[] getPosts();

    public ForumAO getForum();
    public void setForum(ForumAO forum);

    @NotNull
    public String getUserKey();
    public void setUserKey(String userKey);

    public UserAO getUser();
    public void setUser(UserAO user);
    
    @OneToOne
    public StateAO getState();
}
