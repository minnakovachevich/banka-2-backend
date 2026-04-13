package rs.raf.banka2_bek.auth.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthResponseDtoTest {

    @Test
    void noArgsConstructorDefaultsTokenTypeBearer() {
        AuthResponseDto dto = new AuthResponseDto();
        assertThat(dto.getTokenType()).isEqualTo("Bearer");
        assertThat(dto.getAccessToken()).isNull();
        assertThat(dto.getRefreshToken()).isNull();
    }

    @Test
    void allArgsConstructorSetsTokens() {
        AuthResponseDto dto = new AuthResponseDto("access-123", "refresh-456");
        assertThat(dto.getAccessToken()).isEqualTo("access-123");
        assertThat(dto.getRefreshToken()).isEqualTo("refresh-456");
        assertThat(dto.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    void settersUpdateAllFields() {
        AuthResponseDto dto = new AuthResponseDto();
        dto.setAccessToken("a");
        dto.setRefreshToken("r");
        dto.setTokenType("Custom");
        assertThat(dto.getAccessToken()).isEqualTo("a");
        assertThat(dto.getRefreshToken()).isEqualTo("r");
        assertThat(dto.getTokenType()).isEqualTo("Custom");
    }
}
