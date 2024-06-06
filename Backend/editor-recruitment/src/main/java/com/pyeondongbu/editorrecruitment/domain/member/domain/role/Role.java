package com.pyeondongbu.editorrecruitment.domain.member.domain.role;

public enum Role {
    CLIENT,
    EDITOR,
    ETC_WORKER,
    GUEST;

    public static boolean isValidRole(String role) {
        if (role == null) {
            return false;
        }
        try {
            Role.valueOf(role);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
