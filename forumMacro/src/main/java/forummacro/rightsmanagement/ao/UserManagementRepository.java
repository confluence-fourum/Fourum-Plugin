package forummacro.rightsmanagement.ao;

import com.atlassian.activeobjects.tx.Transactional;

/**
 * Created by Rik on 10/06/2014.
 */
@Transactional
public interface UserManagementRepository {
    UserAO[] getUser(int forumId);

    UserAO[] getUser(String userKey, int forumId);

    UserAO createNewUserAO(String newUserKey, int forumId, RoleAO roleAO);

    void deleteUser(UserAO userAO);

    void saveUser(UserAO userAO);
}
