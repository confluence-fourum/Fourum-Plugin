package forummacro.forum.ao;

import forummacro.rightsmanagement.ao.RoleAO;
import forummacro.rightsmanagement.ao.UserAO;
import net.java.ao.Entity;
import net.java.ao.OneToMany;
import net.java.ao.schema.NotNull;

/**
 * Active Object for Forum Contains setters and getters for forum fields
 * <p/>
 * getThreads is an ArrayList that contains the active objects of Thread with an
 * OneToMany relationship.
 *
 * @author Nico Smolders
 */
public interface ForumAO extends Entity {

    public String getTitle();

    @NotNull
    public void setTitle(String title);

    public String getDescription();

    public void setDescription(String description);

    @OneToMany
    public ThreadAO[] getThreads();

    @OneToMany
    public UserAO[] getUsers();

    @OneToMany
    public RoleAO[] getRoles();
}
