package lease.Approval.auth.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public enum Permission {

    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    MANAGER_READ("manager:read"),
    MANAGER_UPDATE("manager:update"),
    MANAGER_CREATE("manager:create"),
    MANAGER_DELETE("manager:delete");

    Permission(String permission) {
        this.permission = permission;
    }

    private final String permission;

    public String getPermission() {
        return permission;
    }
}
