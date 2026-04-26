package rs.raf.banka2_bek.interbank.protocol;

/**
 * Spec ref: protokol §2.2 Idempotence keys
 *
 * Obavezno polje na svakoj poruci izmedju banaka. Banka koja salje generise
 * locallyGeneratedKey (max 64 bajta), banka koja prima ga trajno belezi i
 * vraca isti odgovor pri retry-u (at-most-once semantika preko §2.9).
 */
public record IdempotenceKey(
        int routingNumber,
        String locallyGeneratedKey
) {}
