package restapi.Serializables;

import forummacro.rightsmanagement.ao.RoleAO;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rik on 13/05/2014.
 */

@XmlRootElement
public class SerializableRole {
    private String name;
    private String description;
    private SerializablePermission[] permissions;

    private SerializableRole(String name, String description, SerializablePermission[] permissions) {
        this.name = name;
        this.description = description;
        this.permissions = permissions;
    }

    public static SerializableRole build(RoleAO role) {
        return new SerializableRole(role.getName(), role.getDescription(), SerializablePermission.build(role.getPermissions()));
    }


    public static SerializableRole[] build(RoleAO[] roles) {
        List<SerializableRole> serializableRoles = new ArrayList<SerializableRole>();
        for (RoleAO role : roles) {
            serializableRoles.add(SerializableRole.build(role));
        }
        return serializableRoles.toArray(new SerializableRole[]{});
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement
    public SerializablePermission[] getPermissions() {
        return permissions;
    }

    public void setPermissions(SerializablePermission[] permissions) {
        this.permissions = permissions;
    }
}