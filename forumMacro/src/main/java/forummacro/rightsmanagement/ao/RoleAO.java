package forummacro.rightsmanagement.ao;

import forummacro.forum.ao.ForumAO;
import net.java.ao.Entity;
import net.java.ao.ManyToMany;
import net.java.ao.schema.Table;

/**
 * Created by Rik on 12/05/2014.
 */
@Table("Role")
public interface RoleAO extends Entity {
    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    @ManyToMany(value = RoleToPermissionAO.class, through = "getPermission", reverse = "getRole")
    PermissionAO[] getPermissions();

    ForumAO getForum();

    void setForum(ForumAO forum);
}
