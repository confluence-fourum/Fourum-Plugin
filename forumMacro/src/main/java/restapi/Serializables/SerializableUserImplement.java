package restapi.Serializables;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.spring.container.ContainerManager;
import forummacro.rightsmanagement.ao.UserAO;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rik on 13/05/2014.
 */
@XmlRootElement
public class SerializableUserImplement implements SerializableUser {
    private String userKey;
    private SerializableRole role;
    private String name;

    private SerializableUserImplement(String userKey, SerializableRole role) {
        this.userKey = userKey;
        this.role = role;
        UserAccessor userAccessor = (UserAccessor) ContainerManager.getInstance().getContainerContext().getComponent("userAccessor");
        ConfluenceUser user = userAccessor.getExistingUserByKey(new UserKey(userKey));
        this.name = user.getFullName();
    }

    public static SerializableUserImplement build(UserAO user) {
        return new SerializableUserImplement(user.getUserKey(), SerializableRole.build(user.getRole()));
    }

    public static SerializableUserImplement[] build(UserAO[] users) {
        List<SerializableUserImplement> serializableUsers = new ArrayList<SerializableUserImplement>();
        for (UserAO userAO : users) {
            serializableUsers.add(SerializableUserImplement.build(userAO));
        }
        return serializableUsers.toArray(new SerializableUserImplement[]{});
    }

    @Override
    @XmlElement
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @XmlElement
    public String getUserKey() {
        return userKey;
    }

    @Override
    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    @Override
    @XmlElement
    public SerializableRole getRole() {
        return role;
    }

    @Override
    public void setRole(SerializableRole role) {
        this.role = role;
    }
}