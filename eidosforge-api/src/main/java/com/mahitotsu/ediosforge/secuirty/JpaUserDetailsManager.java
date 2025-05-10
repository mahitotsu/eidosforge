package com.mahitotsu.ediosforge.secuirty;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
public class JpaUserDetailsManager implements UserDetailsManager {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return this.findByUsername(username);
    }

    private UserEntity findByUsername(final String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    // -----

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public void createUser(final UserDetails user) {
        this.userRepository.save(this.copyProperties(user, new UserEntity()));
    }

    @PreAuthorize("authentication.name == #user.username or hasRole('ADMIN')")
    @Transactional
    @Override
    public void updateUser(@P("user") final UserDetails user) {

        final UserEntity entity = this.findByUsername(user.getUsername());
        if (!ObjectUtils.nullSafeEquals(user.getPassword(), entity.getPassword())) {
            throw new BadCredentialsException("The specified user does not have a valid password.");
        }

        this.userRepository.save(this.copyProperties(user, entity));
    }

    private UserEntity copyProperties(final UserDetails user, final UserEntity entity) {

        final UserEntity u = entity;
        if (u == null) {
            return null;
        }

        u.setUsername(user.getUsername());
        u.setPassword(user.getPassword());

        u.setAccountNonExpired(user.isAccountNonExpired());
        u.setAccountNonLocked(user.isAccountNonLocked());
        u.setCredentialsNonExpired(user.isCredentialsNonExpired());
        u.setEnabled(user.isEnabled());

        if (u.getAuthorities() == null) {
            u.setAuthorities(new ArrayList<>());
        } else {
            u.getAuthorities().clear();
        }
        u.getAuthorities().addAll(user.getAuthorities().stream().map(grantedAuthority -> {
            final UserEntity.GrantedAuthorityEntity a = new UserEntity.GrantedAuthorityEntity();
            a.setAuthority(grantedAuthority.getAuthority());
            return a;
        }).toList());

        return u;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public void deleteUser(final String username) {

        try {
            this.userRepository.delete(this.findByUsername(username));
        } catch (UsernameNotFoundException e) {
            return;
        }
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    @Override
    public void changePassword(final String oldPassword, final String newPassword) {

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final UserEntity entity = this.findByUsername(auth.getName());

        if (!this.passwordEncoder.matches(oldPassword, entity.getPassword())) {
            throw new BadCredentialsException("The specified oldPassword is invalid.");
        }

        entity.setPassword(this.passwordEncoder.encode(newPassword));
    }

    @Transactional(readOnly = true)
    @Override
    public boolean userExists(String username) {
        return this.userRepository.findByUsername(username).isPresent();
    }
}
