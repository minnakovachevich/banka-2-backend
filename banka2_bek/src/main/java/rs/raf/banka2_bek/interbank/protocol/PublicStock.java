package rs.raf.banka2_bek.interbank.protocol;

import java.math.BigDecimal;
import java.util.List;

/**
 * Spec ref: protokol §3.1 Fetching public stocks.
 *
 * Stavka iz odgovora na GET /public-stock — jedna akcija + lista prodavaca u
 * banci sa kolicinama. Kupac koristi ovaj feed da bi inicirao OTC pregovor.
 */
public record PublicStock(
        StockDescription stock,
        List<Seller> sellers
) {

    public record Seller(
            ForeignBankId seller,
            BigDecimal amount
    ) {}
}
