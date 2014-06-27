package restapi.Serializables;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Rik on 02/06/2014.
 */
@XmlRootElement
public class SerializableProfile {
    private String name;
    private String role;
    private String profilePicture;

    private SerializableProfile(String name, String role, String profilePicture) {
        this.name = name;
        this.role = role;
        this.profilePicture = profilePicture;
    }

    public static SerializableProfile build(String name, String role, String profilePicture) {
        return new SerializableProfile(name, role, profilePicture);
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @XmlElement
    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
