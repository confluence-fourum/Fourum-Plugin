package forummacro.rightsmanagement;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rik on 13/05/2014.
 */
public class PermissionConfig {
    public static final String ADMINDESCRIPTION = "This is the administrative user";
    public static final String ADMIN = "Admin";

    public static final String DELETEDROLE = "Deleted";
    public static final String DELETEDROLEDESCRIPTION = "Deleted User";

    public static final Map<String, String> PERMISSIONS = ImmutableMap.<String, String>builder()
            .put("manageUsers", "MANAGEUSERS")
            .put("addUser", "ADDUSER")
            .put("createRole", "CREATEROLE")
            .put("setRoleName", "SETROLENAME")
            .put("read", "READ")
            .put("setRoleDescription", "SETROLEDESCRIPTION")
            .put("deleteRole", "DELETEROLE")
            .put("setPermissions", "SETPERMISSIONS")
            .put("changeUserRole", "CHANGEUSERROLE")
            .put("deleteUser", "DELETEUSER")
            .put("createThread", "CREATETHREAD")
            .put("createItem", "CREATEITEM")
            .put("deleteOwnThread", "DELETEOWNTHREAD")
            .put("deleteAnyThread", "DELETEANYTHREAD")
            .put("deleteOwnItem", "DELETEOWNITEM")
            .put("deleteAnyItem", "DELETEANYITEM")
            .put("editOwnItem", "EDITOWNITEM")
            .put("editAnyItem", "EDITANYITEM")
            .put("addOwnQuote", "ADDOWNQUOTE")
            .put("addAnyQuote", "ADDANYQUOTE")
            .put("deleteOwnQuote", "DELETEOWNQUOTE")
            .put("deleteAnyQuote", "DELETEANYQUOTE")
            .put("addOwnAttachment", "ADDOWNATTACHMENTS")
            .put("addAnyAttachment", "ADDANYATTACHMENTS")
            .build();


    private PermissionConfig() {
    }

    public static List<String> getPermissions() {
        return new ArrayList(PERMISSIONS.values());
    }

    public static String get(String permission) {
        return PERMISSIONS.get(permission);
    }
}
