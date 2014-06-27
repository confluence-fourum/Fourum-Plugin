package forummacro.rightsmanagement.ao;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

/**
 * Created by Rik on 15/05/2014.
 */
@Table("RolePermissions")
public interface RoleToPermissionAO extends Entity {
    public RoleAO getRole();

    public void setRole(RoleAO role);

    public PermissionAO getPermission();

    public void setPermission(PermissionAO permission);
}