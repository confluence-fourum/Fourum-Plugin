package forummacro.rightsmanagement.ao;

import com.atlassian.activeobjects.tx.Transactional;

/**
 * Created by Rik on 10/06/2014.
 */
@Transactional
public interface PermissionManagementRepository {
    PermissionAO[] getPermissions();

    PermissionAO[] getPermission(String permissionName);

    void createPermissions(String[] permissionNames);
}
