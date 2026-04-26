package rs.raf.banka2_bek.interbank.protocol;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Spec ref: protokol §3.4 Reading the current transaction.
 *
 * OtcOffer + isOngoing flag. Vraca se na GET /negotiations/{routingNumber}/{id}.
 * isOngoing == false znaci da je pregovor zatvoren (§3.5 DELETE) ili prihvacen
 * (§3.6 GET .../accept).
 *
 * Java records ne podrzavaju nasledjivanje — repliciramo polja iz OtcOffer-a.
 */
public record OtcNegotiation(
        StockDescription stock,
        OffsetDateTime settlementDate,
        MonetaryValue pricePerUnit,
        MonetaryValue premium,
        ForeignBankId buyerId,
        ForeignBankId sellerId,
        BigDecimal amount,
        ForeignBankId lastModifiedBy,
        boolean isOngoing
) {}
