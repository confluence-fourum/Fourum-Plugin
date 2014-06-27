package restapi;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import forummacro.AOTestDatabaseUpdater;
import forummacro.forum.ao.ForumAO;
import forummacro.forum.ao.ForumRepository;
import forummacro.forum.ao.ForumRepositoryImplement;
import forummacro.rightsmanagement.RightsManagement;
import forummacro.rightsmanagement.RightsManagementImplement;
import forummacro.rightsmanagement.ao.*;
import forummacro.rightsmanagement.exceptions.UserNotAuthorizedException;
import net.java.ao.DBParam;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import restapi.Serializables.SerializablePermission;
import restapi.Serializables.SerializableRole;
import restapi.Serializables.SerializableUser;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Nico Smolders
 */
@RunWith(ActiveObjectsJUnitRunner.class)
@Data(AOTestDatabaseUpdater.ServiceTestDatabaseUpdater.class)
public class RightsManagementRestTest {
    private EntityManager entityManager;
    private ActiveObjects ao;

    private MockRightsManagementRestService restService;
    private UserManagementRepository userManagementRepository;
    private RoleManagementRepository roleManagementRepository;
    private PermissionManagementRepository permissionManagementRepository;
    private RightsManagement rightsManagement;
    private ForumRepository forumRepository;

    private ForumAO forumAO;
    private int forumID;

    private UserAO userAO;
    private UserAO userAO2;
    private RoleAO roleAO;
    private RoleAO roleAO2;
    private PermissionAO permissionAO;
    private PermissionAO permissionAO2;
    private RoleToPermissionAO roleToPermissionAO;
    private RoleToPermissionAO roleToPermissionAO2;

    private MockSerializableUser user;
    private SerializableRole rol;
    private SerializablePermission permission;

    @Before
    public void setUp() throws Exception {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        userManagementRepository = new UserManagementRepositoryImplement(ao);
        roleManagementRepository = new RoleManagementRepositoryImplement(ao);
        permissionManagementRepository = new PermissionManagementRepositoryImplement(ao);
        forumRepository = new ForumRepositoryImplement(ao);
        rightsManagement = new RightsManagementImplement(userManagementRepository, roleManagementRepository, permissionManagementRepository, forumRepository);
        restService = new MockRightsManagementRestService(rightsManagement);

        forumAO = ao.create(ForumAO.class, new DBParam("TITLE", "ForumTitle"));
        forumAO.setDescription("ForumDescription");
        forumAO.save();
        forumID = forumAO.getID();

        permissionAO = ao.create(PermissionAO.class, new DBParam("NAME", "Admin"));
        permissionAO.save();
        permissionAO2 = ao.create(PermissionAO.class, new DBParam("NAME", "User"));
        permissionAO2.save();
        roleAO = ao.create(RoleAO.class);
        roleAO.setName("Admin");
        roleAO.setDescription("Admin");
        roleAO.setForum(forumAO);
        roleAO.save();
        roleAO2 = ao.create(RoleAO.class);
        roleAO2.setName("User");
        roleAO2.setDescription("User");
        roleAO2.setForum(forumAO);
        roleAO2.save();
        roleToPermissionAO = ao.create(RoleToPermissionAO.class);
        roleToPermissionAO.setPermission(permissionAO);
        roleToPermissionAO.setRole(roleAO);
        roleToPermissionAO.save();
        roleToPermissionAO2 = ao.create(RoleToPermissionAO.class);
        roleToPermissionAO2.setPermission(permissionAO2);
        roleToPermissionAO2.setRole(roleAO2);
        roleToPermissionAO2.save();

        userAO = ao.create(UserAO.class, new DBParam("USER_KEY", "1"), new DBParam("FORUM_ID", forumID));
        userAO.setRole(roleAO);
        userAO.save();
        userAO2 = ao.create(UserAO.class, new DBParam("USER_KEY", "2"), new DBParam("FORUM_ID", forumID));
        userAO2.setRole(roleAO2);
        userAO2.save();

    }

    @Test
    public void getAllUsers() {
        Response response = restService.allUsers(forumID);
        SerializableUser expected = user.build(userAO, "Nico Smolders");
        System.out.println(response.getEntity().getClass().getSimpleName());
        SerializableUser[] responseSerializable = (SerializableUser[]) response.getEntity();
        assertEquals(expected.getName(), responseSerializable[0].getName());
    }

    @Test
    public void getAllUserRoles() {
        Response response = restService.allRoles(forumID);
        SerializableRole expected = rol.build(roleAO);
        SerializableRole[] responseSerializable = (SerializableRole[]) response.getEntity();
        assertEquals(expected.getName(), responseSerializable[0].getName());
        assertEquals(expected.getClass(), responseSerializable[0].getClass());
    }

    @Test
    public void getSingleRoll() {
        Response response = restService.singleRole(forumID, roleAO.getName());
        SerializableRole expected = rol.build(roleAO);
        SerializableRole responseSerializable = (SerializableRole) response.getEntity();
        assertEquals(expected.getName(), responseSerializable.getName());
        assertEquals(expected.getClass(), responseSerializable.getClass());
    }

    @Test
    public void getAllPermissions() {
        Response response = restService.allPermissions();
        SerializablePermission expected = permission.build(permissionAO);
        SerializablePermission[] responseSerializable = (SerializablePermission[]) response.getEntity();
        assertEquals(expected.getName(), responseSerializable[0].getName());
        assertEquals(expected.getClass(), responseSerializable[0].getClass());
    }

    @Test
    public void getIsAuthorized() {
        Response response = restService.isAuthorized(forumID, "1", "Admin");
        ResponseBuilder responseExpected = Response.ok("succes");
        assertEquals(responseExpected.build().getEntity(), response.getEntity());
    }

    @Test(expected = UserNotAuthorizedException.class)
    public void getIsNotAuthorized() {
        Response response = restService.isAuthorized(forumID, "1", "User");
    }

//	@Test
//	public void addUser() {
//		Response response = restService.addUser(forumID, "2", "User");
//		SerializableUser expected = user.build(userAO2, "User");
//		SerializableUser[] responseSerializable = (SerializableUser[])response.getEntity();
//		assertEquals(expected.getUserKey(), responseSerializable[0].getUserKey());
//		assertEquals(expected.getName(), responseSerializable[0].getName());
//		assertEquals(expected.getRole(), responseSerializable[0].getRole());
//	}
}