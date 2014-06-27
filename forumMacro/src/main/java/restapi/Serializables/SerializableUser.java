package restapi.Serializables;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Rik on 04/06/2014.
 */
public interface SerializableUser {
    @XmlElement
    String getName();

    void setName(String name);

    @XmlElement
    String getUserKey();

    void setUserKey(String userKey);

    @XmlElement
    SerializableRole getRole();

    void setRole(SerializableRole role);
}
