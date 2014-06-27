package restapi;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import forummacro.rightsmanagement.RightsManagement;
import restapi.Serializables.SerializablePermission;
import restapi.Serializables.SerializableRole;
import restapi.Serializables.SerializableUserImplement;
import restapi.exceptions.EmptyInputException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Description for the class ForumThreadRestService:
 * Provides a REST API to the ActiveObjects-layer using JAX-RS. We need JSON/XML Serializable entities, so
 * all data is converted from/to AO <-> JSON/XML.
 *
 * @author Rik Harink - R.J.Harink@student.han.nl
 *         Adapted from:
 *         author mdkr
 * @version Copyright (c) 2014 HAN University, All rights reserved.
 */


@Path("/RightsManagement")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class RightsManagementRestService {
    private RightsManagement rightsManagement;
    private Sanitizer sanitizer;

    /**
     * The rightsManagement RestService provides REST api access to the backend of our rightsmanagementsystem for the forum
     *
     * @param rightsManagement the rightsManagement service to use for checking rights and managing users,roles and permissions
     */
    public RightsManagementRestService(RightsManagement rightsManagement, Sanitizer sanitizer) {
        this.rightsManagement = rightsManagement;
        this.sanitizer = sanitizer;
    }


    private String getUserKey() {
        return AuthenticatedUserThreadLocal.get().getKey().getStringValue();
    }

    /**
     * Gets all forum users for a given forum
     *
     * @param forumId the forum you want to get the users from
     * @return the users of the forum
     */
    @GET
    @Path("{forumid}/users")
    public Response allUsers(@PathParam("forumid") int forumId) {
        return Response.ok(SerializableUserImplement.build(rightsManagement.getUser(forumId))).build();
    }

    /**
     * Gets all the roles in the given forum
     *
     * @param forumId the forum you want to get the roles from
     * @return the roles of the forum
     */
    @GET
    @Path("{forumid}/roles")
    public Response allRoles(@PathParam("forumid") int forumId) {
        return Response.ok(SerializableRole.build(rightsManagement.getRole(forumId))).build();
    }

    /**
     * Returns the roleinformation of the given rolename in the given forum
     *
     * @param forumId  the forum you want to get the role from
     * @param roleName the role you want to get
     * @return the role of the given name in the given forum
     */
    @GET
    @Path("{forumid}/role/{rolename}")
    public Response singleRole(@PathParam("forumid") int forumId, @PathParam("rolename") String roleName) {
        return Response.ok(SerializableRole.build(rightsManagement.getRole(roleName, forumId))).build();
    }

    /**
     * Returns all permissions of our system
     *
     * @return the permissions
     */
    @GET
    @Path("permissions")
    public Response allPermissions() {
        return Response.ok(SerializablePermission.build(rightsManagement.getPermissions())).build();
    }

    /**
     * Returns if the current user is authorized for a given permission
     *
     * @param forumId    the forum you want to check permissions of
     * @param permission the permission you want to check
     * @return ok if the user is authorized, otherwise isAuthorized will throw an Unauthorized exception if not
     */
    @GET
    @Path("{forumid}/isAuthorized/{permission}")
    public Response isAuthorized(@PathParam("forumid") int forumId, @PathParam("permission") String permission) {
        String key = getUserKey();
        try {
            rightsManagement.isAuthorized(key, permission, forumId);
        }
        catch (WebApplicationException e){
            return Response.ok(false).build();
        }
        return Response.ok(true).build();
    }


    /**
     * Add a ConfluenceUser to the forum
     *
     * @param forumId   the forum you want to add the user for
     * @param newUserId the UserKey of the user you want to add
     * @param rolename  the role the user needs to get
     * @return the newley constructed user
     */
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Path("{forumid}/addUser")
    public Response addUser(@PathParam("forumid") int forumId,
                            @FormParam(value = "newUserId") String newUserId,
                            @FormParam(value = "rolename") String rolename) {
        String userId = getUserKey();
        String safeNewUserId = sanitizer.sanitizeInput(newUserId);
        String safeRoleName = sanitizer.sanitizeInput(rolename);
        return Response.ok(SerializableUserImplement.build(rightsManagement.addUser(userId, safeNewUserId, safeRoleName, forumId))).build();
    }

    /**
     * Create a new role in the forum
     *
     * @param forumId         the forum you want to create the role for
     * @param roleName        the rolename of your new role
     * @param roleDescription the description of the role
     * @return
     */
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Path("{forumid}/createRole")
    public Response createRole(@PathParam("forumid") int forumId,
                               @FormParam(value = "roleName") String roleName,
                               @FormParam(value = "roleDescription") String roleDescription) {
        String safeRoleName = sanitizer.sanitizeInput(roleName);
        String safeRoleDescription = sanitizer.sanitizeInput(roleDescription);
        checkEmptyInput(safeRoleName);
        checkEmptyInput(safeRoleDescription);
        return Response.ok(SerializableRole.build(rightsManagement.createRole(getUserKey(), safeRoleName, safeRoleDescription, forumId))).build();
    }

    private void checkEmptyInput(String input) {
        if (input.isEmpty()){
            throw new EmptyInputException("Your input is empty");
        }
    }

    /**
     * Remove a user from the forum
     *
     * @param forumId the id of the forum you want to remove a user from
     * @param userKey the userkey of the user you want to delete
     * @return
     */
    @DELETE
    @Path("{forumid}/deleteUser/{userkey}")
    public Response deleteUser(@PathParam("forumid") int forumId,
                               @PathParam("userkey") String userKey) {
        String safeUserKey = sanitizer.sanitizeInput(userKey);
        checkEmptyInput(safeUserKey);
        rightsManagement.deleteUser(getUserKey(), safeUserKey, forumId);
        return Response.ok().build();
    }

    /**
     * Remove a role from the forum
     *
     * @param forumId  the forum you want to remove a role from
     * @param roleName the name of the role you want to delete
     * @return
     */
    @DELETE
    @Path("{forumid}/deleteRole/{rolename}")
    public Response deleteRole(@PathParam("forumid") int forumId,
                               @PathParam("rolename") String roleName) {
        String safeRoleName = sanitizer.sanitizeInput(roleName);
        checkEmptyInput(safeRoleName);
        rightsManagement.deleteRole(getUserKey(), safeRoleName, forumId);
        return Response.ok().build();
    }

    /**
     * Set a new role name of an existing role
     *
     * @param forumId     the forum you want to change a role name in
     * @param roleName    the name of the role you want to change
     * @param newRoleName the new name
     * @return the edited role
     */
    @PUT
    @Path("{forumId}/setRoleName/{roleName}/{newRoleName}")
    public Response setRoleName(@PathParam("forumId") int forumId,
                                @PathParam("roleName") String roleName,
                                @PathParam("newRoleName") String newRoleName) {
        String safeRoleName = sanitizer.sanitizeInput(roleName);
        String safeNewRoleName = sanitizer.sanitizeInput(newRoleName);
        checkEmptyInput(safeRoleName);
        checkEmptyInput(safeNewRoleName);
        return Response.ok(SerializableRole.build(rightsManagement.setRoleName(getUserKey(), safeRoleName, safeNewRoleName, forumId))).build();
    }

    /**
     * @param forumId         Set a new description for an existing role
     * @param roleName        the name of the role you want to change
     * @param roleDescription the new role description
     * @return the edited role
     */
    @PUT
    @Path("{forumId}/setRoleDescription/{roleName}/{roleDescription}")
    public Response setRoleDescription(@PathParam("forumId") int forumId,
                                       @PathParam("roleName") String roleName,
                                       @PathParam("roleDescription") String roleDescription) {
        String safeRoleName = sanitizer.sanitizeInput(roleName);
        String safeRoleDescription = sanitizer.sanitizeInput(roleDescription);
        checkEmptyInput(safeRoleName);
        checkEmptyInput(safeRoleDescription);
        return Response.ok(SerializableRole.build(rightsManagement.setRoleDescription(getUserKey(), safeRoleName, safeRoleDescription, forumId))).build();
    }

    /**
     * Set the role of a user in a forum
     *
     * @param forumId  the forum in which you want to change a users role
     * @param userId   the user you want to change the role of
     * @param rolename the name of the role you want to give the user
     * @return the changed user
     */
    @PUT
    @Path("{forumid}/setUserRole/{userid}/{userRole}")
    public Response setUserRole(@PathParam("forumid") int forumId,
                                @PathParam("userid") String userId,
                                @PathParam("userRole") String rolename) {
        String safeUserId = sanitizer.sanitizeInput(userId);
        String safeRolename = sanitizer.sanitizeInput(rolename);
        checkEmptyInput(safeUserId);
        checkEmptyInput(safeRolename);
        return Response.ok(SerializableUserImplement.build(rightsManagement.changeUserRole(getUserKey(), safeUserId, safeRolename, forumId))).build();
    }


    /**
     * Set the permissions of a role in a forum
     *
     * @param forumId     the forum in which you want to set permissions to a role
     * @param roleName    the role you want to set the permissions of
     * @param permissions the permissions you want to give the role
     * @return the new role
     */
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
    @Path("{forumId}/setPermissions")
    public Response setPermissions(@PathParam("forumId") int forumId,
                                   @QueryParam("roleName") String roleName,
                                   @QueryParam("permissions") String permissions) {
        List<String> safePermissions = sanitizer.parseJson(permissions);
        String safeRoleName = sanitizer.sanitizeInput(roleName);
        checkEmptyInput(safeRoleName);
        return Response.ok(SerializableRole.build(rightsManagement.setPermissions(getUserKey(), safeRoleName, safePermissions, forumId))).build();
    }
}
