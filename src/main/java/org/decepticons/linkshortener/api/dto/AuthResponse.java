package org.decepticons.linkshortener.api.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
@Getter
@Setter
public class AuthResponse {
    private String username;
    private Collection<? extends GrantedAuthority> authorities;
    private String token;

    public AuthResponse(String username, Collection<? extends GrantedAuthority> authorities, String token) {
        this.username = username;
        this.authorities = authorities;
        this.token = token;
    }
}