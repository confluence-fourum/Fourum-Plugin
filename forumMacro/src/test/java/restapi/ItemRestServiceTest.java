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
import restapi.Serializables.SerializablePost;

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
public class ItemRestServiceTest {
    private final String threadTitle = "threadTitleTest";
    private final String threadDescription = "threadDescriptionTest";
    private final String itemMessage = "itemMessageTest";
    private final boolean threadSticky = false;
    private final Date date = Calendar.getInstance().getTime();
    private final Sanitizer sanitizer = new Sanitizer();
    private EntityManager entityManager = null;
    private ActiveObjects ao;
    private ThreadRepository threadRepository;
    private ItemRepository itemRepository;
    private UserManagementRepository userManagementRepository;
    private RoleManagementRepository roleManagementRepository;
    private PermissionManagementRepository permissionManagementRepository;
    private ForumRepository forumRepository;
    private RightsManagement rightsManagement;
    private ItemRestService itemRestService;
    private ForumAO forumAO;
    private int forumID;
    private ConfluenceUser mockAdminUser;
    private String userKeyAdmin;
    private ThreadAO thread;
    private PostAO item;

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
        itemRestService = new ItemRestService(itemRepository, sanitizer);
        mockAdminUser = MockConfluenceUserFactory.mockUser();
        userKeyAdmin = mockAdminUser.getKey().getStringValue();
        rightsManagement.initPermissions();
        rightsManagement.initAdmin(userKeyAdmin, forumID);
        AuthenticatedUserThreadLocal.set(mockAdminUser);
        thread = threadRepository.add(threadTitle, threadDescription,
                threadSticky, date, forumID, userKeyAdmin);
        item = itemRepository.add(itemMessage, date, thread.getID(),
                userKeyAdmin);
    }

    @After
    public void after() {
        // Always clean up the thread local after the test so it doesn't affect
        // other tests
        AuthenticatedUserThreadLocal.reset();
    }

    @Test
    public void GetItemApiCallTest() {
        Response response = itemRestService.getItem(item.getID());
        SerializablePost expected = SerializablePost.build(item);
        SerializablePost responseSerializablePost = (SerializablePost) response
                .getEntity();
        assertEquals(expected.getMessage(),
                responseSerializablePost.getMessage());
        assertEquals(expected.getId(), responseSerializablePost.getId());
        assertEquals(expected.getCreationDate(), responseSerializablePost.getCreationDate());
        assertEquals(expected.getUserKey(),
                responseSerializablePost.getUserKey());
    }

    @Test
    public void SaveOneItemApiCallTest() {
        Response response = itemRestService.getAllItems(thread.getID());
        SerializablePost[] items = (SerializablePost[]) response.getEntity();
        assertEquals(items.length, itemRepository.getAllItems(thread.getID())
                .size());
        itemRestService.saveItem(itemMessage, thread.getID());
        assertEquals(items.length + 1,
                itemRepository.getAllItems(thread.getID()).size());
    }

    @Test
    public void DeleteOneItemApiCallTest() {
        Response response = itemRestService.getAllItems(thread.getID());
        SerializablePost[] items = (SerializablePost[]) response.getEntity();

        assertEquals(items.length, itemRepository.getAllItems(thread.getID())
                .size());
        itemRestService.deleteItem(item.getID());
        assertEquals(items.length - 1,
                itemRepository.getAllItems(thread.getID()).size());
    }

}