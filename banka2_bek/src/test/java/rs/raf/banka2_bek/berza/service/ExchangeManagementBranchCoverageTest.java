package rs.raf.banka2_bek.berza.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import rs.raf.banka2_bek.berza.dto.ExchangeDto;
import rs.raf.banka2_bek.berza.model.Exchange;
import rs.raf.banka2_bek.berza.repository.ExchangeRepository;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Targets remaining missed branches/instructions in ExchangeManagementService:
 *  - L67  isNonTradingDay: exchange.getHolidays() == null branch
 *  - L156 calculateNextOpenTime: while loop exit branch (best-effort; intrinsically loop-bound)
 *  - L159 calculateNextOpenTime: exchange.getHolidays() == null branch
 *  - L238 toDto: closeTime == null branch
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExchangeManagementBranchCoverageTest {

    @Mock private ExchangeRepository exchangeRepository;

    private ExchangeManagementService service;

    @BeforeEach
    void setUp() {
        service = new ExchangeManagementService(exchangeRepository);
    }

    private Exchange exchangeWithHolidaysNull(LocalTime open, LocalTime close) {
        return Exchange.builder()
                .id(1L)
                .name("Test Exchange")
                .acronym("TST")
                .micCode("XTST")
                .country("US")
                .currency("USD")
                .timeZone("America/New_York")
                .openTime(open)
                .closeTime(close)
                .testMode(false)
                .active(true)
                .holidays(null) // critical: null holidays for L67/L159 branch
                .build();
    }

    // ---------- L67: isNonTradingDay with holidays == null ----------
    @Test
    void isNonTradingDay_withNullHolidays_returnsFalse() throws Exception {
        Exchange e = exchangeWithHolidaysNull(LocalTime.of(9, 30), LocalTime.of(16, 0));

        // Pick a known weekday (Monday)
        ZonedDateTime weekday = ZonedDateTime.of(2026, 4, 13, 12, 0, 0, 0, ZoneId.of("America/New_York"));

        Method m = ExchangeManagementService.class.getDeclaredMethod(
                "isNonTradingDay", ZonedDateTime.class, Exchange.class);
        m.setAccessible(true);
        boolean result = (boolean) m.invoke(service, weekday, e);
        assertThat(result).isFalse();
    }

    // ---------- L159: calculateNextOpenTime with holidays == null ----------
    @Test
    void calculateNextOpenTime_withNullHolidays_returnsIsoString() throws Exception {
        Exchange e = exchangeWithHolidaysNull(LocalTime.of(9, 30), LocalTime.of(16, 0));

        Method m = ExchangeManagementService.class.getDeclaredMethod(
                "calculateNextOpenTime", Exchange.class);
        m.setAccessible(true);
        Object result = m.invoke(service, e);
        // Will return an ISO timestamp (not null since openTime is set)
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(String.class);
    }

    // ---------- L238: toDto with closeTime == null ----------
    // Uses public getByAcronym → toDto. testMode=true short-circuits isExchangeOpen so a null
    // closeTime cannot crash isWithinTradingHours. The toDto null-check ternary then takes the
    // null branch on closeTime.
    @Test
    void toDto_closeTimeNull_returnsNullCloseTime() {
        Exchange e = Exchange.builder()
                .id(2L)
                .name("Null-Close")
                .acronym("NUL")
                .micCode("XNUL")
                .country("US")
                .currency("USD")
                .timeZone("America/New_York")
                .openTime(LocalTime.of(9, 30))
                .closeTime(null) // L238 false branch
                .testMode(true)  // makes isExchangeOpen return true → no nextOpen calc, no NPE
                .active(true)
                .holidays(new HashSet<>())
                .build();
        when(exchangeRepository.findByAcronym("NUL")).thenReturn(Optional.of(e));

        ExchangeDto dto = service.getByAcronym("NUL");
        assertThat(dto).isNotNull();
        assertThat(dto.getCloseTime()).isNull();
        assertThat(dto.getOpenTime()).isEqualTo("09:30");
    }

    // ---------- L156: while (safetyCounter < 365) loop-exhaustion branch ----------
    // Fabricate an exchange where every weekday for the next 365 days is a holiday,
    // so the loop never breaks and exits via the false-branch on the while condition.
    @Test
    void calculateNextOpenTime_safetyCounterExhausted_returnsResult() throws Exception {
        Set<LocalDate> holidays = new HashSet<>();
        LocalDate start = LocalDate.now().plusDays(1);
        for (int i = 0; i < 400; i++) {
            holidays.add(start.plusDays(i));
        }
        Exchange e = Exchange.builder()
                .id(3L)
                .name("Always Closed")
                .acronym("CLO")
                .micCode("XCLO")
                .country("US")
                .currency("USD")
                .timeZone("America/New_York")
                .openTime(LocalTime.of(9, 30))
                .closeTime(LocalTime.of(16, 0))
                .testMode(false)
                .active(true)
                .holidays(holidays)
                .build();

        Method m = ExchangeManagementService.class.getDeclaredMethod(
                "calculateNextOpenTime", Exchange.class);
        m.setAccessible(true);
        Object result = m.invoke(service, e);
        assertThat(result).isNotNull();
    }
}
