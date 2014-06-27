package forummacro.forum.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import forummacro.forum.exceptions.ThreadNotFoundException;
import forummacro.rightsmanagement.PermissionConfig;
import forummacro.rightsmanagement.RightsManagement;
import net.java.ao.DBParam;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Implementation of the ThreadRepo.. interface
 *
 * @author Nico Smolders
 */
public class ThreadRepositoryImplement implements ThreadRepository {

    private final ActiveObjects ao;
    private final RightsManagement rightsManagement;

    /**
     * The constructor
     *
     * @param ao
     * @param rightsManagement
     */
    public ThreadRepositoryImplement(ActiveObjects ao,
                                     RightsManagement rightsManagement) {
        this.rightsManagement = rightsManagement;
        this.ao = ao;
    }

    /**
     * Gets all the threads of the forum
     */
    @Override
    public List<ThreadAO> getAllThreads(int forumID) {
        ForumAO[] forums = ao.find(ForumAO.class, "ID = ?", forumID);
        ForumAO forum = null;
        for (ForumAO forumAO : forums) {
            forum = forumAO;
        }
        return newArrayList(forum.getThreads());
    }

    /**
     * Deletes a thread with the given id
     *
     * @param threadID
     * @param userKey
     */
    @Override
    public void delete(int threadID, String userKey) {
        boolean isAuthorisedToDelete = false;
        int forumID = ao.get(ThreadAO.class, threadID).getForum().getID();
        ThreadAO[] threads = ao.find(ThreadAO.class, "ID = ?", threadID);

        for (ThreadAO thread : threads) {
            if (thread.getUserKey() == userKey) {
                rightsManagement.isAuthorized(userKey,
                        PermissionConfig.PERMISSIONS.get("deleteOwnThread"),
                        forumID);
                isAuthorisedToDelete = true;
            } else {
                rightsManagement.isAuthorized(userKey,
                        PermissionConfig.PERMISSIONS.get("deleteAnyThread"),
                        forumID);
                isAuthorisedToDelete = true;
            }
            if (isAuthorisedToDelete) {
                PostAO[] posts = thread.getPosts();
                for (PostAO post : posts) {
                    ao.delete(ao.get(PostAO.class, post.getID()));
                }
                ao.delete(ao.get(ThreadAO.class, threadID).getState());
                ao.delete(ao.get(ThreadAO.class, threadID));
            }
        }
    }

    /**
     * Adds a thread
     *
     * @param title
     * @param description
     * @param sticky
     * @param date
     */
    @Override
    public ThreadAO add(String title, String description, boolean sticky,
                        Date date, int forumID, String userKey) {
        rightsManagement.isAuthorized(userKey,
                PermissionConfig.PERMISSIONS.get("createThread"), forumID);
        final ThreadAO thread = ao.create(ThreadAO.class, new DBParam("TITLE",
                        title), new DBParam("DESCRIPTION", description), new DBParam(
                        "STICKY", sticky), new DBParam("CREATION_DATE", date),
                new DBParam("USER_KEY", userKey), new DBParam("LAST_CHANGED_DATE", date));
        ForumAO[] forums = ao.find(ForumAO.class, "ID = ?", forumID);
        ForumAO forum = null;
        for (ForumAO forumAO : forums) {
            forum = forumAO;
        }
        thread.setForum(forum);
        thread.save();
        StateAO state = ao.create(StateAO.class, new DBParam("CLOSED", false), new DBParam("THREAD_ID", thread.getID()));
        state.save();
        return thread;
    }

    @Override
    public ThreadAO getThread(int forumID, int threadID) {
        ForumAO[] forums = ao.find(ForumAO.class, "ID = ?", forumID);
        ThreadAO[] threads = forums[0].getThreads();
        for (ThreadAO threadAO : threads) {
            if (threadAO.getID() == threadID) {
                return threadAO;
            }
        }
        throw new ThreadNotFoundException();
    }

	@Override
	public StateAO getState(int threadID) {
		ThreadAO[] threads = ao.find(ThreadAO.class, "ID = ?", threadID);
		for (ThreadAO threadAO : threads) {
			if (threadAO.getID() == threadID) {
				return threadAO.getState();
			}
		}
		throw new ThreadNotFoundException("Thread not found for threadID: "+threadID);
	}

	@Override
	public StateAO setState(int threadID, boolean closed, String userKey) {
		ThreadAO[] threads = ao.find(ThreadAO.class, "ID = ?", threadID);
		for (ThreadAO threadAO : threads) {
			if (threadAO.getID() == threadID) {
				threadAO.getState().setClosed(closed);
				Date date = Calendar.getInstance().getTime();
				threadAO.getState().setDate(date);
				threadAO.getState().setUserKey(userKey);
				threadAO.getState().save();
				return threadAO.getState();
			}
		}
		throw new ThreadNotFoundException("Thread not found for threadID: "+threadID);
	}

}
