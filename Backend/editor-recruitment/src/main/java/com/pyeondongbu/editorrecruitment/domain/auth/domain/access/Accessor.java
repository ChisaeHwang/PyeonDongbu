package com.pyeondongbu.editorrecruitment.domain.auth.domain.access;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Slf4j
public class Accessor {

    private Long memberId;
    private Authority authority;

    public static Accessor guest() {
        return new Accessor(0L, Authority.GUEST);
    }

    public static Accessor member(final Long memberId) {
        return new Accessor(memberId, Authority.MEMBER);
    }

    public static Accessor admin(final Long memberId) {
        return new Accessor(memberId, Authority.ADMIN);
    }

    public static Accessor master(final Long memberId) {
        return new Accessor(memberId, Authority.MASTER);
    }


    public boolean isMember() {
        boolean isMember = Authority.MEMBER.equals(authority);
        return isMember;
    }

    public boolean isAdmin() {
        boolean isAdmin = Authority.ADMIN.equals(authority) || Authority.MASTER.equals(authority);
        return isAdmin;
    }

    public boolean isMaster() {
        boolean isMaster = Authority.MASTER.equals(authority);
        return isMaster;
    }

}
