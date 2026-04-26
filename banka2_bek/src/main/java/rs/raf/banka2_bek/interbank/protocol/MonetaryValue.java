package rs.raf.banka2_bek.interbank.protocol;

import java.math.BigDecimal;

/**
 * Spec ref: protokol §2.5 Monetary values
 *
 * KRITICNO: amount je BigDecimal (ne float64) — protokol eksplicitno zabranjuje
 * IEEE754 interpretaciju iako se serijalizuje kao JSON broj (ECMA-404).
 */
public record MonetaryValue(
        CurrencyCode currency,
        BigDecimal amount
) {}
