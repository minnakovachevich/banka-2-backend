package rs.raf.banka2_bek.auth.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void noArgsConstructor_defaults() {
        User u = new User();
        // default field initializers
        assertThat(u.isActive()).isTrue();
        assertThat(u.getRole()).isEqualTo("CLIENT");
        assertThat(u.getId()).isNull();
    }

    @Test
    void allArgsLikeConstructor_setsFields() {
        User u = new User("Marko", "Petrovic", "marko@banka.rs", "pass", true, "ADMIN");
        assertThat(u.getFirstName()).isEqualTo("Marko");
        assertThat(u.getLastName()).isEqualTo("Petrovic");
        assertThat(u.getEmail()).isEqualTo("marko@banka.rs");
        assertThat(u.getPassword()).isEqualTo("pass");
        assertThat(u.isActive()).isTrue();
        assertThat(u.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void settersAndGetters_coverAllFields() {
        User u = new User();
        u.setId(7L);
        u.setFirstName("Jelena");
        u.setLastName("Djordjevic");
        u.setEmail("jelena@banka.rs");
        u.setPassword("secret");
        u.setUsername("jelena");
        u.setPhone("+381601234567");
        u.setAddress("Knez Mihailova 1");
        u.setDateOfBirth(1234567890L);
        u.setGender("F");
        u.setSaltPassword("salt");
        u.setActive(false);
        u.setRole("EMPLOYEE");

        assertThat(u.getId()).isEqualTo(7L);
        assertThat(u.getFirstName()).isEqualTo("Jelena");
        assertThat(u.getLastName()).isEqualTo("Djordjevic");
        assertThat(u.getEmail()).isEqualTo("jelena@banka.rs");
        assertThat(u.getPassword()).isEqualTo("secret");
        // getUsername() returns email per implementation
        assertThat(u.getUsername()).isEqualTo("jelena@banka.rs");
        assertThat(u.getPhone()).isEqualTo("+381601234567");
        assertThat(u.getAddress()).isEqualTo("Knez Mihailova 1");
        assertThat(u.getDateOfBirth()).isEqualTo(1234567890L);
        assertThat(u.getGender()).isEqualTo("F");
        assertThat(u.getSaltPassword()).isEqualTo("salt");
        assertThat(u.isActive()).isFalse();
        assertThat(u.getRole()).isEqualTo("EMPLOYEE");
    }

    @Test
    void getAuthorities_returnsRolePrefixedAuthority() {
        User u = new User();
        u.setRole("ADMIN");
        Collection<? extends GrantedAuthority> auths = u.getAuthorities();
        assertThat(auths).hasSize(1);
        assertThat(auths.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void userDetailsFlags_alwaysTrueExceptEnabled() {
        User u = new User();
        u.setActive(true);
        assertThat(u.isAccountNonExpired()).isTrue();
        assertThat(u.isAccountNonLocked()).isTrue();
        assertThat(u.isCredentialsNonExpired()).isTrue();
        assertThat(u.isEnabled()).isTrue();

        u.setActive(false);
        assertThat(u.isEnabled()).isFalse();
    }
}
