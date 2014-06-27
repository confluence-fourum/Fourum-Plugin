package forummacro.rightsmanagement.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import forummacro.AOTestDatabaseUpdater;
import forummacro.forum.ao.ForumAO;
import forummacro.forum.ao.ForumRepository;
import forummacro.forum.ao.ForumRepositoryImplement;
import forummacro.rightsmanagement.PermissionConfig;
import forummacro.rightsmanagement.RightsManagement;
import forummacro.rightsmanagement.RightsManagementImplement;
import forummacro.rightsmanagement.exceptions.*;
import net.java.ao.DBParam;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Joost Elshof on 16-5-14.
 */

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(AOTestDatabaseUpdater.ServiceTestDatabaseUpdater.class)
public class RightsManagementTest {
    private static final String ADMINUSERKEY = "1337";
    private static final String NEWUSERKEY = "512";
    private static final String NONEXISTINGUSERKEY = "1001";
    private static final String EXISTINGPERMISSION = PermissionConfig.PERMISSIONS.get("addUser");
    private static final String NONEXISTINGPERMISSION = "fakePermission";
    private static final String NONEXISTINGROLENAME = "fakeRoleName";
    private static final String NEWROLENAME = "NewRole";
    private static final String NEWROLEDESCRIPTION = "This is an Role for testing";
    private static final String FORUMTITLE = "TEST FORUM";
    private static final String FORUMDESCRIPTION = "TEST FORUM DESC";
    private static final int WRONGFORUMID = -1;
    private static int FORUMID;
    private static int FORUMID2;
    public EntityManager entityManager;
    private ActiveObjects ao;
    private UserManagementRepository userManagementRepository;
    private RoleManagementRepository roleManagementRepository;
    private PermissionManagementRepository permissionManagementRepository;
    private ForumRepository forumRepository;
    private RightsManagement rightsManagement;
    private ForumAO forum;
    private ForumAO forum2;

    @Before
    public void setUp() throws Exception {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        forum = ao.create(ForumAO.class, new DBParam("TITLE", FORUMTITLE));
        forum.setDescription(FORUMDESCRIPTION);
        forum.save();

        forum2 = ao.create(ForumAO.class, new DBParam("TITLE", FORUMTITLE));
        forum2.setDescription(FORUMDESCRIPTION);
        forum2.save();
        FORUMID = forum.getID();
        FORUMID2 = forum2.getID();

        userManagementRepository = new UserManagementRepositoryImplement(ao);
        roleManagementRepository = new RoleManagementRepositoryImplement(ao);
        permissionManagementRepository = new PermissionManagementRepositoryImplement(ao);
        forumRepository = new ForumRepositoryImplement(ao);
        rightsManagement = new RightsManagementImplement(userManagementRepository, roleManagementRepository, permissionManagementRepository, forumRepository);
        rightsManagement.initPermissions();
        rightsManagement.initAdmin(ADMINUSERKEY, FORUMID);
        rightsManagement.initAdmin(ADMINUSERKEY, FORUMID2);
    }

    private void setupAnExtraUserWithNewRole(String newRoleNAme, String newRoleDescription, String newUserKey) {
        setupNewRole(newRoleNAme, newRoleDescription);
        setupNewUser(newUserKey, newRoleNAme);
    }

    private void setupNewRole(String newRoleNAme, String newRoleDescription) {
        RoleAO role = rightsManagement.createRole(ADMINUSERKEY, newRoleNAme, newRoleDescription, FORUMID);
    }

    private void setupNewUser(String newUserKey, String roleName) {
        UserAO user = rightsManagement.addUser(ADMINUSERKEY, newUserKey, roleName, FORUMID);
    }

    @Test
    public void permissionsAreInitialized() {
        assertEquals(PermissionConfig.getPermissions().size(), rightsManagement.getPermissions().length);
    }

    @Test
    public void getForumReturnsRightForum() {
        ForumAO forum = forumRepository.getForum(FORUMID);
        assertEquals(FORUMID, forum.getID());
        assertEquals(FORUMDESCRIPTION, forum.getDescription());
        assertEquals(FORUMTITLE, forum.getTitle());
    }

    @Test(expected = ForumNotFoundException.class)
    public void getForumWithWrongIDThrowsException() {
        forumRepository.getForum(WRONGFORUMID);
    }

    @Test
    public void userIsAuthorizedTest() {
        rightsManagement.isAuthorized(ADMINUSERKEY, EXISTINGPERMISSION, FORUMID);
    }

