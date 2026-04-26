package rs.raf.banka2_bek.investmentfund.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/*
================================================================================
 P4 — TODO: AUTO-LIKVIDACIJA HARTIJA FONDA (Spec Celina 4 (Nova) §3070-3102)
 Zaduzen: BE tim
--------------------------------------------------------------------------------
 KONTEKST:
   Ako klijent (ili supervizor u ime banke) trazi povlacenje iz fonda, a
   fund.account.balance < amount (RSD), ne mozemo odmah isplatiti. Spec:
   "vrsi se automatska likvidacija dovoljnog broja hartija i klijent dobija
    obavestenje da ce u kratkom roku dobiti isplatu".

 KOMPLETAN FLOW (treba implementirati):

  1. InvestmentFundService.withdraw(...) detektuje:
        if (fund.account.balanceRsd < requestedAmount) {
            tx.status = PENDING;
            fundLiquidationService.liquidateFor(fundId, deficitRsd);
        }
     Zatim vraca tx u PENDING — klijent dobija notifikaciju "Hartije se
     prodaju, isplata uskoro".

  2. FundLiquidationService.liquidateFor(fundId, neededRsd):
     - Ucitaj sve Portfolio holdings sa userRole='FUND', userId=fundId
       (NAPOMENA: trebace nam novi UserRole.FUND ili dogovoriti se da li
        koristimo userRole='FUND' kao stringovni discriminator. Spec ne
        zahteva eksplicitno — bitno je samo da fond ima portfolio nalik
        klijentskom)
     - Sortiraj po RSD vrednosti (listing.price * portfolio.quantity konv. u RSD), DESC
     - Strategija "najveci first" (manje ordera, brze isplate):
         while (preostalo > 0 && holdings nije prazan) {
             holding = top-of-list
             k = ceil(preostalo / (price * 1.01))  // 1% bafer za price drift
             k = min(k, holding.quantity)
             createSellOrder(fundId, listingId, k, MARKET)
             preostalo -= price * k konv. u RSD
         }
     - Alternativa: proporcionalna prodaja (1-2% svakog holdinga) — manje
       impacta ali vise ordera.

  3. OrderExecutionService.executeSingleOrder kad fill-a SELL order tipa
     FUND, mora pozvati FundLiquidationService.onFillCompleted(orderId):
       - Iznos prebaci sa banka.bankin_racun -> fund.account
       - Iznos je vec u valuti listinga -> konvertuj u RSD bez komisije
         (jer je interna fond transakcija)
     POSLE TOGA:
       - Pokusaj resolve-a PENDING ClientFundTransaction sa fundId
         (FIFO po createdAt). Ako fund.account.balanceRsd >= tx.amountRsd:
           debit fund, credit klijent racun (sa eventualnom konverzijom +
           1% FX komisije za klijente, 0% za supervizore — vidi P7)
           tx.status = COMPLETED
           NotificationService.send(klijent, "Vasa isplata iz fonda X je gotova")

  4. NOTIFIKACIJA:
     - Pri liquidateFor: PushNotification + email klijentu sa porukom
       "Likvidacija u toku, ocekivano vreme isporuke 24h" (ili sl.)
     - Pri onFillCompleted (svaki fill): "Likvidacija je napredovala — X% gotovo"
     - Pri completed: "Sredstva su prebacena na racun Y"

 ZAVISNOSTI (do tada NE pokretati):
   - Portfolio entitet treba podrzati userRole='FUND' (trenutno samo
     'CLIENT'/'EMPLOYEE'). Cek dogovor sa BE timom oko nazivanja.
   - OrderServiceImpl mora dozvoljavati order.userRole='FUND' i kreirati
     ga kroz interno API (ne kroz POST /orders od strane klijenta) —
     fundLiquidationService.createSellOrder helper koji bypass-uje OTP
     i validacije balansa (banka prodaje svoje hartije).
   - OrderExecutionService.executeSingleOrder treba prepoznati userRole=FUND
     i pozvati onFillCompleted hook (event listener).

 ALTERNATIVA (jednostavnija, prihvatljiva za prvu iteraciju):
   Sinhrono: ako fond nema dovoljno cash-a, vrati 409 Conflict sa porukom
   "Fond trenutno nema dovoljno likvidnih sredstava — pokusajte sutra
    nakon scheduliranog rebalans-a." Sve dok auto-likvidacija nije gotova,
   supervizori rucno pokrecu prodaje preko CreateOrderPage sa fundId
   selektorom (P3 je vec implementiran).

 TESTOVI (kad se implementira):
   - liquidateFor sa dovoljno holdinga -> kreira tacno N ordera
   - liquidateFor sa nedovoljno holdinga -> baca IllegalState
   - onFillCompleted akumulira fond.account; resolve-uje PENDING tx FIFO
   - delimicna isplata: ako prvi fill ne pokriva amount, tx ostaje PENDING
================================================================================
*/
@Service
public class FundLiquidationService {

    // TODO: injectovati OrderService, PortfolioRepository, ListingRepository,
    //   ClientFundTransactionRepository, AccountRepository, NotificationService,
    //   CurrencyConversionService

    @Transactional
    public void liquidateFor(Long fundId, BigDecimal amountRsd) {
        // TODO §P4 — vidi blok iznad za detaljan flow
        throw new UnsupportedOperationException("TODO P4: implementirati liquidateFor — vidi TODO blok iznad");
    }

    @Transactional
    public void onFillCompleted(Long orderId) {
        // TODO §P4 — proveri da li je order.userRole=FUND;
        //   ako da, prebaci prihod na fund.account i pokusaj da resolve-ujes
        //   neku PENDING ClientFundTransaction-u (FIFO po createdAt)
        throw new UnsupportedOperationException("TODO P4: implementirati onFillCompleted — vidi TODO blok iznad");
    }
}
