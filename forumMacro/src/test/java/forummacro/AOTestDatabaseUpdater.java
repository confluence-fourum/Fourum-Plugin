package forummacro;

import forummacro.forum.ao.AttachmentAO;
import forummacro.forum.ao.ForumAO;
import forummacro.forum.ao.PostAO;
import forummacro.forum.ao.StateAO;
import forummacro.forum.ao.ThreadAO;
import forummacro.rightsmanagement.ao.PermissionAO;
import forummacro.rightsmanagement.ao.RoleAO;
import forummacro.rightsmanagement.ao.RoleToPermissionAO;
import forummacro.rightsmanagement.ao.UserAO;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.DatabaseUpdater;

/**
 * Created by Rik on 22/05/2014.
 */
public class AOTestDatabaseUpdater {
    private AOTestDatabaseUpdater() {
    }

    ;

    public static final class ServiceTestDatabaseUpdater implements DatabaseUpdater {
        @Override
        public void update(EntityManager entityManager) throws Exception {
            entityManager.migrate(UserAO.class, RoleAO.class, RoleToPermissionAO.class, PermissionAO.class, ForumAO.class, ThreadAO.class, AttachmentAO.class, PostAO.class, StateAO.class);
        }
    }
}
