package restapi;

import forummacro.rightsmanagement.ao.UserAO;
import restapi.Serializables.SerializableRole;
import restapi.Serializables.SerializableUser;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rik on 13/05/2014.
 */
@XmlRootElement
public class MockSerializableUser implements SerializableUser {
    private String userKey;
    private SerializableRole role;
    private String name;

    private MockSerializableUser(String userKey, SerializableRole role, String name) {
        this.userKey = userKey;
        this.role = role;
        this.name = name;
    }

    public static SerializableUser build(UserAO user, String name) {
        return new MockSerializableUser(user.getUserKey(), SerializableRole.build(user.getRole()), name);
    }

    public static SerializableUser[] build(UserAO[] users, String name) {
        List<SerializableUser> serializableUsers = new ArrayList<SerializableUser>();
        for (UserAO userAO : users) {
            serializableUsers.add(new MockSerializableUser(userAO.getUserKey(),
                    SerializableRole.build(userAO.getRole()), name));
        }
        return serializableUsers.toArray(new SerializableUser[]{});
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

    @XmlElement
    public SerializableRole getRole() {
        return role;
    }

    public void setRole(SerializableRole role) {
        this.role = role;
    }
}