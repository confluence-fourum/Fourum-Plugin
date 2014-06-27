package forummacro.forum.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import forummacro.rightsmanagement.exceptions.ForumNotFoundException;

/**
 * Created by Rik on 10/06/2014.
 */
public class ForumRepositoryImplement implements ForumRepository {
    private ActiveObjects ao;

    public ForumRepositoryImplement(ActiveObjects ao) {
        this.ao = ao;
    }

    @Override
    public ForumAO getForum(int forumId) {
        ForumAO forum = ao.get(ForumAO.class, forumId);
        if (forum == null) {
            throw new ForumNotFoundException();
        }
        return forum;
    }
}
