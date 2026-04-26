package rs.raf.banka2_bek.interbank.protocol;

/**
 * Spec ref: protokol §2.12.2 COMMIT_TX message body.
 *
 * Inicijalna banka salje ovo nakon sto su sve banke glasale YES.
 * Primalac brise rezervaciju i primenjuje sve postinge. Odgovor je 204.
 */
public record CommitTransaction(
        ForeignBankId transactionId
) {}
