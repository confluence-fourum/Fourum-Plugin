package forummacro.rightsmanagement.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import net.java.ao.DBParam;

/**
 * Created by Joost Elshof on 10-6-14.
 */
public class PermissionManagementRepositoryImplement implements PermissionManagementRepository {
    private final ActiveObjects ao;

    public PermissionManagementRepositoryImplement(ActiveObjects ao) {
        this.ao = ao;
    }

    @Override
    public PermissionAO[] getPermissions() {
        return ao.find(PermissionAO.class);
    }

    @Override
    public PermissionAO[] getPermission(final String permissionName) {
        return ao.find(PermissionAO.class, "NAME = ?", permissionName);
    }

    @Override
    public void createPermissions(String[] permissionNames) {
        for (String permissionName : permissionNames) {
            PermissionAO permissionAO = ao.create(PermissionAO.class, new DBParam("NAME", permissionName));
            permissionAO.save();
        }
    }
}
