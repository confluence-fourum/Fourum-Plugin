package restapi;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import forummacro.AOTestDatabaseUpdater;
import forummacro.forum.ao.ForumAO;
import forummacro.rightsmanagement.ao.RoleAO;
import forummacro.rightsmanagement.ao.UserAO;
import net.java.ao.DBParam;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import restapi.Serializables.SerializableRole;
import restapi.Serializables.SerializableUser;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(AOTestDatabaseUpdater.ServiceTestDatabaseUpdater.class)
public class SerializableUserTest {
    private static final String FORUMTITLE = "TESTFORUM";
    private static final String FORUMDESCRIPTION = "TEST FORUM DESCRIPTION";
    private static final String USERNAME = "RIK HARINK";
    private EntityManager entityManager;
    private ActiveObjects ao;
    private String userKey;
    private UserAO user;
    private RoleAO role;
    private ForumAO forum;
    private int forumID;

    @Before
    public void setUp() throws Exception {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        forum = ao.create(ForumAO.class, new DBParam("TITLE", FORUMTITLE));
        forum.setDescription(FORUMDESCRIPTION);
        forum.save();
        forumID = forum.getID();
        userKey = "JUNIT";
        role = getRole();
        user = getUser(userKey, role);
    }

    private UserAO getUser(String key, RoleAO role) {
        UserAO usr = ao.create(UserAO.class, new DBParam("USER_KEY", key), new DBParam("FORUM_ID", forumID));
        usr.setRole(role);
        usr.save();
        return usr;
    }

    private RoleAO getRole() {
        RoleAO roleTwo = ao.create(RoleAO.class, new DBParam("NAME", "UserTest"), new DBParam("FORUM_ID", forumID));
        roleTwo.setDescription("User Test");
        roleTwo.save();
        return roleTwo;
    }

    private UserAO[] getUserArray(int arrayLength) {
        List<UserAO> userList = new ArrayList<UserAO>();
        for (int i = 0; i < arrayLength; i++) {
            userList.add(getUser("User " + i, role));
        }
        return userList.toArray(new UserAO[]{});
    }

    @Test
    public void testGetUserKey() throws Exception {
        SerializableUser sUser = getSerializableUser();
        assertEquals(sUser.getUserKey(), userKey);
    }

    @Test
    public void testSetUserKey() throws Exception {
        String newUserKey = "GUNIT";
        SerializableUser sUser = getSerializableUser();
        sUser.setUserKey(newUserKey);
        assertEquals(sUser.getUserKey(), newUserKey);
    }

    @Test
    public void testGetRole() throws Exception {
        SerializableUser sUser = getSerializableUser();
        assertEquals(sUser.getRole().getName(), role.getName());
    }

    private SerializableUser getSerializableUser() {
        return MockSerializableUser.build(user, USERNAME);
    }

    @Test
    public void testSetRole() throws Exception {
        String roleName = "JUNIT ADMIN";
        SerializableUser sUser = getSerializableUser();
        RoleAO role = ao.create(RoleAO.class, new DBParam("NAME", roleName), new DBParam("FORUM_ID", forumID));
        SerializableRole sRole = SerializableRole.build(role);
        sUser.setRole(sRole);
        assertEquals(sUser.getRole().getName(), roleName);
    }

    @Test
    public void testBuildSingleUser() throws Exception {
        SerializableUser sUser = getSerializableUser();
        assertEquals(sUser.getRole().getName(), role.getName());
    }

    @Test
    public void testBuildUserArrayLength() throws Exception {
        int length = 10;
        SerializableUser[] userArray = MockSerializableUser.build(getUserArray(length), USERNAME);
        assertEquals(userArray.length, length);
    }

    @Test
    public void testBuildUserArray() throws Exception {
        int length = 10;
        UserAO[] userAOArray = getUserArray(length);
        SerializableUser[] userArray1 = MockSerializableUser.build(userAOArray, USERNAME);
        SerializableUser[] userArray2 = MockSerializableUser.build(userAOArray, USERNAME);
        for (int i = 0; i < length; i++) {
            SerializableUser user1 = userArray1[i];
            SerializableUser user2 = userArray2[i];
            assertEquals(user1.getUserKey(), user2.getUserKey());
            assertEquals(user1.getRole().getName(), user2.getRole().getName());
            assertEquals(user1.getRole().getDescription(), user2.getRole().getDescription());
        }
    }
}
