package forummacro.forum;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import forummacro.AOTestDatabaseUpdater;
import forummacro.forum.ao.*;
import forummacro.forum.exceptions.ItemNotFoundException;
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
 * @author Jur Braam
 * @author Cees Mandjes
 */

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(AOTestDatabaseUpdater.ServiceTestDatabaseUpdater.class)
public class ItemRepositoryTest {
    private final String threadTitle = "threadTitleTest";
    private final String threadDescription = "threadDescriptionTest";
    private final String itemMessage = "itemMessageTest";
    private final boolean threadSticky = false;
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
    private ThreadAO thread;
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
        thread = threadRepository.add(threadTitle, threadDescription,
                threadSticky, date, forumID, userKeyAdmin);
    }

    @After
    public void after() {
        // Always clean up the thread local after the test so it doesn't affect
        // other tests
        AuthenticatedUserThreadLocal.reset();
    }

    @Test
    public void makeOneItemTest() {
        PostAO item = itemRepository.add(itemMessage, date, thread.getID(),
                userKeyAdmin);
        assertEquals(item, itemRepository.getItem(item.getID()));
        assertEquals(1, itemRepository.getAllItems(thread.getID()).size());
    }

    @Test(expected = ItemNotFoundException.class)
    public void getANonExistingItemTest() {
        itemRepository.getItem(23);
    }

    @Test
    public void DeleteOneItem() {
        PostAO item = itemRepository.add(itemMessage, date, thread.getID(),
                userKeyAdmin);
        itemRepository.delete(item.getID(), userKeyAdmin);
        assertEquals(0, itemRepository.getAllItems(thread.getID()).size());
    }

    @Test
    public void DeleteOneItemWithAnotherAuthorisedUserTest() {
        PostAO item = itemRepository.add(itemMessage, date, thread.getID(),
                userKeyParticipant);
        itemRepository.delete(item.getID(), userKeyAdmin);
        assertEquals(0, itemRepository.getAllItems(thread.getID()).size());
    }

    @Test
    public void makeQuoteTest() {
        PostAO item = itemRepository.add(itemMessage, date, thread.getID(),
                userKeyAdmin);
        PostAO item2 = itemRepository.add(itemMessage, date, thread.getID(),
                userKeyAdmin, item.getID());

        assertEquals(item.getMessage(), itemRepository.getItem(item2.getID()).getQuote().getMessage());
        assertEquals(2, itemRepository.getAllItems(thread.getID()).size());
    }

}