package rs.raf.banka2_bek.interbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.raf.banka2_bek.interbank.model.InterbankMessage;
import rs.raf.banka2_bek.interbank.model.InterbankMessageStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/*
================================================================================
 REPOSITORY: InterbankMessage (PROTOKOL §2.2 IDEMPOTENCY + §2.9 RETRY)
--------------------------------------------------------------------------------
 KLJUC: (sender_routing_number, locally_generated_key) — par je UNIQUE u DB,
 sluzi kao primarni nacin idempotency lookup-a.

 RETRY: scheduler trazi sve PENDING outbound poruke ciji je lastAttemptAt
 stariji od cutoff-a.
================================================================================
*/
public interface InterbankMessageRepository extends JpaRepository<InterbankMessage, Long> {

    /**
     * §2.2 idempotency — pri prijemu poruke proveravamo da li smo je vec videli
     * (cache hit -> vrati cached responseBody).
     */
    Optional<InterbankMessage> findBySenderRoutingNumberAndLocallyGeneratedKey(
            int senderRoutingNumber, String locallyGeneratedKey);

    /**
     * Za audit trail: sve poruke vezane za jednu transakciju.
     */
    List<InterbankMessage> findByTransactionIdOrderByCreatedAtAsc(String transactionId);

    /**
     * Za InterbankRetryScheduler — sve outbound poruke koje cekaju retry.
     */
    @Query("select m from InterbankMessage m " +
            "where m.status = :status " +
            "and (m.lastAttemptAt is null or m.lastAttemptAt < :cutoff)")
    List<InterbankMessage> findPendingForRetry(@Param("status") InterbankMessageStatus status,
                                                @Param("cutoff") LocalDateTime cutoff);
}
