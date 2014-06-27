package restapi.Serializables;

import forummacro.forum.ao.ThreadAO;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * When the restAPI is called, this class is used to build a thread.
 *
 * @author Nico Smolders
 */
@XmlRootElement
public class SerializableThread {

    private String title;
    private int id;
    private String description;
    private Date date;
    private Date lastChangedDate;
    private boolean sticky;
    private String userKey;

    private SerializableThread(String title, int id, String description,
                               boolean sticky, Date date, Date lastChangedDate, String userKey) {
        this.title = title;
        this.id = id;
        this.description = description;
        this.date = date;
        this.lastChangedDate = lastChangedDate;
        this.sticky = sticky;
        this.userKey = userKey;
    }

    public static SerializableThread build(ThreadAO threadAO) {
        return new SerializableThread(threadAO.getTitle(), threadAO.getID(),
                threadAO.getDescription(), threadAO.getSticky(),
                threadAO.getCreationDate(), threadAO.getLastChangedDate(), threadAO.getUserKey());
    }

    public static SerializableThread[] build(List<ThreadAO> threads) {
        List<SerializableThread> serializableThreads = new ArrayList<SerializableThread>();
        for (ThreadAO thread : threads) {
            serializableThreads.add(build(thread));
        }
        return serializableThreads.toArray(new SerializableThread[]{});
    }

    @XmlElement
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @XmlElement
    public Date getLastChangedDate(){
        return lastChangedDate;
    }

    public void setLastChangedDate(Date lastChangedDate){
        this.lastChangedDate = lastChangedDate;
    }

    @XmlElement
    public boolean getSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    @XmlElement
    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

}