    @Test(expected = UserNotAuthorizedException.class)
    public void userIsNotAuthorizedTest() {
        setupAnExtraUserWithNewRole(NEWROLENAME, NEWROLEDESCRIPTION, NEWUSERKEY);
        rightsManagement.isAuthorized(NEWUSERKEY, rightsManagement.getPermissions()[0].getName(), FORUMID);
    }

    @Test(expected = PermissionNotFoundException.class)
    public void permissionNotFoundIsAuthorizedTest() {
        rightsManagement.isAuthorized(ADMINUSERKEY, NONEXISTINGPERMISSION, FORUMID);
    }

    @Test(expected = UserNotAuthorizedException.class)
    public void userNotFoundIsAuthorizedTest() {
        rightsManagement.isAuthorized(NONEXISTINGUSERKEY, NONEXISTINGPERMISSION, FORUMID);
    }

    @Test
    public void getAllUsersTest1() {
        UserAO[] userAOs = new UserAO[1];
        assertEquals(userAOs.getClass(), rightsManagement.getUser(FORUMID).getClass());
    }

    @Test
    public void getAllUsersTest2() {
        assertEquals(1, rightsManagement.getUser(FORUMID).length);
    }

    @Test
    public void getSearchedUserTest() {
        assertEquals(ADMINUSERKEY, rightsManagement.getUser(ADMINUSERKEY, FORUMID).getUserKey());
    }

    @Test(expected = UserNotFoundException.class)
    public void userNotFoundGetSearchedUserTest() {
        rightsManagement.getUser(NONEXISTINGUSERKEY, FORUMID).getUserKey();
    }

    @Test
    public void getSearchedUsersTest() {
        List<String> userKeys = new ArrayList<String>();
        userKeys.add(ADMINUSERKEY);
        assertEquals(ADMINUSERKEY, rightsManagement.getUser(userKeys, FORUMID)[0].getUserKey());
    }

    @Test(expected = UserNotFoundException.class)
    public void userNotFoundGetSearchedUsersTest() {
        List<String> userKeys = new ArrayList<String>();
        userKeys.add(NONEXISTINGUSERKEY);
        assertEquals(NONEXISTINGUSERKEY, rightsManagement.getUser(userKeys, FORUMID)[0].getUserKey());
    }

