package forummacro.forum;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import forummacro.AOTestDatabaseUpdater;
import forummacro.forum.ao.*;
import forummacro.forum.exceptions.ThreadNotFoundException;
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

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Cees Mandjes
 * @author Jur Braam
 */

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(AOTestDatabaseUpdater.ServiceTestDatabaseUpdater.class)
public class ThreadRepositoryTest {
    private final String threadTitle = "threadTitleTest";
    private final String threadDescription = "threadDescriptionTest";
    private final boolean threadSticky = false;
    private final String itemMessage = "itemMessageTest";
    private final Date date = Calendar.getInstance().getTime();
    private EntityManager entityManager;
    private ActiveObjects ao;
    private ThreadRepository threadRepository;
    private ItemRepository itemRepository;
    private UserManagementRepository userManagementRepository;
    private RoleManagementRepository roleManagementRepository;
    private PermissionManagementRepository permissionManagementRepository;
    private ForumRepository forumRepository;
    private RightsManagement rightsManagement;
    private ConfluenceUser mockAdminUser, mockParticipantUser;
    private ForumAO forumAO;
    private int forumID;
    private String userKeyAdmin, userKeyParticipant;

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
        itemRepository = new ItemRepositoryImplement(ao, rightsManagement);
        mockAdminUser = MockConfluenceUserFactory.mockUser();
        mockParticipantUser = MockConfluenceUserFactory.mockUser();
        userKeyAdmin = mockAdminUser.getKey().getStringValue();
        userKeyParticipant = mockParticipantUser.getKey().getStringValue();
        rightsManagement.initPermissions();
        rightsManagement.initAdmin(userKeyAdmin, forumID);
        rightsManagement.addUser(userKeyAdmin, userKeyParticipant, "Admin",
                forumID);
        AuthenticatedUserThreadLocal.set(mockAdminUser);
        AuthenticatedUserThreadLocal.set(mockParticipantUser);
    }

    @After
    public void after() {
        // Always clean up the thread local after the test so it doesn't affect
        // other tests
        AuthenticatedUserThreadLocal.reset();
    }

    @Test
    public void makeOneThreadTest() {
        ThreadAO thread = threadRepository.add(threadTitle, threadDescription,
                threadSticky, date, forumID, userKeyAdmin);
        assertEquals(thread,
                threadRepository.getThread(forumID, thread.getID()));
    }

    @Test(expected = ThreadNotFoundException.class)
    public void getANonExistingThreadTest() {
        threadRepository.getThread(forumID, 23);
    }

    @Test
    public void DeleteOneThreadWithPostTest() {
        ThreadAO thread = threadRepository.add(threadTitle, threadDescription,
                threadSticky, date, forumID, userKeyAdmin);
        PostAO item = itemRepository.add(itemMessage, date, thread.getID(),
                userKeyAdmin);
        threadRepository.delete(thread.getID(), userKeyAdmin);
        assertEquals(0, threadRepository.getAllThreads(forumID).size());
    }

    @Test
    public void DeleteOneThreadWithoutPostWithAnotherAuthorisedUserTest() {
        ThreadAO thread = threadRepository.add(threadTitle, threadDescription,
                threadSticky, date, forumID, userKeyParticipant);
        threadRepository.delete(thread.getID(), userKeyAdmin);
        assertEquals(0, threadRepository.getAllThreads(forumID).size());
    }

}