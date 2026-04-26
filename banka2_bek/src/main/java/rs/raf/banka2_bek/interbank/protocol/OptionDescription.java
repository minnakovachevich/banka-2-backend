package rs.raf.banka2_bek.interbank.protocol;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Spec ref: protokol §2.7.2 Options
 *
 * Opcioni ugovor formiran kroz prihvatanje OTC ponude (§3.6.1).
 * negotiationId — ID OTC pregovora koji je rodio opciju (osigurava da se
 * pseudo-account TxAccount.OPTION nalazi kod prodavca).
 *
 * INVARIJANTE:
 *  - amount mora biti integer > 0
 *  - settlementDate u ISO8601 sa zonom (§2.4)
 *  - posle settlementDate-a opcija nije izvrsiva; banka prodavca otpusta
 *    rezervaciju i markira opciju kao iskoriscenu
 */
public record OptionDescription(
        ForeignBankId negotiationId,
        StockDescription stock,
        MonetaryValue pricePerUnit,
        OffsetDateTime settlementDate,
        BigDecimal amount
) {}
