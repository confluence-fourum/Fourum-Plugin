package restapi;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import forummacro.AOTestDatabaseUpdater;
import forummacro.rightsmanagement.ao.PermissionAO;
import net.java.ao.DBParam;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import restapi.Serializables.SerializablePermission;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(AOTestDatabaseUpdater.ServiceTestDatabaseUpdater.class)
public class SerializablePermissionTest {
    private static final String PERMISSIONNAME = "JUNITPERMISSION";
    private EntityManager entityManager;
    private ActiveObjects ao;
    private PermissionAO permission;
    private SerializablePermission sPerm;

    private PermissionAO getPermission(String name) {
        return ao.create(PermissionAO.class, new DBParam("NAME", name));
    }

    private PermissionAO[] getPermissionArray(int arrayLength) {
        List<PermissionAO> permissionList = new ArrayList<PermissionAO>();
        for (int i = 0; i < arrayLength; i++) {
            permissionList.add(getPermission("Permission" + i));
        }
        return permissionList.toArray(new PermissionAO[]{});
    }

    @Before
    public void setUp() throws Exception {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        permission = ao.create(PermissionAO.class, new DBParam("NAME", PERMISSIONNAME));
        sPerm = SerializablePermission.build(permission);
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(sPerm.getName(), PERMISSIONNAME);
    }

    @Test
    public void testSetName() throws Exception {
        String newName = "JUNITNEWNAMEPERMISSION";
        sPerm.setName(newName);
        assertEquals(sPerm.getName(), newName);
    }

    @Test
    public void testPermissionArrayBuild() throws Exception {
        int length = 10;
        PermissionAO[] permArray = getPermissionArray(length);
        SerializablePermission[] sPermArray1 = SerializablePermission.build(permArray);
        SerializablePermission[] sPermArray2 = SerializablePermission.build(permArray);
        for (int i = 0; i < length; i++) {
            assertEquals(sPermArray1[i].getName(), sPermArray2[i].getName());
        }
    }
}