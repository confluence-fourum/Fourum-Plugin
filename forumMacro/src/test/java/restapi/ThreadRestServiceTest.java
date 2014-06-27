package restapi;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import forummacro.AOTestDatabaseUpdater;
import forummacro.forum.MockConfluenceUserFactory;
import forummacro.forum.ao.*;
import forummacro.rightsmanagement.RightsManagement;
import forummacro.rightsmanagement.RightsManagementImplement;
import forummacro.rightsmanagement.ao.*;
import net.java.ao.DBParam;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import restapi.Serializables.SerializableThread;

import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Jur Braam
 * @author Cees Mandjes
 */

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(AOTestDatabaseUpdater.ServiceTestDatabaseUpdater.class)
public class ThreadRestServiceTest {
    private final String threadTitle = "test";
    private final String threadDescription = "test";
    private final boolean threadSticky = false;
    private final Date threadDate = Calendar.getInstance().getTime();
    private final Sanitizer sanitizer = new Sanitizer();
    private EntityManager entityManager = null;
    private ActiveObjects ao;
    private ThreadRepository threadRepository;
    private UserManagementRepository userManagementRepository;
    private RoleManagementRepository roleManagementRepository;
    private PermissionManagementRepository permissionManagementRepository;
    private ForumRepository forumRepository;
    private RightsManagement rightsManagement;
    private ThreadRestService threadRestService;
    private ForumAO forumAO;
    private int forumID;
    private ConfluenceUser mockAdminUser;
    private String userKeyAdmin;
    private ThreadAO thread;

    @Before
    public void setUp() throws Exception {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        forumAO = ao.create(ForumAO.class, new DBParam("TITLE", "ForumTitle"));
        forumAO.setDescription("ForumDescription");
        forumAO.save();
        forumID = forumAO.getID();
        userManagementRepository = new UserManagementRepositoryImplement(ao);
        roleManagementRepository = new RoleManagementRepositoryImplement(ao);
        permissionManagementRepository = new PermissionManagementRepositoryImplement(ao);
        forumRepository = new ForumRepositoryImplement(ao);
        rightsManagement = new RightsManagementImplement(userManagementRepository, roleManagementRepository, permissionManagementRepository, forumRepository);
        threadRepository = new ThreadRepositoryImplement(ao, rightsManagement);
        threadRestService = new ThreadRestService(threadRepository, sanitizer);
        mockAdminUser = MockConfluenceUserFactory.mockUser();
        userKeyAdmin = mockAdminUser.getKey().getStringValue();
        rightsManagement.initPermissions();
        rightsManagement.initAdmin(userKeyAdmin, forumID);
        AuthenticatedUserThreadLocal.set(mockAdminUser);
        thread = threadRepository.add(threadTitle, threadDescription,
                threadSticky, threadDate, forumID, userKeyAdmin);
    }

    @After
    public void after() {
        // Always clean up the thread local after the test so it doesn't affect
        // other tests
        AuthenticatedUserThreadLocal.reset();
    }

    @Test
    public void GetThreadApiCallTest() {
        Response response = threadRestService
                .getThread(forumID, thread.getID());
        SerializableThread expected = SerializableThread.build(thread);
        SerializableThread responseSerializableThread = (SerializableThread) response
                .getEntity();
        assertEquals(expected.getTitle(), responseSerializableThread.getTitle());
        assertEquals(expected.getId(), responseSerializableThread.getId());
        assertEquals(expected.getDescription(),
                responseSerializableThread.getDescription());
        assertEquals(expected.getSticky(),
                responseSerializableThread.getSticky());
        assertEquals(expected.getDate(), responseSerializableThread.getDate());
        assertEquals(expected.getUserKey(),
                responseSerializableThread.getUserKey());
    }

    @Test
    public void SaveOneThreadApiCallTest() {
        Response response = threadRestService.getAllThreads(forumID);
        SerializableThread[] threads = (SerializableThread[]) response
                .getEntity();

        assertEquals(threads.length, threadRepository.getAllThreads(forumID)
                .size());
        threadRestService.saveThread(threadTitle, threadDescription,
                threadSticky, forumID);
        assertEquals(threads.length + 1, threadRepository
                .getAllThreads(forumID).size());
    }

    @Test
    public void DeleteOneThreadApiCallTest() {
        Response response = threadRestService.getAllThreads(forumID);
        SerializableThread[] threads = (SerializableThread[]) response
                .getEntity();

        assertEquals(threads.length, threadRepository.getAllThreads(forumID)
                .size());
        threadRestService.deleteThread(thread.getID());
        assertEquals(threads.length - 1, threadRepository
                .getAllThreads(forumID).size());
    }
}