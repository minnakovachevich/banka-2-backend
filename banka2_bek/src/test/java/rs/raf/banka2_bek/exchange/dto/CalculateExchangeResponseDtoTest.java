package rs.raf.banka2_bek.exchange.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CalculateExchangeResponseDtoTest {

    @Test
    void noArgsConstructorDefaults() {
        CalculateExchangeResponseDto dto = new CalculateExchangeResponseDto();
        assertThat(dto.getConvertedAmount()).isEqualTo(0.0);
        assertThat(dto.getExchangeRate()).isEqualTo(0.0);
        assertThat(dto.getFromCurrency()).isNull();
        assertThat(dto.getToCurrency()).isNull();
    }

    @Test
    void allArgsConstructorSetsAllFields() {
        CalculateExchangeResponseDto dto = new CalculateExchangeResponseDto(123.45, 1.1, "EUR", "RSD");
        assertThat(dto.getConvertedAmount()).isEqualTo(123.45);
        assertThat(dto.getExchangeRate()).isEqualTo(1.1);
        assertThat(dto.getFromCurrency()).isEqualTo("EUR");
        assertThat(dto.getToCurrency()).isEqualTo("RSD");
    }

    @Test
    void settersUpdateAllFields() {
        CalculateExchangeResponseDto dto = new CalculateExchangeResponseDto();
        dto.setConvertedAmount(50.0);
        dto.setExchangeRate(2.5);
        dto.setFromCurrency("USD");
        dto.setToCurrency("EUR");
        assertThat(dto.getConvertedAmount()).isEqualTo(50.0);
        assertThat(dto.getExchangeRate()).isEqualTo(2.5);
        assertThat(dto.getFromCurrency()).isEqualTo("USD");
        assertThat(dto.getToCurrency()).isEqualTo("EUR");
    }
}
