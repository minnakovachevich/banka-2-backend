package rs.raf.banka2_bek.interbank.protocol;

import java.math.BigDecimal;

/**
 * Spec ref: protokol §2.8.1 Postings
 *
 * Jedna linija u double-entry knjizenju. Pozitivan amount = debit (povecava
 * sredstva na racunu); negativan amount = kredit (smanjuje sredstva).
 *
 * BigDecimal (ne float64) za preciznost — vidi MonetaryValue note.
 */
public record Posting(
        TxAccount account,
        BigDecimal amount,
        Asset asset
) {}
