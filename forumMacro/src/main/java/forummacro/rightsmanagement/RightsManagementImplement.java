package forummacro.rightsmanagement;

import forummacro.forum.ao.ForumRepository;
import forummacro.rightsmanagement.ao.*;
import forummacro.rightsmanagement.exceptions.*;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by Joost Elshof on 9-5-14.
 */
public class RightsManagementImplement implements RightsManagement {
    private final UserManagementRepository userManagementRepository;
    private final RoleManagementRepository roleManagementRepository;
    private final PermissionManagementRepository permissionManagementRepository;
    private final ForumRepository forumRepository;

    public RightsManagementImplement(UserManagementRepository userManagementRepository,
                                     RoleManagementRepository roleManagementRepository,
                                     PermissionManagementRepository permissionManagementRepository,
                                     ForumRepository forumRepository) {
        this.userManagementRepository = userManagementRepository;
        this.roleManagementRepository = roleManagementRepository;
        this.permissionManagementRepository = permissionManagementRepository;
        this.forumRepository = forumRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void isAuthorized(String userKey, String permissionName, int forumId) {
        UserAO user;
        try {
            user = getUser(userKey, forumId);
        } catch (UserNotFoundException e) {
            throw new UserNotAuthorizedException("User not authorized to perform  " + permissionName);
        }
        List<PermissionAO> permissions = newArrayList(user.getRole().getPermissions());
        if (!permissions.contains(getPermission(permissionName))) {
            throw new UserNotAuthorizedException("User not authorized to perform  " + permissionName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAO[] getUser(int forumId) {
        return userManagementRepository.getUser(forumId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAO getUser(String userKey, int forumId) {
        UserAO[] user = userManagementRepository.getUser(userKey, forumId);
        if (user.length > 0) {
            return user[0];
        }
        throw new UserNotFoundException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAO[] getUser(List<String> userKeys, int forumId) {
        List<UserAO> users = new ArrayList<UserAO>();
        for (String userKey : userKeys) {
            users.add(getUser(userKey, forumId));
        }
        return users.toArray(new UserAO[]{});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAO addUser(String userKey, String newUserKey, String roleName, int forumId) {
        isAuthorized(userKey, PermissionConfig.PERMISSIONS.get("addUser"), forumId);
        return createNewUserAO(newUserKey, roleName, forumId);
    }

    private UserAO createNewUserAO(String newUserKey, String roleName, int forumId) {
        for (UserAO user : getUser(forumId)) {
            if (user.getUserKey().equals(newUserKey)) {
                throw new UserAlreadyExistsException();
            }
        }
        forumRepository.getForum(forumId);
        final RoleAO roleAO = getRole(roleName, forumId);
        return userManagementRepository.createNewUserAO(newUserKey, forumId, roleAO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAO changeUserRole(String userKey, String changedUserKey, String newRoleName, int forumId) {
        isAuthorized(userKey, PermissionConfig.PERMISSIONS.get("changeUserRole"), forumId);
        UserAO userAO = getUser(changedUserKey, forumId);
        userAO.setRole(getRole(newRoleName, forumId));
        userManagementRepository.saveUser(userAO);
        return userAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(String userKey, final String toBeDeletedUserKey, int forumId) {
        //TODO: make clever function to handle deletion. (pay attention to items and thread)
        isAuthorized(userKey, PermissionConfig.PERMISSIONS.get("deleteUser"), forumId);
        userManagementRepository.deleteUser(getUser(toBeDeletedUserKey, forumId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initPermissions() {
        permissionManagementRepository.createPermissions(PermissionConfig.getPermissions().toArray(new String[]{}));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAO initAdmin(final String userKey, int forumId) {
        RoleAO adminRole = createNewRoleAO(PermissionConfig.ADMIN, PermissionConfig.ADMINDESCRIPTION, forumId);
        roleManagementRepository.createRoletoPermissions(getPermissions(), adminRole);
        return createNewUserAO(userKey, adminRole.getName(), forumId);
    }


    /**
     * {@inheritDoc}
     */
    public void initDeletedUserRole(int forumId){
        createNewRoleAO(PermissionConfig.DELETEDROLE, PermissionConfig.DELETEDROLEDESCRIPTION, forumId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoleAO[] getRole(int forumId) {
        return roleManagementRepository.getRole(forumId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoleAO getRole(String roleName, int forumId) {
        RoleAO[] role = roleManagementRepository.getRole(roleName, forumId);
        if (role.length > 0) {
            return role[0];
        }
        throw new RoleNotFoundException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoleAO createRole(String userKey, String name, String description, int forumId) {
        isAuthorized(userKey, PermissionConfig.PERMISSIONS.get("createRole"), forumId);
        return createNewRoleAO(name, description, forumId);
    }

    private RoleAO createNewRoleAO(String roleName, String roleDescription, int forumId) {
        for (RoleAO role : getRole(forumId)) {
            if (role.getName().equals(roleName)) {
                throw new RoleAlreadyExistsException();
            }
        }
        return roleManagementRepository.createNewRoleAO(roleName, roleDescription, forumId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoleAO setRoleName(String userKey, String roleName, String newRoleName, int forumId) {
        isAuthorized(userKey, PermissionConfig.PERMISSIONS.get("setRoleName"), forumId);
        for (RoleAO role : getRole(forumId)) {
            if (role.getName().equals(newRoleName)) {
                throw new RoleAlreadyExistsException();
            }
        }
        final RoleAO roleAO = getRole(roleName, forumId);
        roleAO.setName(newRoleName);
        roleManagementRepository.saveRole(roleAO);
        return roleAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoleAO setRoleDescription(String userKey, String roleName, String newRoleDescription, int forumId) {
        isAuthorized(userKey, PermissionConfig.PERMISSIONS.get("setRoleDescription"), forumId);
        final RoleAO roleAO = getRole(roleName, forumId);
        roleAO.setDescription(newRoleDescription);
        roleManagementRepository.saveRole(roleAO);
        return roleAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteRole(String userKey, String roleName, int forumId) {
        isAuthorized(userKey, PermissionConfig.PERMISSIONS.get("deleteRole"), forumId);
        final RoleAO roleAO = getRole(roleName, forumId);
        for (UserAO userAO : getUser(forumId)) {
            if (userAO.getRole().equals(roleAO)) {
                throw new RoleNotEmptyException();
            }
        }
        roleManagementRepository.deleteRole(roleAO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PermissionAO[] getPermissions() {
        return permissionManagementRepository.getPermissions();
    }

    private PermissionAO[] getPermissions(final List<String> permissionNames) {
        List<PermissionAO> permissions = new ArrayList<PermissionAO>();
        for (String permissionName : permissionNames) {
            permissions.add(getPermission(permissionName));
        }
        return permissions.toArray(new PermissionAO[]{});
    }

    public PermissionAO[] getPermissions(String roleName, int forumId) {
        RoleAO role = getRole(roleName, forumId);
        return role.getPermissions();
    }

    private PermissionAO getPermission(final String permissionName) {
        PermissionAO[] permission = permissionManagementRepository.getPermission(permissionName);
        if (permission.length > 0) {
            return permission[0];
        }
        throw new PermissionNotFoundException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoleAO setPermissions(String userKey, String roleName, List<String> permissionNames, int forumId) {
        isAuthorized(userKey, PermissionConfig.PERMISSIONS.get("setPermissions"), forumId);
        final RoleAO roleAO = getRole(roleName, forumId);
        PermissionAO[] permissions = getPermissions(permissionNames);
        roleManagementRepository.deleteRoleToPermissionAOs(roleAO);
        roleManagementRepository.createRoletoPermissions(permissions, roleAO);
        return roleAO;
    }
}
