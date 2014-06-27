package forummacro.forum.ao;

import java.util.Date;

import net.java.ao.Entity;
import net.java.ao.schema.NotNull;

/**
 * 
 * @author Nico Smolders
 *
 */
public interface StateAO extends Entity {
	@NotNull
	public boolean getClosed();
	public void setClosed(boolean closed);
	
	public String getUserKey();
	public void setUserKey(String userKey);
	
	public ThreadAO getThread();
	public void setThread(ThreadAO thread);
	
	public Date getDate();
	public void setDate(Date date);
}
