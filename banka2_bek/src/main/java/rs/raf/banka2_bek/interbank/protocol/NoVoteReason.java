package rs.raf.banka2_bek.interbank.protocol;

/**
 * Spec ref: protokol §2.12.1 NEW_TX messages — NoVoteReason variants.
 *
 * Razlozi za NO glas pri verifikaciji NEW_TX (§2.8.6). Svaki razlog moze
 * referencirati posting koji je izazvao failure (osim UNBALANCED_TX).
 *
 * Vise od jednog razloga moze biti prisutno u TransactionVote.reasons.
 */
public record NoVoteReason(
        Reason reason,
        Posting posting
) {

    public enum Reason {
        UNBALANCED_TX,
        NO_SUCH_ACCOUNT,
        NO_SUCH_ASSET,
        UNACCEPTABLE_ASSET,
        INSUFFICIENT_ASSET,
        OPTION_AMOUNT_INCORRECT,
        OPTION_USED_OR_EXPIRED,
        OPTION_NEGOTIATION_NOT_FOUND
    }
}
