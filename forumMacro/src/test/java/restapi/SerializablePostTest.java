package restapi;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import forummacro.AOTestDatabaseUpdater;
import forummacro.forum.ao.AttachmentAO;
import forummacro.forum.ao.PostAO;
import net.java.ao.DBParam;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import restapi.Serializables.SerializableAttachment;
import restapi.Serializables.SerializablePost;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(AOTestDatabaseUpdater.ServiceTestDatabaseUpdater.class)
public class SerializablePostTest {
    private static final Date DATE = Calendar.getInstance().getTime();
    private static final String MESSAGE = "TEST MESSAGE";
    private static final String USERKEY = "1";
    private static final String ATTACHMENTNAME = "ATTACHMENT1";
    private static final String ATTACHMENTURL = "http://www.google.nl/logo.png";
    private EntityManager entityManager = null;
    private ActiveObjects ao;
    private PostAO post;
    private AttachmentAO attachment;

    @Before
    public void setUp() throws Exception {
        ao = new TestActiveObjects(entityManager);
        post = ao.create(PostAO.class, new DBParam("USER_KEY", USERKEY), new DBParam("CREATION_DATE", DATE), new DBParam("LAST_EDITED_DATE", DATE), new DBParam("MESSAGE", MESSAGE));
        AttachmentAO attachment = ao.create(AttachmentAO.class, new DBParam("NAME", ATTACHMENTNAME), new DBParam("URL", ATTACHMENTURL));
        attachment.setPost(post);
        attachment.save();
        this.attachment = attachment;
    }

    @Test
    public void testBuild() throws Exception {
        SerializablePost sPost = SerializablePost.build(post);
    }

    @Test
    public void testBuild1() throws Exception {

    }

    @Test
    public void testGetMessage() throws Exception {
        SerializablePost sPost = SerializablePost.build(post);
        assertEquals(MESSAGE, sPost.getMessage());
    }

    @Test
    public void testSetMessage() throws Exception {

    }

    @Test
    public void testGetDate() throws Exception {
        SerializablePost sPost = SerializablePost.build(post);
        assertEquals(DATE, sPost.getCreationDate());
    }

    @Test
    public void testSetDate() throws Exception {

    }

    @Test
    public void testGetId() throws Exception {
        SerializablePost sPost = SerializablePost.build(post);
        assertEquals(post.getID(), sPost.getId());
    }

    @Test
    public void testSetId() throws Exception {

    }

    @Test
    public void testGetUserKey() throws Exception {
        SerializablePost sPost = SerializablePost.build(post);
        assertEquals(USERKEY, sPost.getUserKey());
    }

    @Test
    public void testSetUserKey() throws Exception {

    }

    @Test
    public void testGetAttachments() throws Exception {
        SerializablePost sPost = SerializablePost.build(post);
        SerializableAttachment[] attachments = sPost.getAttachments();
        SerializableAttachment sAttachment = SerializableAttachment.build(attachment);
        if (attachments.length != 1) {
            fail("Expected length 1 got length " + attachments.length);
        }
        if (!sAttachment.equals(sPost.getAttachments()[0])) {
            fail(sAttachment.getName() + "is not equal to " + attachments[0].getName());
        }
    }

    @Test(expected = java.lang.AssertionError.class)
    public void testGetAttachmentsFail() throws Exception {
        SerializablePost sPost = SerializablePost.build(post);
        SerializableAttachment[] attachments = sPost.getAttachments();
        SerializableAttachment sAttachment = SerializableAttachment.build();
        sAttachment.setName("FAIL TEST");
        sAttachment.setUrl("FAIl URL");
        if (attachments.length != 1) {
            fail("Expected length 1 got length " + attachments.length);
        }
        if (!sAttachment.equals(sPost.getAttachments()[0])) {
            fail(sAttachment.getName() + " is not equal to " + attachments[0].getName());
        }
    }

    @Test
    public void testSetAttachments() throws Exception {

    }
}