package rs.raf.banka2_bek.interbank.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.raf.banka2_bek.interbank.protocol.ForeignBankId;
import rs.raf.banka2_bek.interbank.protocol.OtcNegotiation;
import rs.raf.banka2_bek.interbank.protocol.OtcOffer;
import rs.raf.banka2_bek.interbank.protocol.PublicStock;
import rs.raf.banka2_bek.interbank.protocol.UserInformation;

import java.util.List;

/*
================================================================================
 TODO — OTC NEGOTIATION + USER INFO ENDPOINT-i (PROTOKOL §3.1-3.7)
 Zaduzen: BE tim
 Spec ref: protokol §3 OTC negotiation protocol
--------------------------------------------------------------------------------
 ENDPOINT-i (sve auth-ovani sa X-Api-Key, §2.10):

   GET  /public-stock                              — §3.1, lista javnih akcija
   POST /negotiations                              — §3.2, kreiraj pregovor
   GET  /negotiations/{routingNumber}/{id}         — §3.4, citaj pregovor
   PUT  /negotiations/{routingNumber}/{id}         — §3.3, counter-offer
   DELETE /negotiations/{routingNumber}/{id}       — §3.5, zatvori pregovor
   GET  /negotiations/{routingNumber}/{id}/accept  — §3.6, prihvati ponudu
   GET  /user/{routingNumber}/{id}                 — §3.7, friendly name

 NAPOMENA O CALLER-IMA:
   Sve endpointe poziva DRUGA banka, ne nas FE. Nas FE poziva nase interne
   API-je (npr. /api/otc/...) koji onda krose ka ovim endpoint-ima preko
   InterbankClient-a.

 RESPONSES:
   GET /public-stock                           -> 200 PublicStock[]
   POST /negotiations                          -> 200 ForeignBankId
   GET /negotiations/{rn}/{id}                 -> 200 OtcNegotiation, 404 ako ne postoji
   PUT /negotiations/{rn}/{id}                 -> 204 No Content, 409 ako nije nas red ili zatvoren
   DELETE /negotiations/{rn}/{id}              -> 204 No Content
   GET /negotiations/{rn}/{id}/accept          -> 200 (sinhrono — ceka COMMITTED)
   GET /user/{rn}/{id}                         -> 200 UserInformation, 404 ako ne postoji
================================================================================
*/
@RestController
public class OtcNegotiationController {

    // TODO: injectovati OtcNegotiationService, BankRoutingService

    @GetMapping("/public-stock")
    public ResponseEntity<List<PublicStock>> listPublicStocks(
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey) {
        // TODO: §3.1 + §2.10 auth
        throw new UnsupportedOperationException("TODO: §3.1 GET /public-stock");
    }

    @PostMapping("/negotiations")
    public ResponseEntity<ForeignBankId> createNegotiation(
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @RequestBody OtcOffer offer) {
        // TODO: §3.2 — auth, validacija (sellerId.routingNumber == nas), kreiraj
        // local entity, vrati ForeignBankId(myRouting, generatedId)
        throw new UnsupportedOperationException("TODO: §3.2 POST /negotiations");
    }

    @GetMapping("/negotiations/{routingNumber}/{id}")
    public ResponseEntity<OtcNegotiation> readNegotiation(
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @PathVariable int routingNumber,
            @PathVariable String id) {
        // TODO: §3.4 — auth, lookup po (routingNumber, id), vrati OtcNegotiation
        throw new UnsupportedOperationException("TODO: §3.4 GET /negotiations/{rn}/{id}");
    }

    @PutMapping("/negotiations/{routingNumber}/{id}")
    public ResponseEntity<Void> postCounterOffer(
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @PathVariable int routingNumber,
            @PathVariable String id,
            @RequestBody OtcOffer offer) {
        // TODO: §3.3 — auth, provera turn-a (lastModifiedBy != caller-cija banka),
        // 409 ako nije nas red, update local entity
        throw new UnsupportedOperationException("TODO: §3.3 PUT /negotiations/{rn}/{id}");
    }

    @DeleteMapping("/negotiations/{routingNumber}/{id}")
    public ResponseEntity<Void> closeNegotiation(
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @PathVariable int routingNumber,
            @PathVariable String id) {
        // TODO: §3.5 — auth, postavi isOngoing=false
        throw new UnsupportedOperationException("TODO: §3.5 DELETE /negotiations/{rn}/{id}");
    }

    @GetMapping("/negotiations/{routingNumber}/{id}/accept")
    public ResponseEntity<Void> acceptNegotiation(
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @PathVariable int routingNumber,
            @PathVariable String id) {
        // TODO: §3.6 — auth, OtcNegotiationService.acceptReceivedNegotiation
        // (sinhrono — vraca tek kad je transakcija COMMITTED, ili NO glas → error)
        // §3.6.1 forma optionContract i salje TransactionExecutorService.execute
        throw new UnsupportedOperationException("TODO: §3.6 GET /negotiations/{rn}/{id}/accept");
    }

    @GetMapping("/user/{routingNumber}/{id}")
    public ResponseEntity<UserInformation> getUserInfo(
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @PathVariable int routingNumber,
            @PathVariable String id) {
        // TODO: §3.7 — auth, OtcNegotiationService.serveUserInfo
        // Ako routingNumber != nas → 404 (mi nismo autoritativni)
        throw new UnsupportedOperationException("TODO: §3.7 GET /user/{rn}/{id}");
    }
}
