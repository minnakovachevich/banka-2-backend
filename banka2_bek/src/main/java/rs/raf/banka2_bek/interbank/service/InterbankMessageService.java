package rs.raf.banka2_bek.interbank.service;

import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
import org.springframework.stereotype.Service;
import rs.raf.banka2_bek.interbank.model.InterbankMessage;
import rs.raf.banka2_bek.interbank.model.InterbankMessageDirection;
import rs.raf.banka2_bek.interbank.model.InterbankMessageStatus;
import rs.raf.banka2_bek.interbank.protocol.IdempotenceKey;
import rs.raf.banka2_bek.interbank.protocol.MessageType;
import rs.raf.banka2_bek.interbank.repository.InterbankMessageRepository;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

/*
================================================================================
 TODO — IDEMPOTENCY + MESSAGE LOG (PROTOKOL §2.2, §2.9, §2.11)
 Zaduzen: BE tim
 Spec ref: protokol §2.2 Idempotence keys, §2.9 Message exchange,
           §2.11 Sending messages (response caching)
--------------------------------------------------------------------------------
 KRITICNO PRAVILO (§2.2):
   "Banka MORA pratiti idempotence kljuceve zauvijek."
   Pri primanju poruke sa key-em koji je vec videjen, mora vratiti ISTI
   odgovor kao pri prvom prijemu — atomicno sa lokalnom transakcijom.

 OBAVEZNE METODE:

 INBOUND (mi smo primalac):

 1. Optional<String> findCachedResponse(IdempotenceKey key);
    Pretraga po (key.routingNumber, key.locallyGeneratedKey).
    Ako postoji: vrati cached response body (kao JSON string).
    Idempotency check pre svake operacije.

 2. void recordInboundResponse(IdempotenceKey key, MessageType messageType,
                                String requestBody, Integer httpStatus,
                                String responseBody);
    Upisi i request i response u istom redu, ATOMICNO sa biznis logikom
    (npr. prepareLocal). Ako se transakcija rollback-uje, brise se i ovaj
    zapis — pri retry-u poruka se obradjuje cisto.

 OUTBOUND (mi saljemo):

 3. IdempotenceKey generateKey();
    Vrati novu IdempotenceKey{ourRoutingNumber, UUID().toString()}.

 4. InterbankMessage recordOutbound(IdempotenceKey key, int targetRouting,
                                     MessageType type, String body);
    U lokalnoj transakciji koja je formirala poruku — upise zapis u
    message log sa status=PENDING. RetryScheduler ce ga pokupiti.

 5. void markOutboundSent(IdempotenceKey key, Integer httpStatus,
                           String responseBody);
    Posle uspesnog slanja: status=SENT, sacuvaj http status + telo
    odgovora (za audit). Samo za 200/204; 202 ostavi PENDING (retry kasnije).

 6. void markOutboundFailed(IdempotenceKey key, String errorMessage);
    Network greska / 4xx / 5xx — status ostane PENDING ali increment
    retryCount + lastError. Posle MAX_RETRY -> STUCK.

 NAPOMENA O TRANSAKCIONALNOSTI:
  Prema §2.2: idempotency lookup + biznis logika moraju ici u JEDNOJ lokalnoj
  transakciji. Spring @Transactional(propagation = REQUIRED) na pozivajucem
  servisu (TransactionExecutorService.handleNewTx itd.) je dovoljno.

 INDEX U BAZI:
  - (sender_routing_number, locally_generated_key) UNIQUE INDEX — za fast
    duplicate detection
  - (status, last_attempt_at) INDEX — za retry scheduler

 LIMITS:
  - locally_generated_key max 64 bajta (po protokolu §2.2)
  - InterbankMessage.idempotenceKey kolona: VARCHAR(64) + routing_number INT
================================================================================
*/
@Service
public class InterbankMessageService {

    private final InterbankMessageRepository repository;
    private final BankRoutingService bankRoutingService;

    public InterbankMessageService(InterbankMessageRepository repository, BankRoutingService bankRoutingService) {
        this.repository = repository;
        this.bankRoutingService = bankRoutingService;
    }

    public Optional<String> findCachedResponse(IdempotenceKey key) {
        // TODO: §2.2 lookup po (key.routingNumber, key.locallyGeneratedKey)

        Optional<InterbankMessage> messageOpt = repository.findBySenderRoutingNumberAndLocallyGeneratedKey(
                key.routingNumber(),
                key.locallyGeneratedKey()
        );
        return messageOpt.map(interbankMessage -> interbankMessage.getDirection().toString());
    }

    public void recordInboundResponse(IdempotenceKey key,
                                      MessageType messageType,
                                      String requestBody,
                                      Integer httpStatus,
                                      String responseBody) {
        // TODO: §2.2 upis (key + request + response + status) atomicno


        throw new UnsupportedOperationException("TODO: implementirati recordInboundResponse");
    }

    public IdempotenceKey generateKey() {

        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        StringBuilder sb = new StringBuilder(64);
        for(byte b : bytes) sb.append(String.format("%02x", b));
        return new IdempotenceKey(bankRoutingService.myRoutingNumber(), sb.toString());
    }

    public InterbankMessage recordOutbound(IdempotenceKey key,
                                            int targetRouting,
                                            MessageType type,
                                            String body) {
        // TODO: upis u message log sa status=PENDING
        throw new UnsupportedOperationException("TODO: implementirati recordOutbound");
    }

    public void markOutboundSent(IdempotenceKey key, Integer httpStatus, String responseBody) {
        // TODO: status=SENT (samo za 200/204; 202 ostavi PENDING)
        throw new UnsupportedOperationException("TODO: implementirati markOutboundSent");
    }

    public void markOutboundFailed(IdempotenceKey key, String errorMessage) {
        // TODO: increment retryCount + lastError; posle MAX_RETRY -> STUCK
        throw new UnsupportedOperationException("TODO: implementirati markOutboundFailed");
    }


}
