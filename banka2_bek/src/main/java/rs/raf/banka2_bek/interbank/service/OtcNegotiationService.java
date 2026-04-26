package rs.raf.banka2_bek.interbank.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.raf.banka2_bek.interbank.protocol.ForeignBankId;
import rs.raf.banka2_bek.interbank.protocol.OtcNegotiation;
import rs.raf.banka2_bek.interbank.protocol.OtcOffer;
import rs.raf.banka2_bek.interbank.protocol.PublicStock;
import rs.raf.banka2_bek.interbank.protocol.UserInformation;

import java.util.List;

/*
================================================================================
 INTER-BANK OTC NEGOTIATION ENTITETI — TODO PRIPREMA:
   Postojeci `otc/model/OtcOffer` i `otc/model/OtcContract` entiteti su
   intra-bank only (buyerId, sellerId su Long ka lokalnoj clients/employees
   tabeli). Inter-bank pregovori imaju strane u DRUGOJ banci — ID je
   ForeignBankId{routingNumber, id} (§2.3), opaque za nas.

   Treba uvesti DVA NOVA ENTITETA u ovom paketu:
     interbank/model/InterbankOtcNegotiation
       - id (PK lokalni)
       - foreignNegotiationId (kompozitni: routingNumber + idString) — UNIKATNO
       - localPartyType: BUYER ili SELLER (mi smo jedna od strana)
       - localPartyId, localPartyRole (Long + String) — moja banka, moj kupac
       - foreignPartyId (ForeignBankId polja kao routingNumber + id)
       - listingId / ticker (mora se mapirati na lokalni listing)
       - quantity, pricePerStock, premium, settlementDate
       - lastModifiedBy (ForeignBankId)
       - isOngoing, status (ACTIVE/ACCEPTED/DECLINED/CLOSED)
       - createdAt, lastModifiedAt

     interbank/model/InterbankOtcContract
       - id (PK)
       - sourceNegotiationId (FK ka InterbankOtcNegotiation)
       - localPartyType (BUYER ili SELLER)
       - localPartyId, localPartyRole
       - foreignPartyId (ForeignBankId)
       - listing, quantity, strikePrice, premium, settlementDate
       - status (ACTIVE/EXERCISED/EXPIRED)
       - createdAt, exercisedAt

   NE prosirivati intra-bank entitete — namene su razlicite i mesanje bi
   pokvarilo postojeci flow.

================================================================================
 TODO — OTC NEGOTIATION (PROTOKOL §3)
 Zaduzen: BE tim
 Spec ref: A protocol for bank-to-bank asset exchange.htm, sekcije:
   §3.1 Fetching public stocks
   §3.2 Creating an OTC negotiation
   §3.3 Posting a counter-offer
   §3.4 Reading the current transaction
   §3.5 Closing negotiations
   §3.6 Accepting an offer
   §3.6.1 Forming option contracts
   §3.7 Resolving friendly names for remote IDs
--------------------------------------------------------------------------------

OUTBOUND (mi smo kupac, partner banka je seller; ili obrnuto pri counter-offer):

 1. List<PublicStock> fetchRemotePublicStocks(int routingNumber);
    GET {partnerBaseUrl}/public-stock
    - Vraca listu javnih akcija + lista prodavaca u toj banci
    - Cache: 5 min TTL (FE poziva discovery cesto)
    - **P8 ROLE FILTER (Spec Celina 5 (Nova) §840-848):** "Klijenti vide
      ponude Klijenata, Aktuari vide ponude Aktuara". Posto remote banka
      vraca sve seller-e nezavisno od role, MORAMO ovde (ili na FE-u) da
      filtriramo. Implementacija opcija:
        a) BE filter: posle dohvata, drugu banku pitamo /user/{rn}/{id} za
           svakog seller-a — sporo. NE preporucujemo.
        b) FE filter: druga banka u UserInformation dodaje "userRole" polje
           (extension protokola) — ali to nije u zicanom protokolu.
        c) Konvencija: routing+id format kodira role (npr. parno=client,
           neparno=employee) — nije robusno.
        d) Praktican kompromis: OtcOffer.lastModifiedBy.id ima prefiks
           ("C-..." ili "E-...") koji partner banka konzistentno koristi.
      ODLUKA: Cek na profesorov odgovor. Privremeno ovo NE filtriramo
      remote-side; FE prikazuje sve i obelezi da je cross-role mozda blokiran
      kad se acceptOffer pozove (server vraca 400).
    Spec: §3.1

 2. ForeignBankId createNegotiation(OtcOffer offer);
    POST {sellerBankBaseUrl}/negotiations
    Body: OtcOffer
    Response: ForeignBankId (negotiation ID, generisan kod prodavceve banke)
    - Pre slanja: rezervisi premium iznos lokalno (kupac u svojoj banci)
    Spec: §3.2

 3. void postCounterOffer(ForeignBankId negotiationId, OtcOffer updated);
    PUT {sellerBankBaseUrl}/negotiations/{rn}/{id}
    Body: OtcOffer (sa novim uslovima)
    - Mora postaviti updated.lastModifiedBy == nasi userId-eks (jer mi
      saljemo)
    - 409 ako nije nas red ili je pregovor zatvoren
    Spec: §3.3 (turn pravilo: turn je buyer ako lastModifiedBy != buyerId)

 4. OtcNegotiation readNegotiation(ForeignBankId negotiationId);
    GET {sellerBankBaseUrl}/negotiations/{rn}/{id}
    Response: OtcNegotiation (= OtcOffer + isOngoing)
    - Koristi se da kupcheva banka sinhronizuje lokalnu kopiju
    Spec: §3.4

 5. void closeNegotiation(ForeignBankId negotiationId);
    DELETE {sellerBankBaseUrl}/negotiations/{rn}/{id}
    - Bilo koja strana moze. Posle ovoga isOngoing=false
    Spec: §3.5

 6. void acceptOffer(ForeignBankId negotiationId);
    GET {sellerBankBaseUrl}/negotiations/{rn}/{id}/accept
    - Druga banka formira transakciju (videti tabelu §3.6) i prosledi je
      TransactionExecutorService.execute(...)
    - Odgovor stizu tek kad je transakcija COMMITTED
    - Ovde NE pravimo lokalnu Transaction; samo cekamo ack
    Spec: §3.6

 7. UserInformation resolveUserName(ForeignBankId userId);
    GET {bankBaseUrl}/user/{rn}/{id}
    Response: UserInformation (bankDisplayName, displayName)
    404 ako ID nije validan
    Spec: §3.7

INBOUND (mi smo seller — autoritativna kopija pregovora kod nas):

 8. List<PublicStock> serveLocalPublicStocks();
    Agregacija lokalnog Portfolio.publicQuantity > 0 grupisanih po listing-u.
    Vraca PublicStock listu — za svaki ticker, lista prodavaca u nasoj banci
    sa kolicinama.
    Spec: §3.1

 9. ForeignBankId acceptCreatedNegotiation(OtcOffer offer);
    POST handler. Validacija:
     - sellerId.routingNumber == nas (mi smo autoritativni)
     - sellerId.id postoji (klijent ili banka kao seller)
     - amount <= seller's publicQuantity - sum(active negotiations)
     - settlementDate u buducnosti
    Generisi local negotiationId (UUID).
    Save InterbankOtcNegotiation entity (zameni postojeci OtcOffer entity koji
    je intra-bank only, vidi Refactor entiteta).
    Vrati ForeignBankId{ourRoutingNumber, generatedId}.
    Spec: §3.2

 10. void receiveCounterOffer(ForeignBankId negotiationId, OtcOffer updated);
     PUT handler. Validacije:
      - negotiation postoji, isOngoing == true
      - offer.lastModifiedBy je strana cija je tura (provera sa state)
     Update local entity.
     Spec: §3.3

 11. OtcNegotiation getNegotiation(ForeignBankId negotiationId);
     GET handler. Vrati current state iz local entity.
     Spec: §3.4

 12. void closeReceivedNegotiation(ForeignBankId negotiationId);
     DELETE handler. Postavi isOngoing=false.
     Spec: §3.5

 13. void acceptReceivedNegotiation(ForeignBankId negotiationId);
     GET .../accept handler. Forma transakciju (§3.6 tabela):
       Buyer  | Credit | premium
       Seller | Debit  | premium
       Buyer  | Debit  | optionContract(O)  (jedan)
       Seller | Credit | optionContract(O)  (jedan)
     gde je optionContract(O) = OptionDescription{
       negotiationId: o.id,
       stock: o.stock,
       pricePerUnit: o.pricePerUnit,
       settlementDate: o.settlementDate,
       amount: o.amount
     }
     Posalji u TransactionExecutorService.execute(transaction).
     Cekaj COMMITTED, pa vrati 200.
     Pre commit-a: rezervisi sellerove akcije u portfoliju (§2.7.2).
     Spec: §3.6 + §3.6.1

 14. UserInformation serveUserInfo(String localUserId);
     GET handler. Vrati ime klijenta (Client.firstName + lastName) ili
     zaposlenog. 404 ako nema.
     Spec: §3.7

DEPENDENCY INJECTION (planirano):
   InterbankClient client
   BankRoutingService routing
   InterbankOtcNegotiationRepository negotiations  — novi entitet
   PortfolioRepository portfolios                  — public stocks + reservations
   ClientRepository clients                        — friendly names
   EmployeeRepository employees                    — friendly names
   TransactionExecutorService executor             — accept flow

NAPOMENA O OPCIJAMA:
 §3.6.1 forma optionContract daje OptionDescription koja ide kao Asset.OptionAsset
 u Posting. Pseudo-account TxAccount.Option(negotiationId) je kreditovan k akcijama
 i debit-ovan k*pi sredstvima — vidi §2.7.2 i §2.8.6 verifikacija.
================================================================================
*/
@Service
public class OtcNegotiationService {

