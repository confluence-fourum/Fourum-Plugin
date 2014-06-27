package forummacro.forum.ao;

import com.atlassian.activeobjects.tx.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Interface for creating and deleting threads
 *
 * @author Nico Smolders
 */
@Transactional
public interface ThreadRepository {
    ThreadAO add(String title, String description, boolean sticky,
                 Date creationDate, int forumID, String userKey);

    List<ThreadAO> getAllThreads(int forumID);

    ThreadAO getThread(int forumID, int threadID);

    void delete(int id, String userKey);
    
    StateAO getState(int threadID);
    
    StateAO setState(int threadID, boolean state, String userKey);
}
