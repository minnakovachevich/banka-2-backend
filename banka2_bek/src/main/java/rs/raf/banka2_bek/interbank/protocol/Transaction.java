package rs.raf.banka2_bek.interbank.protocol;

import java.util.List;

/**
 * Spec ref: protokol §2.8.2 Transaction objects + §2.8.3 Transaction formation
 *
 * Uravnotezena (sum debita == sum kredita) lista postingova + metadata.
 * Inicijator dodaje transactionId koji sluzi za korelaciju kroz NEW_TX /
 * COMMIT_TX / ROLLBACK_TX poruke.
 *
 * INVARIJANTE:
 *  - postings ne moze biti prazan
 *  - SUM postings.amount po asset-u mora biti 0 (balanced)
 *  - transactionId.routingNumber == nas routing number (mi smo inicijator)
 */
public record Transaction(
        List<Posting> postings,
        ForeignBankId transactionId,
        String message,
        String callNumber,
        String paymentCode,
        String paymentPurpose
) {}
