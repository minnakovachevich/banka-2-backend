package rs.raf.banka2_bek.interbank.protocol;

/**
 * Spec ref: protokol §2.3 Foreign object identifiers
 *
 * Strogo: banke koje nisu vlasnik objekta MORAJU `id` tretirati kao opaque
 * (ne interpretirati). Koristi se za identifikaciju klijenata, opcija i
 * OTC pregovora preko granica banaka.
 */
public record ForeignBankId(
        int routingNumber,
        String id
) {}
