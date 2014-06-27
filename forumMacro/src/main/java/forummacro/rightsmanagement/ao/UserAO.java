package forummacro.rightsmanagement.ao;

import forummacro.forum.ao.ForumAO;
import forummacro.forum.ao.PostAO;
import forummacro.forum.ao.ThreadAO;
import net.java.ao.Entity;
import net.java.ao.OneToMany;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Table;

/**
 * Created by Rik on 12/05/2014.
 */
@Table("User")
public interface UserAO extends Entity {
    @NotNull
    String getUserKey();

    void setUserKey(String userKey);

    RoleAO getRole();

    void setRole(RoleAO role);

    @NotNull
    ForumAO getForum();

    void setForum(ForumAO forum);

    @OneToMany
    PostAO[] getPosts();

    @OneToMany
    ThreadAO[] getThreads();
}