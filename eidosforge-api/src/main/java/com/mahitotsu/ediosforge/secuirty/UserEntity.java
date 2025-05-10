package com.mahitotsu.ediosforge.secuirty;

import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
public class UserEntity implements UserDetails {

    @Embeddable
    @Data
    public static class GrantedAuthorityEntity implements GrantedAuthority {

        @Column(nullable = false)
        @NotBlank
        private String authority;
    }

    @Id
    @Column(updatable = false)
    private UUID userId;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String username;

    @Column(nullable = false)
    @NotBlank
    private String password;

    @Column(nullable = false)
    private boolean accountNonExpired;

    @Column(nullable = false)
    private boolean accountNonLocked;

    @Column(nullable = false)
    private boolean credentialsNonExpired;

    @Column(nullable = false)
    private boolean enabled;

    @ElementCollection
    private Collection<GrantedAuthorityEntity> authorities;
}
