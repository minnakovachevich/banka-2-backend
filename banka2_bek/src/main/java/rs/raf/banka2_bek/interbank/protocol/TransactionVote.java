package rs.raf.banka2_bek.interbank.protocol;

import java.util.List;

/**
 * Spec ref: protokol §2.12.1 NEW_TX messages — odgovor na NEW_TX.
 *
 * Ako vote == YES, reasons je null/prazna.
 * Ako vote == NO, reasons sadrzi bar 1 NoVoteReason (moze i vise).
 */
public record TransactionVote(
        Vote vote,
        List<NoVoteReason> reasons
) {

    public enum Vote {
        YES,
        NO
    }
}
