package com.megait.soir.domain;

import org.springframework.security.core.GrantedAuthority;

public enum MemberType implements GrantedAuthority {
    ADMIN, USER;


    @Override
    public String getAuthority() {
        return name();
    }
}
