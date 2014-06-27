package restapi.Serializables;

import com.atlassian.confluence.user.ConfluenceUser;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rik on 02/06/2014.
 */
@XmlRootElement
public class SerializableConfluenceUser {
    private String userKey;
    private String name;
    private String fullName;

    private SerializableConfluenceUser(String name, String fullName, String userKey) {
        this.name = name;
        this.userKey = userKey;
        this.fullName = fullName;
    }

    public static SerializableConfluenceUser build(ConfluenceUser user) {
        return new SerializableConfluenceUser(user.getName(), user.getFullName(), user.getKey().getStringValue());
    }

    public static SerializableConfluenceUser[] build(List<ConfluenceUser> users) {
        List<SerializableConfluenceUser> serializableConfluenceUsers = new ArrayList<SerializableConfluenceUser>();
        for (ConfluenceUser user : users) {
            serializableConfluenceUsers.add(new SerializableConfluenceUser(user.getName(), user.getFullName(), user.getKey().getStringValue()));
        }
        return serializableConfluenceUsers.toArray(new SerializableConfluenceUser[]{});
    }


    @XmlElement
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
}
