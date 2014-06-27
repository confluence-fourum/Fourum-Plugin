package forummacro.forum.ao;

import com.atlassian.activeobjects.tx.Transactional;

/**
 * Created by Rik on 10/06/2014.
 */
@Transactional
public interface ForumRepository {
    /**
     * Returns the forum which belongs to the given forum id
     *
     * @param forumId the forum id you want to get
     * @return the forum found
     */
    ForumAO getForum(int forumId);
}
