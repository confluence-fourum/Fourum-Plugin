package forummacro.rightsmanagement.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import net.java.ao.DBParam;

/**
 * Created by Joost Elshof on 10-6-14.
 */
public class UserManagementRepositoryImplement implements UserManagementRepository {
    private final ActiveObjects ao;

    public UserManagementRepositoryImplement(ActiveObjects ao) {
        this.ao = ao;
    }

    @Override
    public UserAO[] getUser(int forumId) {
        return ao.find(UserAO.class, "FORUM_ID = ?", forumId);
    }

    @Override
    public UserAO[] getUser(String userKey, int forumId) {
        return ao.find(UserAO.class, "USER_KEY = ? AND FORUM_ID = ?", userKey, forumId);

    }

    @Override
    public UserAO createNewUserAO(String newUserKey, int forumId, RoleAO roleAO) {
        final UserAO user = ao.create(UserAO.class, new DBParam("USER_KEY", newUserKey), new DBParam("FORUM_ID", forumId));
        user.setRole(roleAO);
        user.save();
        return user;
    }

    @Override
    public void deleteUser(UserAO userAO) {
        ao.delete(userAO);
    }

    @Override
    public void saveUser(UserAO userAO) {
        userAO.save();
    }
}
