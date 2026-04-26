package rs.raf.banka2_bek.interbank.protocol;

/**
 * Spec ref: protokol §2.7.3 Stock — sve banke moraju imati identican skup
 * tickera (jedan eksterni izvor podataka).
 */
public record StockDescription(
        String ticker
) {}
