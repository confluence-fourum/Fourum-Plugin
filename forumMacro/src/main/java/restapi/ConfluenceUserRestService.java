package restapi;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserKey;
import forummacro.rightsmanagement.RightsManagement;
import restapi.Serializables.SerializableConfluenceUser;
import restapi.Serializables.SerializableProfile;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rik on 10/06/2014.
 */

@Path("/confluenceUser")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class ConfluenceUserRestService {
    private UserAccessor userAccessor;
    private RightsManagement rightsManagement;
    private SettingsManager settingsManager;

    public ConfluenceUserRestService(UserAccessor userAccessor, RightsManagement rightsManagement, SettingsManager settingsManager) {
        this.userAccessor = userAccessor;
        this.rightsManagement = rightsManagement;
        this.settingsManager = settingsManager;
    }

    @GET
    @Path("{forumID}/getProfileInfo/{userKey}")
    public Response getUserProfileInfo(@PathParam("userKey") String userKey,
                                       @PathParam("forumID") int forumID) {
        ConfluenceUser user = userAccessor.getExistingUserByKey(new UserKey(userKey));
        String name = user.getFullName();
        String role = rightsManagement.getUser(userKey, forumID).getRole().getName();
        String profilePicture = settingsManager.getGlobalSettings().getBaseUrl() + userAccessor.getUserProfilePicture(user).getDownloadPath();
        return Response.ok(SerializableProfile.build(name, role, profilePicture)).build();
    }

    /**
     * Returns all existing ConfluenceUsers
     *
     * @return a Response containing information about all Confluence Users
     */
    @GET
    @Path("confluenceusers")
    public Response confluenceUsers() {
        List<String> confUsernames = userAccessor.getUserNamesWithConfluenceAccess();
        List<ConfluenceUser> users = new ArrayList<ConfluenceUser>();
        for (String username : confUsernames) {
            users.add(userAccessor.getUserByName(username));
        }
        return Response.ok(SerializableConfluenceUser.build(users)).build();
    }
}
