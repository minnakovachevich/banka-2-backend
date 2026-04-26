package rs.raf.banka2_bek.profitbank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.raf.banka2_bek.investmentfund.dto.InvestmentFundDtos.ClientFundPositionDto;
import rs.raf.banka2_bek.investmentfund.service.InvestmentFundService;
import rs.raf.banka2_bek.profitbank.dto.ProfitBankDtos.ActuaryProfitDto;
import rs.raf.banka2_bek.profitbank.service.ActuaryProfitService;

import java.util.List;

/**
 * P6 — Spec Celina 4 (Nova) §4393-4645: Portal "Profit Banke" za supervizore.
 *
 * Dva endpointa:
 *   GET /profit-bank/actuary-performance  — spisak aktuara + profit u RSD
 *   GET /profit-bank/fund-positions       — fondovi u kojima banka ima udele
 *
 * Pristup samo supervizorima/adminima (vidi GlobalSecurityConfig).
 */
@RestController
@RequestMapping("/profit-bank")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ADMIN','SUPERVISOR')")
@RequiredArgsConstructor
public class ProfitBankController {

    private final ActuaryProfitService actuaryProfitService;
    private final InvestmentFundService investmentFundService;

    @GetMapping("/actuary-performance")
    public ResponseEntity<List<ActuaryProfitDto>> actuaryPerformance() {
        return ResponseEntity.ok(actuaryProfitService.listAllActuariesProfit());
    }

    @GetMapping("/fund-positions")
    public ResponseEntity<List<ClientFundPositionDto>> fundPositions() {
        // Banka kao klijent fonda (P9): ClientFundPosition gde je userId = ownerClientId
        // banke i userRole = "CLIENT". Implementacija u InvestmentFundService.listBankPositions
        // (jos u TODO-u — vraca prazan list dok fund flow nije dovrsen).
        try {
            return ResponseEntity.ok(investmentFundService.listBankPositions());
        } catch (UnsupportedOperationException e) {
            // InvestmentFundService.listBankPositions je jos TODO — vrati prazan list
            // umesto 500 kako FE moze da renderuje "Banka nema pozicije" placeholder.
            return ResponseEntity.ok(List.of());
        }
    }

    // TODO (opciono Celina 4 (Nova) §4585-4628):
    //   POST /profit-bank/fund-positions/{fundId}/invest  — supervizor uplata u ime banke
    //   POST /profit-bank/fund-positions/{fundId}/withdraw — supervizor povlacenje u ime banke
    //   InvestmentFundService.invest/withdraw sa userRole=CLIENT i userId=ownerClientId banke
    //   pokriva ovaj scenario; dodati nove endpointe samo ako spec eksplicitno
    //   trazi UI dugmad bez FX komisije (vec implementirano u P7).
}
