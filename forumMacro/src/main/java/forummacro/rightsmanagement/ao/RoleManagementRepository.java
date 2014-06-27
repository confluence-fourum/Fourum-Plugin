package forummacro.rightsmanagement.ao;

import com.atlassian.activeobjects.tx.Transactional;

/**
 * Created by Rik on 10/06/2014.
 */
@Transactional
public interface RoleManagementRepository {
    void createRoletoPermissions(PermissionAO[] permissionAOs, RoleAO roleAO);

    RoleAO[] getRole(int forumId);

    RoleAO[] getRole(String roleName, int forumId);

    RoleAO createNewRoleAO(String roleName, String roleDescription, int forumId);

    void deleteRole(RoleAO roleAO);

    void deleteRoleToPermissionAOs(RoleAO roleAO);

    void saveRole(RoleAO roleAO);
}
