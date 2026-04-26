package rs.raf.banka2_bek.interbank.service;

import org.springframework.stereotype.Service;
import rs.raf.banka2_bek.interbank.config.InterbankProperties;

import java.util.Optional;

/*
================================================================================
 TODO — ROUTING NUMBER RESOLUTION (PROTOKOL §2.1)
 Zaduzen: BE tim
 Spec ref: protokol §2.1 Bank identification — "Account brojevi pocinju sa
           tri cifre koje identifikuju banku. Te tri cifre se zovu routing
           numbers."
--------------------------------------------------------------------------------
 SVRHA:
 Za dato accountNumber, otkrij da li je nas (myRoutingNumber prefix) ili
 koja partnerska banka. Koristi se u:
  - PaymentController.createPayment: ako je receiver iz druge banke,
    redirektuj na TransactionExecutorService umesto obicnog transfer flow-a
  - TransactionExecutorService: parsira sve TxAccount.Account.num iz Postings
    da identifikuje sve ucesnice i zato ko se promovira u koordinatora

 METODE:
   int myRoutingNumber()
       Vraca routing number nase banke iz properties.

   int parseRoutingNumber(String accountNumber)
       Iz accountNumber izvuce prve 3 cifre kao int. IllegalArgumentException
       ako accountNumber nije parse-abilan.

   boolean isLocalAccount(String accountNumber)
       True ako parseRoutingNumber == myRoutingNumber.

   Optional<PartnerBank> resolvePartner(String accountNumber)
       Trazi partnera ciji se routingNumber poklapa.

   Optional<PartnerBank> resolvePartnerByRouting(int routingNumber)
       Direct lookup po vec parsiranom routing number-u (koristi se za
       Message receivers gde routingNumber dolazi iz Transaction.transactionId).

 EDGE CASES:
  - accountNumber null/krati od 3 cifre -> IllegalArgumentException
  - accountNumber sa nepoznatim prefixom -> Optional.empty()
    (caller: vrati 404 / NoVoteReason.NO_SUCH_ACCOUNT)
================================================================================
*/
@Service
public class BankRoutingService {

    private final InterbankProperties properties;

    public BankRoutingService(InterbankProperties properties) {
        this.properties = properties;
    }

    public int myRoutingNumber() {
        // TODO: validacija da properties.myRoutingNumber nije null
        throw new UnsupportedOperationException("TODO: implementirati BankRoutingService.myRoutingNumber");
    }

    public int parseRoutingNumber(String accountNumber) {
        // TODO: uzmi prve 3 cifre, parseInt
        throw new UnsupportedOperationException("TODO: implementirati BankRoutingService.parseRoutingNumber");
    }

    public boolean isLocalAccount(String accountNumber) {
        // TODO: parseRoutingNumber + uporedi sa myRoutingNumber
        throw new UnsupportedOperationException("TODO: implementirati BankRoutingService.isLocalAccount");
    }

    public Optional<InterbankProperties.PartnerBank> resolvePartner(String accountNumber) {
        // TODO: parseRoutingNumber + delegate na resolvePartnerByRouting
        throw new UnsupportedOperationException("TODO: implementirati BankRoutingService.resolvePartner");
    }

    public Optional<InterbankProperties.PartnerBank> resolvePartnerByRouting(int routingNumber) {
        // TODO: properties.partners.stream().filter(p -> p.routingNumber == routingNumber).findFirst()
        throw new UnsupportedOperationException("TODO: implementirati BankRoutingService.resolvePartnerByRouting");
    }
}
