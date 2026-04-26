package rs.raf.banka2_bek.interbank.protocol;

/**
 * Spec ref: protokol §2.7.1 Monetary assets — wrapper oko valute, koristi se
 * u Asset variantu MONAS (sredstvo na valutnom racunu).
 */
public record MonetaryAsset(
        CurrencyCode currency
) {}
