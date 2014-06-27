package forummacro.rightsmanagement;

import com.atlassian.activeobjects.tx.Transactional;
import forummacro.rightsmanagement.ao.PermissionAO;
import forummacro.rightsmanagement.ao.RoleAO;
import forummacro.rightsmanagement.ao.UserAO;

import java.util.List;

/**
 * Created by Rik on 09/05/2014.
 */


@Transactional
public interface RightsManagement {
    /**
     * Checks if the user is authorized to do the action with the supplied permissionId
     * Throws a UserNotAuthorized runtime exception if the user isn't authorized otherwise it does nothing
     *
     * @param userKey        the user key of the user that needs to be checked on authorization
     * @param permissionName the name of the permission the user needs
     * @param forumId
     */
    public void isAuthorized(String userKey, String permissionName, int forumId);

    /**
     * Returns the list of all users in the forum
     *
     * @param forumId
     * @return the list of users
     */
    public UserAO[] getUser(int forumId);

    /**
     * Returns the forum user that belongs to the given userKey
     *
     * @param userKey
     * @param forumId
     * @return the UserAO that was found
     */
    public UserAO getUser(String userKey, int forumId);

    /**
     * Returns the forum users that belong to the given list of userKeys
     *
     * @param userKeys
     * @param forumId
     * @return the list of Users found
     */
    public UserAO[] getUser(List<String> userKeys, int forumId);

    /**
     * Adds a UserAO to the forum given the userKey is authorized, the confluenceUserKey exists and the roleId is valid
     *
     * @param userKey
     * @param newUserKey the user who is being added
     * @param roleName
     * @param forumId
     */
    public UserAO addUser(String userKey, String newUserKey, String roleName, int forumId);

    /**
     * Changes the user role of the given user to a new role
     *
     * @param userKey
     * @param changedUserKey the userKey of the user that needs a role change
     * @param newRoleName    the new role id for the user
     * @param forumId
     */
    public UserAO changeUserRole(String userKey, String changedUserKey, String newRoleName, int forumId);

    /**
     * Removes the user behind the given userKey
     *
     * @param userKey            the userKey of the user performing the action
     * @param toBeDeletedUserKey the userKey of the user that needs to be removed
     * @param forumId
     */
    public void deleteUser(String userKey, String toBeDeletedUserKey, int forumId);

    /**
     * Returns the list of all roles that exist in the given forum
     *
     * @param forumId the forum from which you want to get the roles
     * @return the list of all existing roles
     */
    public RoleAO[] getRole(int forumId);

    /**
     * Returns the role that is behind the given roleId
     *
     * @param roleName the role id of the role to return
     * @param forumId
     * @return the RoleAO asked for
     */
    public RoleAO getRole(String roleName, int forumId);

    /**
     * Creates a new role with the given name and description
     *
     * @param userKey
     * @param name        the name of the role
     * @param description a short description of the role
     * @param forumId
     */
    public RoleAO createRole(String userKey, String name, String description, int forumId);

    /**
     * Changes the role name of the role that belongs to the given role name
     *
     * @param userKey     userKey of the user performing the action
     * @param roleName    the name of the role whose name needs to be changed
     * @param newRoleName the new name of the role
     * @param forumId
     */
    public RoleAO setRoleName(String userKey, String roleName, String newRoleName, int forumId);

    /**
     * Changes the role description of the role that belongs to the given role name
     *
     * @param userKey            userKey of the user performing the action
     * @param roleName           the role id of the role whose description needs to be changed
     * @param newRoleDescription
     * @param forumId
     */
    public RoleAO setRoleDescription(String userKey, String roleName, String newRoleDescription, int forumId);

    /**
     * Deletes the role that belongs to the given role name
     *
     * @param userKey  the userKey of the user performing the action
     * @param roleName the name of the role you want to delete
     * @param forumId
     */
    public void deleteRole(String userKey, String roleName, int forumId);

    /**
     * Returns all the existing Permissions
     *
     * @return all existing Permission
     */
    public PermissionAO[] getPermissions();

    /**
     * Returns the permissions of the role that belongs to the given role name
     *
     * @param roleName the role whose permissions you want
     * @param forumId
     * @return
     */
    public PermissionAO[] getPermissions(String roleName, int forumId);

    /**
     * Sets the Permissions of the given role to the permisssions belonging to the given list of permissionnames
     *
     * @param userKey         the the userKey of the user performing the action
     * @param roleName        the name of the role whose PERMISSIONS needs to be changed
     * @param permissionNames the names of the permissions the role needs to get
     * @param forumId
     */
    public RoleAO setPermissions(String userKey, String roleName, List<String> permissionNames, int forumId);

    /**
     * Initializes the permissions in the system using PermissionConfig
     */
    public void initPermissions();

    /**
     * Makes the user belonging to the given userKey admin without checkout autorization (used for making the first admin)
     *
     * @param userKey the key of the user that needs to become admin
     * @param forumId
     */
    public UserAO initAdmin(String userKey, int forumId);

    /**
     * Makes a role for removed users
     * @param forumId the forum for which to make the role
     */
    public void initDeletedUserRole(int forumId);
}