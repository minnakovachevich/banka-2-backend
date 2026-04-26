package rs.raf.banka2_bek.interbank.protocol;

/**
 * Spec ref: protokol §2.7.1 Monetary assets — fiksiran skup valuta dozvoljenih
 * u medjubankarskim porukama (sve banke moraju da znaju isti skup).
 */
public enum CurrencyCode {
    RSD,
    EUR,
    USD,
    CHF,
    JPY,
    AUD,
    CAD,
    GBP
}
