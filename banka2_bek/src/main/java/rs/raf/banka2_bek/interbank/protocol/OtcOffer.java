package rs.raf.banka2_bek.interbank.protocol;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Spec ref: protokol §3.2 Creating an OTC negotiation, §3.3 Counter-offer.
 *
 * Pregovor o opciji izmedju kupca i prodavca. Autoritativna kopija je uvek
 * kod prodavceve banke (§3.2).
 *
 * PRAVILO TURE (§3.3):
 *   turn je buyer ako lastModifiedBy != buyerId
 *   turn je seller ako lastModifiedBy != sellerId
 * Suprotna strana ne sme postavljati counter-offer dok je njen ne-red.
 */
public record OtcOffer(
        StockDescription stock,
        OffsetDateTime settlementDate,
        MonetaryValue pricePerUnit,
        MonetaryValue premium,
        ForeignBankId buyerId,
        ForeignBankId sellerId,
        BigDecimal amount,
        ForeignBankId lastModifiedBy
) {}
