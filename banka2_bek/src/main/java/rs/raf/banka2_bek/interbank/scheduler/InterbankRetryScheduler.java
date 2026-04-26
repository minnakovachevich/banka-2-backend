package rs.raf.banka2_bek.interbank.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/*
================================================================================
 TODO — RETRY PETLJA ZA NEPOTVRDJENE PORUKE (PROTOKOL §2.9)
 Zaduzen: BE tim
 Spec ref: protokol §2.9 Message exchange — "Svaka poruka mora biti retry-ovana
           dok se ne prizna" (at-most-once preko idempotence kljuceva)
--------------------------------------------------------------------------------
 FLOW:
  Svaka 2 minuta proveravamo InterbankMessage gde je status=PENDING i
  poslednji pokusaj stariji od interval praga. Za svaku poruku:
   1. Ako retryCount >= maxRetries:
      - status=STUCK, log ERROR (supervizor treba intervenciju)
      - Lokalna transakcija ostaje u PREPARED (rezervisana sredstva); manualna
        akcija ili supervisor MARK STUCK -> ROLLBACK lokalno
   2. Inace:
      - InterbankClient.sendMessage(routingNumber, type, envelope, responseType)
      - 200/204 -> markOutboundSent (status=SENT)
      - 202     -> ostani PENDING (legitimno cekanje)
      - 4xx/5xx/network -> markOutboundFailed (retryCount++)
      - 401     -> auth issue, skip retry, log ERROR

 IDEMPOTENCY (§2.2):
  Idempotence key se ZADRZAVA pri retry-u. Druga banka pri ponovnom
  prijemu vraca isti odgovor (cache hit u InterbankMessageService).

 KONFIGURACIJA:
   interbank.retry.interval-seconds=30
   interbank.retry.max-retries=10
   interbank.retry.stuck-timeout-minutes=30

 TESTOVI:
  - Retry se ne dešava pre interval praga
  - Max retries -> STUCK + log
  - 202 ne uvecava retryCount, ostaje PENDING
  - Uspesan retry oslobadja iz pending-a
  - Idempotency: ponovljeni response je cache-iran kod druge banke
================================================================================
*/
@Component
public class InterbankRetryScheduler {

    // TODO: injectovati InterbankMessageRepository, InterbankClient, InterbankMessageService

    /**
     * Cron svaka 2 minuta. Snizi na 30s ako hoces brzi reagovanje
     * (i azuriraj interbank.retry.interval-seconds u skladu).
     */
    @Scheduled(fixedRate = 120_000)
    public void retryStaleMessages() {
        // TODO:
        //  1. messageRepo.findPendingForRetry(now - intervalSeconds)
        //  2. za svaku:
        //     - if retryCount >= maxRetries: markOutboundFailed → STUCK
        //     - else: client.sendMessage(...) + recordovati ishod
        //  3. Atomicno per-poruka (osim send-a) — ne blokiraj druge poruke
    }
}