    @Transactional
    public List<PublicStock> fetchRemotePublicStocks(int routingNumber) {
        throw new UnsupportedOperationException("TODO: §3.1 GET /public-stock outbound");
    }

    @Transactional
    public ForeignBankId createNegotiation(OtcOffer offer) {
        throw new UnsupportedOperationException("TODO: §3.2 POST /negotiations outbound");
    }

    @Transactional
    public void postCounterOffer(ForeignBankId negotiationId, OtcOffer updated) {
        throw new UnsupportedOperationException("TODO: §3.3 PUT /negotiations/{rn}/{id} outbound");
    }

    @Transactional
    public OtcNegotiation readNegotiation(ForeignBankId negotiationId) {
        throw new UnsupportedOperationException("TODO: §3.4 GET /negotiations/{rn}/{id} outbound");
    }

    @Transactional
    public void closeNegotiation(ForeignBankId negotiationId) {
        throw new UnsupportedOperationException("TODO: §3.5 DELETE /negotiations/{rn}/{id} outbound");
    }

    @Transactional
    public void acceptOffer(ForeignBankId negotiationId) {
        throw new UnsupportedOperationException("TODO: §3.6 GET /negotiations/{rn}/{id}/accept outbound");
    }

    @Transactional
    public UserInformation resolveUserName(ForeignBankId userId) {
        throw new UnsupportedOperationException("TODO: §3.7 GET /user/{rn}/{id} outbound");
    }

    @Transactional
    public List<PublicStock> serveLocalPublicStocks() {
        throw new UnsupportedOperationException("TODO: §3.1 inbound");
    }

    @Transactional
    public ForeignBankId acceptCreatedNegotiation(OtcOffer offer) {
        throw new UnsupportedOperationException("TODO: §3.2 inbound");
    }

    @Transactional
    public void receiveCounterOffer(ForeignBankId negotiationId, OtcOffer updated) {
        throw new UnsupportedOperationException("TODO: §3.3 inbound");
    }

    @Transactional
    public OtcNegotiation getNegotiation(ForeignBankId negotiationId) {
        throw new UnsupportedOperationException("TODO: §3.4 inbound");
    }

    @Transactional
    public void closeReceivedNegotiation(ForeignBankId negotiationId) {
        throw new UnsupportedOperationException("TODO: §3.5 inbound");
    }

    @Transactional
    public void acceptReceivedNegotiation(ForeignBankId negotiationId) {
        throw new UnsupportedOperationException("TODO: §3.6 + §3.6.1 inbound (form option contract + execute tx)");
    }

    @Transactional
    public UserInformation serveUserInfo(String localUserId) {
        throw new UnsupportedOperationException("TODO: §3.7 inbound");
    }
}
