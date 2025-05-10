package com.mahitotsu.ediosforge.security;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import com.mahitotsu.ediosforge.base.TestBase;
import com.mahitotsu.ediosforge.secuirty.JpaUserDetailsManager;
import com.mahitotsu.ediosforge.secuirty.UserEntity;
import com.mahitotsu.ediosforge.secuirty.UserRepository;

public class JpaUserDetailsManagerTest extends TestBase {

    private static final Random RANDOM = new Random();

    @Autowired
    private JpaUserDetailsManager userDetailsManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserEntity createRandomUserEntity(final String... authorities) {

        final String number = String.format("%019d", RANDOM.nextLong(Long.MAX_VALUE));
        final String username = "user" + number;
        final String password = "pass" + number;

        final UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(this.passwordEncoder.encode(password));
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);

        user.setAuthorities(new ArrayList<>());
        user.getAuthorities().addAll(Arrays.stream(authorities).map(a -> {
            final UserEntity.GrantedAuthorityEntity authority = new UserEntity.GrantedAuthorityEntity();
            authority.setAuthority(a);
            return authority;
        }).toList());

        return user;
    }

    @Test
    public void test_loadUserByUsername() {

        final UserEntity entity = this.createRandomUserEntity("ROLE_ADMIN", "ROLE_USER");
        this.userRepository.save(entity);

        final UserDetails user = this.userDetailsManager.loadUserByUsername(entity.getUsername());
        assertThat(user)
                .usingRecursiveComparison()
                .ignoringFields("entityId", "entityVersion")
                .isNotSameAs(entity)
                .isEqualTo(entity);
    }

    @Test
    public void test_loadUserByUsername_UsernameNotFound() {

        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> this.userDetailsManager.loadUserByUsername("uzer0123456789987654321"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void test_createUser() {

        final UserEntity entity = this.createRandomUserEntity("ROLE_ADMIN", "ROLE_USER");
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> this.userDetailsManager.loadUserByUsername(entity.getUsername()));

        this.userDetailsManager.createUser(entity);

        final UserDetails user = this.userDetailsManager.loadUserByUsername(entity.getUsername());
        assertThat(user)
                .usingRecursiveComparison()
                .ignoringFields("entityId", "entityVersion")
                .isNotSameAs(entity)
                .isEqualTo(entity);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void test_updateUser() {

        final UserEntity entity = this.createRandomUserEntity("ROLE_ADMIN", "ROLE_USER");
        this.userRepository.save(entity);

        entity.setAccountNonExpired(false);
        entity.setAccountNonLocked(false);
        entity.setCredentialsNonExpired(false);

        final UserDetails user1 = this.userDetailsManager.loadUserByUsername(entity.getUsername());
        assertThat(user1)
                .usingRecursiveComparison()
                .ignoringFields("entityId", "entityVersion")
                .isNotSameAs(entity)
                .isNotEqualTo(entity);

        this.userDetailsManager.updateUser(entity);
        final UserDetails user2 = this.userDetailsManager.loadUserByUsername(entity.getUsername());
        assertThat(user2)
                .usingRecursiveComparison()
                .ignoringFields("entityId", "entityVersion")
                .isNotSameAs(entity)
                .isEqualTo(entity);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void test_deleteUser() {

        final UserEntity entity = this.createRandomUserEntity("ROLE_ADMIN", "ROLE_USER");
        this.userRepository.save(entity);
        assertThat(this.userDetailsManager.loadUserByUsername(entity.getUsername())).isNotNull();

        this.userDetailsManager.deleteUser(entity.getUsername());
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> this.userDetailsManager.loadUserByUsername(entity.getUsername()));
    }
}
