package rs.raf.banka2_bek.auth.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageResponseDtoTest {

    @Test
    void noArgsConstructorLeavesMessageNull() {
        MessageResponseDto dto = new MessageResponseDto();
        assertThat(dto.getMessage()).isNull();
    }

    @Test
    void allArgsConstructorSetsMessage() {
        MessageResponseDto dto = new MessageResponseDto("hello");
        assertThat(dto.getMessage()).isEqualTo("hello");
    }

    @Test
    void setterUpdatesMessage() {
        MessageResponseDto dto = new MessageResponseDto();
        dto.setMessage("changed");
        assertThat(dto.getMessage()).isEqualTo("changed");
    }
}
