package rs.raf.banka2_bek.exchange.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ExchangeRateDtoTest {

    @Test
    void noArgsConstructorDefaults() {
        ExchangeRateDto dto = new ExchangeRateDto();
        assertThat(dto.getCurrency()).isNull();
        assertThat(dto.getRate()).isEqualTo(0.0);
        assertThat(dto.getBuyRate()).isEqualTo(0.0);
        assertThat(dto.getSellRate()).isEqualTo(0.0);
        assertThat(dto.getMiddleRate()).isEqualTo(0.0);
        assertThat(dto.getDate()).isNull();
    }

    @Test
    void twoArgConstructorComputesRates() {
        ExchangeRateDto dto = new ExchangeRateDto("EUR", 117.5);
        assertThat(dto.getCurrency()).isEqualTo("EUR");
        assertThat(dto.getRate()).isEqualTo(117.5);
        assertThat(dto.getMiddleRate()).isEqualTo(117.5);
        assertThat(dto.getBuyRate()).isEqualTo(Math.round(117.5 * 0.98 * 1000000.0) / 1000000.0);
        assertThat(dto.getSellRate()).isEqualTo(Math.round(117.5 * 1.02 * 1000000.0) / 1000000.0);
        assertThat(dto.getDate()).isEqualTo(LocalDate.now().toString());
    }

    @Test
    void settersUpdateAllFields() {
        ExchangeRateDto dto = new ExchangeRateDto();
        dto.setCurrency("USD");
        dto.setRate(100.0);
        dto.setBuyRate(98.0);
        dto.setSellRate(102.0);
        dto.setMiddleRate(100.0);
        dto.setDate("2026-04-13");
        assertThat(dto.getCurrency()).isEqualTo("USD");
        assertThat(dto.getRate()).isEqualTo(100.0);
        assertThat(dto.getBuyRate()).isEqualTo(98.0);
        assertThat(dto.getSellRate()).isEqualTo(102.0);
        assertThat(dto.getMiddleRate()).isEqualTo(100.0);
        assertThat(dto.getDate()).isEqualTo("2026-04-13");
    }
}
