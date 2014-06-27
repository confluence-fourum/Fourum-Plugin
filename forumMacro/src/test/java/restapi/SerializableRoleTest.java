package restapi;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import forummacro.AOTestDatabaseUpdater;
import forummacro.forum.ao.ForumAO;
import forummacro.rightsmanagement.ao.RoleAO;
import net.java.ao.DBParam;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import restapi.Serializables.SerializableRole;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(AOTestDatabaseUpdater.ServiceTestDatabaseUpdater.class)
public class SerializableRoleTest {
    private static final String FORUMTITLE = "FORUM TITLE";
    private static final String FORUMDESCRIPTION = "FORUM DESC";
    private static final String ROLENAME = "JUNIT ADMIN";
    private static final String ROLEDESCRIPTION = "JUNIT TEST ROLE";
    private EntityManager entityManager;
    private ActiveObjects ao;
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
        role = ao.create(RoleAO.class, new DBParam("NAME", ROLENAME), new DBParam("FORUM_ID", forumID));
        role.setDescription(ROLEDESCRIPTION);
        role.save();
    }

    private RoleAO getRole(String name, String description) {
        RoleAO role = ao.create(RoleAO.class, new DBParam("NAME", name), new DBParam("FORUM_ID", forumID));
        role.setDescription(description);
        role.save();
        return role;
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(SerializableRole.build(role).getName(), ROLENAME);
    }

    @Test
    public void testSetName() throws Exception {
        String newName = "Luminis";
        SerializableRole sRole = SerializableRole.build(role);
        sRole.setName(newName);
        assertEquals(sRole.getName(), newName);
    }

    @Test
    public void testGetDescription() throws Exception {
        SerializableRole sRole = SerializableRole.build(role);
        assertEquals(sRole.getDescription(), ROLEDESCRIPTION);
    }

    @Test
    public void testSetDescription() throws Exception {
        String newDescription = "Description Test";
        SerializableRole sRole = SerializableRole.build(role);
        sRole.setDescription(newDescription);
        assertEquals(sRole.getDescription(), newDescription);
    }

    @Test
    public void testBuildingOneSerializableRole() throws Exception {
        SerializableRole sRole = SerializableRole.build(role);
        assertEquals(sRole.getName(), ROLENAME);
        assertEquals(sRole.getDescription(), ROLEDESCRIPTION);
    }

    @Test
    public void testBuildingSerializableRoleArrayLength() throws Exception {
        int arrayLength = 10;
        RoleAO[] roleArray = getRoleArray(arrayLength);
        SerializableRole[] sRoleArray = SerializableRole.build(roleArray);
        assertEquals(sRoleArray.length, arrayLength);
    }

    @Test
    public void testBuildingSerializableRoleArrayConsistency() {
        int arrayLength = 10;
        RoleAO[] roleArray = getRoleArray(arrayLength);
        SerializableRole[] sRoleArray1 = SerializableRole.build(roleArray);
        SerializableRole[] sRoleArray2 = SerializableRole.build(roleArray);
        for (int i = 0; i < arrayLength; i++) {
            SerializableRole role1 = sRoleArray1[i];
            SerializableRole role2 = sRoleArray2[i];
            assertEquals(role1.getName(), role2.getName());
            assertEquals(role1.getDescription(), role2.getDescription());
        }
    }

    private RoleAO[] getRoleArray(int arrayLength) {
        List<RoleAO> roleList = new ArrayList<RoleAO>();
        for (int i = 0; i < arrayLength; i++) {
            roleList.add(getRole("Role " + i, "Description" + i));
        }
        return roleList.toArray(new RoleAO[]{});
    }
}