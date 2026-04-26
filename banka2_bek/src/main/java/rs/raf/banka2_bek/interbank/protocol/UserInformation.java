package rs.raf.banka2_bek.interbank.protocol;

/**
 * Spec ref: protokol §3.7 Resolving friendly names for remote IDs.
 *
 * Odgovor na GET /user/{routingNumber}/{id}. Ako ID ne postoji, banka vraca
 * 404. Koristi se cisto za prikaz imena u UI-u (umesto opaque ID-a).
 */
public record UserInformation(
        String bankDisplayName,
        String displayName
) {}
