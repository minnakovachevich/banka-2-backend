package rs.raf.banka2_bek.interbank.protocol;

/**
 * Spec ref: protokol §2.12 Message types
 *
 * Tri tipa medjubankarskih poruka. Sve ostalo (placanja, OTC exercise) se
 * kodira kroz Postings na Transaction-u — nema posebnih poruka za rezervaciju
 * akcija ili commit funds-a.
 */
public enum MessageType {
    NEW_TX,
    COMMIT_TX,
    ROLLBACK_TX
}
