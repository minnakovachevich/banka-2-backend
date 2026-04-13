package rs.raf.banka2_bek.auth.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordResetDtoTest {

    @Test
    void noArgsConstructorLeavesFieldsNull() {
        PasswordResetDto dto = new PasswordResetDto();
        assertThat(dto.getToken()).isNull();
        assertThat(dto.getNewPassword()).isNull();
    }

    @Test
    void allArgsConstructorSetsFields() {
        PasswordResetDto dto = new PasswordResetDto("tok-1", "NewPass11");
        assertThat(dto.getToken()).isEqualTo("tok-1");
        assertThat(dto.getNewPassword()).isEqualTo("NewPass11");
    }

    @Test
    void settersUpdateAllFields() {
        PasswordResetDto dto = new PasswordResetDto();
        dto.setToken("t");
        dto.setNewPassword("Aa11aaaa");
        assertThat(dto.getToken()).isEqualTo("t");
        assertThat(dto.getNewPassword()).isEqualTo("Aa11aaaa");
    }
}
