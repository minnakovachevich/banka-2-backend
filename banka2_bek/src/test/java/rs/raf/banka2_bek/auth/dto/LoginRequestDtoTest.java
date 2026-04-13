package rs.raf.banka2_bek.auth.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestDtoTest {

    @Test
    void noArgsConstructorLeavesFieldsNull() {
        LoginRequestDto dto = new LoginRequestDto();
        assertThat(dto.getEmail()).isNull();
        assertThat(dto.getPassword()).isNull();
    }

    @Test
    void allArgsConstructorSetsFields() {
        LoginRequestDto dto = new LoginRequestDto("user@banka.rs", "pass");
        assertThat(dto.getEmail()).isEqualTo("user@banka.rs");
        assertThat(dto.getPassword()).isEqualTo("pass");
    }

    @Test
    void settersUpdateAllFields() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("a@b.rs");
        dto.setPassword("secret");
        assertThat(dto.getEmail()).isEqualTo("a@b.rs");
        assertThat(dto.getPassword()).isEqualTo("secret");
    }
}
