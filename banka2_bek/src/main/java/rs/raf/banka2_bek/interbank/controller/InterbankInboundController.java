package rs.raf.banka2_bek.interbank.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.raf.banka2_bek.interbank.protocol.Message;

/*
================================================================================
 TODO — INBOUND ENDPOINT ZA PORUKE OD DRUGIH BANAKA (PROTOKOL §2.11)
 Zaduzen: BE tim
 Spec ref: protokol §2.11 Sending messages, §2.10 Authentication,
           §2.12 Message types
--------------------------------------------------------------------------------
 JEDINSTVENA TACKA ZA SVE INBOUND PORUKE (po protokolu):
   POST /interbank
   Content-Type: application/json
   X-Api-Key: <token koji smo MI izdali toj banci>

 STATUSI ODGOVORA (§2.11):
   202 Accepted — primljeno ali jos neobradeno; pošiljač retry-uje
   200 OK       — primljeno + zavrseno; body = response (npr. TransactionVote
                  za NEW_TX)
   204 No Content — primljeno + zavrseno bez tela
   401 — los X-Api-Key
   400 — malformed envelope
   500 — internal errors

 AUTHENTICATION (§2.10):
   - Procitaj X-Api-Key header
   - Provera u BankRoutingService da postoji partner sa tim inboundToken-om
   - Dodatno: envelope.idempotenceKey.routingNumber MORA biti routingNumber
     tog partnera (sprecava CSRF iz druge banke)

 IDEMPOTENCY (§2.2):
   - InterbankMessageService.findCachedResponse(envelope.idempotenceKey)
   - Ako postoji: vrati cached response sa istim httpStatus-om (200/204)
   - Ako ne: izvrsi handler, pa pozovi recordInboundResponse atomicno

 DISPATCH PO TIPU (§2.12):
   NEW_TX      -> TransactionExecutorService.handleNewTx → vrati TransactionVote (200)
   COMMIT_TX   -> TransactionExecutorService.handleCommitTx → 204
   ROLLBACK_TX -> TransactionExecutorService.handleRollbackTx → 204

 NAPOMENA:
   Endpoint-ovi za public-stock, /negotiations/* i /user/* idu u
   OtcNegotiationController (§3.1-3.7), NE ovde. Ovaj kontroler je samo
   za §2 Transaction execution protocol.
================================================================================
*/
@RestController
@RequestMapping("/interbank")
public class InterbankInboundController {

    // TODO: injectovati TransactionExecutorService, InterbankMessageService,
    //   BankRoutingService, ObjectMapper

    /**
     * Glavni endpoint po §2.11. Body je Message<Type> envelope sa
     * idempotenceKey + messageType + message (Transaction / CommitTransaction
     * / RollbackTransaction).
     *
     * Vraca:
     *  - 200 OK + TransactionVote za NEW_TX
     *  - 204 No Content za COMMIT_TX, ROLLBACK_TX
     *  - 202 Accepted ako poruka nije jos obradena (npr. backoff)
     */
    @PostMapping
    public ResponseEntity<Object> receiveMessage(
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @RequestBody Message<Object> envelope) {
        // TODO:
        //  1. §2.10 — provera X-Api-Key + envelope.idempotenceKey.routingNumber
        //  2. §2.2 — InterbankMessageService.findCachedResponse → return cache hit
        //  3. §2.12 dispatch po envelope.messageType:
        //     case NEW_TX:      cast envelope.message → Transaction →
        //                       TransactionExecutorService.handleNewTx
        //                       → TransactionVote → 200
        //     case COMMIT_TX:   cast → CommitTransaction →
        //                       TransactionExecutorService.handleCommitTx → 204
        //     case ROLLBACK_TX: cast → RollbackTransaction →
        //                       TransactionExecutorService.handleRollbackTx → 204
        //  4. Atomicno sa biznis logikom: InterbankMessageService.recordInboundResponse
        //  5. Network/IO greske -> 500; biznis -> 200 sa NO glasom (ne 5xx)
        throw new UnsupportedOperationException("TODO: implementirati POST /interbank");
    }
}
