package rs.raf.banka2_bek.interbank.protocol;

/**
 * Spec ref: protokol §2.12.3 ROLLBACK_TX message body.
 *
 * Inicijalna banka salje ovo ako bilo koja banka glasa NO. Primalac otpusta
 * rezervaciju, oznaci transakciju kao failed. Odgovor je 204.
 */
public record RollbackTransaction(
        ForeignBankId transactionId
) {}