    @Test
    public void addUserTest() {
        assertEquals(NEWUSERKEY, rightsManagement.addUser(ADMINUSERKEY, NEWUSERKEY, rightsManagement.getRole(FORUMID)[0].getName(), FORUMID).getUserKey());
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void userAlreadyExistsAddUserTest() {
        rightsManagement.addUser(ADMINUSERKEY, ADMINUSERKEY, rightsManagement.getRole(FORUMID)[0].getName(), FORUMID).getUserKey();
    }

    @Test
    public void changeUserRoleTest() {
        setupAnExtraUserWithNewRole(NEWROLENAME, NEWROLEDESCRIPTION, NEWUSERKEY);
        rightsManagement.changeUserRole(ADMINUSERKEY, NEWUSERKEY, PermissionConfig.ADMIN, FORUMID);
        assertEquals(PermissionConfig.ADMIN, rightsManagement.getUser(NEWUSERKEY, FORUMID).getRole().getName());
    }

    @Test(expected = UserNotFoundException.class)
    public void userNotFoundChangeUserRoleTest() {
        rightsManagement.changeUserRole(ADMINUSERKEY, NEWUSERKEY, PermissionConfig.ADMIN, FORUMID);
    }

    @Test(expected = RoleNotFoundException.class)
    public void roleNotFoundChangeUserRoleTest() {
        rightsManagement.changeUserRole(ADMINUSERKEY, ADMINUSERKEY, NEWROLENAME, FORUMID);
    }

    @Test
    public void deleteUserTest() {
        setupNewUser(NEWUSERKEY, PermissionConfig.ADMIN);
        assertEquals(NEWUSERKEY, rightsManagement.getUser(NEWUSERKEY, FORUMID).getUserKey());
        rightsManagement.deleteUser(ADMINUSERKEY, NEWUSERKEY, FORUMID);
        try {
            rightsManagement.getUser(NEWUSERKEY, FORUMID);
            fail();
        } catch (UserNotFoundException e) {
            System.out.println(e);
        }
    }

    @Test(expected = UserNotFoundException.class)
    public void userNotFoundDeleteUserTest() {
        rightsManagement.deleteUser(ADMINUSERKEY, NONEXISTINGUSERKEY, FORUMID);
    }

    @Test
    public void getAllRolesTest1() {
        RoleAO[] roleAO = new RoleAO[1];
        assertEquals(roleAO.getClass(), rightsManagement.getRole(FORUMID).getClass());
    }

    @Test
    public void getAllRolesTest2() {
        assertEquals(1, rightsManagement.getRole(FORUMID).length);
    }

    @Test
    public void getSearchedRoleTest() {
        assertEquals(PermissionConfig.ADMIN, rightsManagement.getRole(PermissionConfig.ADMIN, FORUMID).getName());
    }

    @Test(expected = RoleNotFoundException.class)
    public void roleNotFoundGetSearchedRoleTest() {
        rightsManagement.getRole(NONEXISTINGROLENAME, FORUMID);
    }

    @Test
    public void createRoleTest() {
        RoleAO roleAO = rightsManagement.createRole(ADMINUSERKEY, NEWROLENAME, NEWROLEDESCRIPTION, FORUMID);
        assertEquals(NEWROLENAME, roleAO.getName());
    }

    @Test(expected = RoleAlreadyExistsException.class)
    public void roleAlreadyExistsCreateRoleTest() {
        rightsManagement.createRole(ADMINUSERKEY, PermissionConfig.ADMIN, PermissionConfig.ADMINDESCRIPTION, FORUMID);
    }

    @Test
    public void setRoleNameTest() {
        RoleAO roleAO = rightsManagement.setRoleName(ADMINUSERKEY, PermissionConfig.ADMIN, NEWROLENAME, FORUMID);
        assertEquals(NEWROLENAME, roleAO.getName());
    }

    @Test(expected = RoleNotFoundException.class)
    public void roleNotFoundSetRoleNameTest() {
        rightsManagement.setRoleName(ADMINUSERKEY, NONEXISTINGROLENAME, NEWROLENAME, FORUMID);
    }

    @Test(expected = RoleAlreadyExistsException.class)
    public void roleAlreadyExistsSetRoleNameTest() {
        rightsManagement.setRoleName(ADMINUSERKEY, PermissionConfig.ADMIN, PermissionConfig.ADMIN, FORUMID);
    }

    @Test
    public void setRoleDescriptionTest() {
        RoleAO roleAO = rightsManagement.setRoleDescription(ADMINUSERKEY, PermissionConfig.ADMIN, NEWROLEDESCRIPTION, FORUMID);
        assertEquals(NEWROLEDESCRIPTION, roleAO.getDescription());
    }

    @Test(expected = RoleNotFoundException.class)
    public void roleNotFoundSetRoleDescriptionTest() {
        rightsManagement.setRoleDescription(ADMINUSERKEY, NONEXISTINGROLENAME, NEWROLEDESCRIPTION, FORUMID);
    }

    @Test
    public void deleteRoleTest() {
        setupNewRole(NEWROLENAME, NEWROLEDESCRIPTION);
        assertEquals(NEWROLENAME, rightsManagement.getRole(NEWROLENAME, FORUMID).getName());
        rightsManagement.deleteRole(ADMINUSERKEY, NEWROLENAME, FORUMID);
        try {
            rightsManagement.getRole(NEWROLENAME, FORUMID);
            fail();
        } catch (RoleNotFoundException e) {
            System.out.println(e);
        }
    }

    @Test(expected = UserNotAuthorizedException.class)
    public void userNotAuthorizedDeleteRoleTest() {
        throw new UserNotAuthorizedException();
    }

    @Test(expected = RoleNotFoundException.class)
    public void roleNotFoundDeleteRoleTest() {
        rightsManagement.deleteRole(ADMINUSERKEY, NONEXISTINGROLENAME, FORUMID);
    }

    @Test(expected = RoleNotEmptyException.class)
    public void roleNotEmptyDeleteRoleTest() {
        rightsManagement.deleteRole(ADMINUSERKEY, PermissionConfig.ADMIN, FORUMID);
    }

    @Test
    public void getAllPermissionsTest1() {
        PermissionAO[] permissionAO = new PermissionAO[1];
        assertEquals(permissionAO.getClass(), rightsManagement.getPermissions().getClass());
    }

    @Test
    public void getAllPermissionsTest2() {
        assertEquals(PermissionConfig.getPermissions().size(), rightsManagement.getPermissions().length);
    }

    @Test
    public void setPermissionsTest() {
        List<String> permissions = new ArrayList<String>();
        permissions.add(PermissionConfig.PERMISSIONS.get("read"));
        rightsManagement.setPermissions(ADMINUSERKEY, PermissionConfig.ADMIN, permissions, FORUMID);
        assertEquals(1, rightsManagement.getPermissions(PermissionConfig.ADMIN, FORUMID).length);
    }

    @Test(expected = RoleNotFoundException.class)
    public void roleNotFoundSetPermissionsTest() {
        List<String> permissions = new ArrayList<String>();
        rightsManagement.setPermissions(ADMINUSERKEY, NONEXISTINGROLENAME, permissions, FORUMID);
    }
}
