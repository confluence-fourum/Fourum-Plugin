package forummacro.rightsmanagement.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import net.java.ao.DBParam;

/**
 * Created by Joost Elshof on 10-6-14.
 */
public class RoleManagementRepositoryImplement implements RoleManagementRepository {
    private final ActiveObjects ao;

    public RoleManagementRepositoryImplement(ActiveObjects ao) {
        this.ao = ao;
    }

    @Override
    public void createRoletoPermissions(PermissionAO[] permissionAOs, RoleAO roleAO) {
        for (PermissionAO permissionAO : permissionAOs) {
            RoleToPermissionAO roleToPermissionAO = ao.create(RoleToPermissionAO.class);
            roleToPermissionAO.setRole(roleAO);
            roleToPermissionAO.setPermission(permissionAO);
            roleToPermissionAO.save();
        }
    }

    @Override
    public RoleAO[] getRole(int forumId) {
        return ao.find(RoleAO.class, "FORUM_ID = ?", forumId);
    }

    @Override
    public RoleAO[] getRole(String roleName, int forumId) {
        return ao.find(RoleAO.class, "NAME = ? AND FORUM_ID = ?", roleName, forumId);
    }

    @Override
    public RoleAO createNewRoleAO(String roleName, String roleDescription, int forumId) {
        final RoleAO roleAO = ao.create(RoleAO.class, new DBParam("NAME", roleName), new DBParam("FORUM_ID", forumId));
        roleAO.setDescription(roleDescription);
        roleAO.save();
        return roleAO;
    }

    @Override
    public void deleteRole(RoleAO roleAO) {
        ao.delete(roleAO);
    }

    @Override
    public void deleteRoleToPermissionAOs(RoleAO roleAO) {
        RoleToPermissionAO[] roleToPermissionAOs = ao.find(RoleToPermissionAO.class);
        for (RoleToPermissionAO roleToPermissionAO : roleToPermissionAOs) {
            if (roleToPermissionAO.getRole().getID() == roleAO.getID()) {
                ao.delete(roleToPermissionAO);
            }
        }
    }

    @Override
    public void saveRole(RoleAO roleAO) {
        roleAO.save();
    }
}
