package forummacro.rightsmanagement.ao;

import net.java.ao.Entity;
import net.java.ao.ManyToMany;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Table;

/**
 * Created by Rik on 12/05/2014.
 */
@Table("Permission")
public interface PermissionAO extends Entity {
    @NotNull
    String getName();

    void setName(String name);


    @ManyToMany(value = RoleToPermissionAO.class, through = "getRole", reverse = "getPermission")
    RoleAO[] getRoles();
}
