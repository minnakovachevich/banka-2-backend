package rs.raf.banka2_bek.auth.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordResetRequestDtoTest {

    @Test
    void noArgsConstructorLeavesEmailNull() {
        PasswordResetRequestDto dto = new PasswordResetRequestDto();
        assertThat(dto.getEmail()).isNull();
    }

    @Test
    void allArgsConstructorSetsEmail() {
        PasswordResetRequestDto dto = new PasswordResetRequestDto("a@b.rs");
        assertThat(dto.getEmail()).isEqualTo("a@b.rs");
    }

    @Test
    void setterUpdatesEmail() {
        PasswordResetRequestDto dto = new PasswordResetRequestDto();
        dto.setEmail("x@y.rs");
        assertThat(dto.getEmail()).isEqualTo("x@y.rs");
    }
}
