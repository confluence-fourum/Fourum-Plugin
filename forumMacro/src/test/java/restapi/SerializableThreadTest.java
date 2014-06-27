package restapi;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import forummacro.AOTestDatabaseUpdater;
import forummacro.forum.ao.ForumAO;
import forummacro.forum.ao.ThreadAO;
import net.java.ao.DBParam;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import restapi.Serializables.SerializableThread;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(AOTestDatabaseUpdater.ServiceTestDatabaseUpdater.class)
public class SerializableThreadTest {
    public static final String TESTTITLE = "JUNIT FORUM";
    public static final String TESTDESCRIPTION = "DESCRIPTION JUNIT";
    public static final Boolean TESTSTICKY = false;
    public static final Date TESTCREATIONDATE = Calendar.getInstance().getTime();
    private static final String FORUMTITLE = "FORUM";
    private static final String FORUMDESCRIPTION = "FORUM DESCRIPTION";
    private static final String USERKEY = "JUNITKEY";
    SerializableThread sThread;
    private EntityManager entityManager;
    private ActiveObjects ao;
    private ThreadAO thread;
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
        thread = ao.create(ThreadAO.class, new DBParam("TITLE", TESTTITLE), new DBParam("DESCRIPTION", TESTDESCRIPTION),
                new DBParam("STICKY", TESTSTICKY), new DBParam("CREATION_DATE", TESTCREATIONDATE), new DBParam("FORUM_ID", forumID), new DBParam("USER_KEY", USERKEY));
        sThread = SerializableThread.build(thread);
    }

    @Test
    public void testThreadArrayBuildLength() throws Exception {
        int length = 10;
        List<ThreadAO> threadArray = getThreadArray(length);
        assertEquals(length, threadArray.size());
        SerializableThread[] threadArray1 = SerializableThread.build(threadArray);
        SerializableThread[] threadArray2 = SerializableThread.build(threadArray);
        assertEquals(threadArray1.length, threadArray2.length);
    }

    @Test
    public void testThreadArrayBuild() throws Exception {
        int length = 10;
        List<ThreadAO> threadArray = getThreadArray(length);
        SerializableThread[] threadArray1 = SerializableThread.build(threadArray);
        SerializableThread[] threadArray2 = SerializableThread.build(threadArray);
        for (int i = 0; i < length; i++) {
            SerializableThread thread1 = threadArray1[i];
            SerializableThread thread2 = threadArray2[i];
            assertEquals(thread1.getTitle(), thread2.getTitle());
            assertEquals(thread1.getDescription(), thread2.getDescription());
            assertEquals(thread1.getDate(), thread2.getDate());
            assertEquals(thread1.getId(), thread2.getId());
            assertEquals(thread1.getSticky(), thread2.getSticky());
        }
    }

    private List<ThreadAO> getThreadArray(int length) {
        List<ThreadAO> threadList = new ArrayList<ThreadAO>();
        for (int i = 0; i < length; i++) {
            threadList.add(getThread("Name " + i, "Description " + i));
        }
        return threadList;
    }

    private ThreadAO getThread(String name, String description) {
        return ao.create(ThreadAO.class, new DBParam("TITLE", name), new DBParam("DESCRIPTION", description),
                new DBParam("STICKY", TESTSTICKY), new DBParam("CREATION_DATE", TESTCREATIONDATE), new DBParam("FORUM_ID", forumID), new DBParam("USER_KEY", USERKEY));
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals(thread.getID(), sThread.getId());
    }

    @Test
    public void testSetId() throws Exception {
        int newId = 42;
        sThread.setId(newId);
        assertEquals(newId, sThread.getId());
    }

    @Test
    public void testGetTitle() throws Exception {
        assertEquals(TESTTITLE, sThread.getTitle());
    }

    @Test
    public void testSetTitle() throws Exception {
        String newTitle = "SO LONG AND THANKS FOR ALL THE FISH";
        sThread.setTitle(newTitle);
        assertEquals(newTitle, sThread.getTitle());
    }

    @Test
    public void testGetDescription() throws Exception {
        assertEquals(sThread.getDescription(), TESTDESCRIPTION);
    }

    @Test
    public void testSetDescription() throws Exception {
        String newDescription = "The restaurant at the end of the universe";
        sThread.setDescription(newDescription);
        assertEquals(newDescription, sThread.getDescription());
    }

    @Test
    public void testGetDate() throws Exception {
        assertEquals(TESTCREATIONDATE, sThread.getDate());
    }

    @Test
    public void testSetDate() throws Exception {
        Date testDate = Calendar.getInstance().getTime();
        sThread.setDate(testDate);
        assertEquals(sThread.getDate(), testDate);
    }

    @Test
    public void testGetSticky() throws Exception {
        assertEquals(sThread.getSticky(), TESTSTICKY);
    }

    @Test
    public void testSetSticky() throws Exception {
        Boolean newSticky = true;
        sThread.setSticky(newSticky);
        assertEquals(newSticky, sThread.getSticky());
    }
}