package restapi;

        import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
        import com.atlassian.confluence.user.ConfluenceUser;
        import com.atlassian.confluence.user.UserAccessor;
        import com.google.gson.JsonArray;
        import com.google.gson.JsonElement;
        import com.google.gson.JsonParser;
        import forummacro.rightsmanagement.RightsManagement;
        import org.jsoup.Jsoup;
        import org.jsoup.safety.Whitelist;
        import restapi.Serializables.SerializableConfluenceUser;
        import restapi.Serializables.SerializablePermission;
        import restapi.Serializables.SerializableRole;

        import javax.ws.rs.*;
        import javax.ws.rs.core.MediaType;
        import javax.ws.rs.core.Response;
        import java.util.ArrayList;
        import java.util.List;

        import static com.google.common.collect.Lists.newArrayList;

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
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class MockRightsManagementRestService {
    private RightsManagement rightsManagement;
    private UserAccessor userAccessor;

    public MockRightsManagementRestService(RightsManagement rightsManagement) {
        this.rightsManagement = rightsManagement;
    }

    public String sanitizeInput(String unsafe) {
        return Jsoup.clean(unsafe, Whitelist.none());
    }

    public List<String> sanitizeInput(List<String> permissions) {
        List<String> safePermissions = new ArrayList<String>();
        for (String permission : permissions) {
            safePermissions.add(sanitizeInput(permission));
        }
        return safePermissions;
    }

    private String getUserKey() {
        return AuthenticatedUserThreadLocal.get().getKey().getStringValue();
    }

    @GET
    @Path("confluenceusers")
    public Response confluenceUsers() {
        List<String> confUsernames = userAccessor.getUserNamesWithConfluenceAccess();
        List<ConfluenceUser> users = new ArrayList<ConfluenceUser>();
        for (String username : confUsernames) {
            System.out.println(username);
            users.add(userAccessor.getUserByName(username));
            System.out.println(userAccessor.getUserByName(username));
        }
        return Response.ok(SerializableConfluenceUser.build(users)).build();
    }

    @GET
    @Path("{forumid}/users")
    public Response allUsers(@PathParam("forumid") int forumId) {
        return Response.ok(MockSerializableUser.build(rightsManagement.getUser(forumId), "Nico Smolders")).build();
    }

    @GET
    @Path("{forumid}/roles")
    public Response allRoles(@PathParam("forumid") int forumId) {
        return Response.ok(SerializableRole.build(rightsManagement.getRole(forumId))).build();
    }

    @GET
    @Path("{forumid}/role/{rolename}")
    public Response singleRole(@PathParam("forumid") int forumId, @PathParam("rolename") String roleName) {
        return Response.ok(SerializableRole.build(rightsManagement.getRole(roleName, forumId))).build();
    }

    @GET
    @Path("permissions")
    public Response allPermissions() {
        return Response.ok(SerializablePermission.build(rightsManagement.getPermissions())).build();
    }

    @GET
    @Path("{forumid}/isAuthorized/{key}/{permission}")
    public Response isAuthorized(@PathParam("forumid") int forumId, @PathParam("key") String key, @PathParam("permission") String permission) {
        rightsManagement.isAuthorized(key, permission, forumId);
        return Response.ok("succes").build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Path("{forumid}/addUser")
    public Response addUser(@PathParam("forumid") int forumId,
                            @FormParam(value = "newUserId") String newUserId,
                            @FormParam(value = "rolename") String rolename) {
        String userId = getUserKey();
        String safeNewUserId = sanitizeInput(newUserId);
        String safeRoleName = sanitizeInput(rolename);
        return Response.ok(MockSerializableUser.build(rightsManagement.addUser(userId, safeNewUserId, safeRoleName, forumId), "test")).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Path("{forumid}/createRole")
    public Response createRole(@PathParam("forumid") int forumId,
                               @FormParam(value = "roleName") String roleName,
                               @FormParam(value = "roleDescription") String roleDescription) {
        String safeRoleName = sanitizeInput(roleName);
        String safeRoleDescription = sanitizeInput(roleDescription);
        return Response.ok(SerializableRole.build(rightsManagement.createRole(getUserKey(), safeRoleName, safeRoleDescription, forumId))).build();
    }

    @DELETE
    @Path("{forumid}/deleteUser/{userkey}")
    public Response deleteUser(@PathParam("forumid") int forumId,
                               @PathParam("userkey") String userKey) {
        String safeUserKey = sanitizeInput(userKey);
        rightsManagement.deleteUser(getUserKey(), safeUserKey, forumId);
        return Response.ok().build();
    }

    @DELETE
    @Path("{forumid}/deleteRole/{rolename}")
    public Response deleteRole(@PathParam("forumid") int forumId,
                               @PathParam("rolename") String roleName) {
        String safeRoleName = sanitizeInput(roleName);
        rightsManagement.deleteRole(getUserKey(), safeRoleName, forumId);
        return Response.ok().build();
    }

    @PUT
    @Path("{forumId}/setRoleName/{roleName}/{newRoleName}")
    public Response setRoleName(@PathParam("forumId") int forumId,
                                @PathParam("roleName") String roleName,
                                @PathParam("newRoleName") String newRoleName) {
        String safeRoleName = sanitizeInput(roleName);
        String safeNewRoleName = sanitizeInput(newRoleName);
        return Response.ok(SerializableRole.build(rightsManagement.setRoleName(getUserKey(), safeRoleName, safeNewRoleName, forumId))).build();
    }

    @PUT
    @Path("{forumId}/setRoleDescription/{roleName}/{roleDescription}")
    public Response setRoleDescription(@PathParam("forumId") int forumId,
                                       @PathParam("roleName") String roleName,
                                       @PathParam("roleDescription") String roleDescription) {
        String safeRoleName = sanitizeInput(roleName);
        String safeRoleDescription = sanitizeInput(roleDescription);
        return Response.ok(SerializableRole.build(rightsManagement.setRoleDescription(getUserKey(), safeRoleName, safeRoleDescription, forumId))).build();
    }

    @PUT
    @Path("{forumid}/setUserRole/{userid}/{userRole}")
    public Response setUserRole(@PathParam("forumid") int forumId,
                                @PathParam("userid") String userId,
                                @PathParam("userRole") String rolename) {
        String safeUserId = sanitizeInput(userId);
        String safeRolename = sanitizeInput(rolename);
        return Response.ok(MockSerializableUser.build(rightsManagement.changeUserRole(getUserKey(), safeUserId, safeRolename, forumId), "test")).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
    @Path("{forumId}/setPermissions")
    public Response setPermissions(@PathParam("forumId") int forumId,
                                   @QueryParam("roleName") String roleName,
                                   @QueryParam("permissions") String permissions) {
        JsonParser parser = new JsonParser();
        JsonArray json = parser.parse(permissions).getAsJsonArray();
        List<String> permissionStrings = new ArrayList<String>();
        for (JsonElement element : newArrayList(json.iterator())) {
            permissionStrings.add(element.getAsString());
        }
        String safeRoleName = sanitizeInput(roleName);
        List<String> safePermissions = sanitizeInput(permissionStrings);
        return Response.ok(SerializableRole.build(rightsManagement.setPermissions(getUserKey(), safeRoleName, safePermissions, forumId))).build();
    }
}