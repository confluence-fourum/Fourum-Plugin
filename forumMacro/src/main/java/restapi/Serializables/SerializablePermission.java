package restapi.Serializables;

import forummacro.rightsmanagement.ao.PermissionAO;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rik on 13/05/2014.
 */
@XmlRootElement
public class SerializablePermission {
    private String name;

    private SerializablePermission(String name) {
        this.name = name;
    }

    public static SerializablePermission build(PermissionAO permission) {
        return new SerializablePermission(permission.getName());
    }

    public static SerializablePermission[] build(PermissionAO[] permissions) {
        List<SerializablePermission> serializablePermissions = new ArrayList<SerializablePermission>();
        for (PermissionAO permission : permissions) {
            serializablePermissions.add(new SerializablePermission(permission
                    .getName()));
        }
        return serializablePermissions.toArray(new SerializablePermission[]{});
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}