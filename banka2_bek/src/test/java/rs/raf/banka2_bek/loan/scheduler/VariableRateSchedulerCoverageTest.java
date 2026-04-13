package rs.raf.banka2_bek.loan.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import rs.raf.banka2_bek.loan.model.InterestType;
import rs.raf.banka2_bek.loan.model.Loan;
import rs.raf.banka2_bek.loan.model.LoanInstallment;
import rs.raf.banka2_bek.loan.model.LoanStatus;
import rs.raf.banka2_bek.loan.model.LoanType;
import rs.raf.banka2_bek.loan.repository.LoanInstallmentRepository;
import rs.raf.banka2_bek.loan.repository.LoanRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Coverage test targeting remaining missed margin branches in VariableRateScheduler.
 * Specifically exercises REFINANCING loan type which was not exercised in the existing test.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VariableRateSchedulerCoverageTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanInstallmentRepository installmentRepository;

    @InjectMocks
    private VariableRateScheduler scheduler;

    @Test
    @DisplayName("processes REFINANCING variable-rate loan (margin 1.00)")
    void processesRefinancingLoan() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setLoanNumber("VAR-REF");
        loan.setLoanType(LoanType.REFINANCING);
        loan.setInterestType(InterestType.VARIABLE);
        loan.setNominalRate(new BigDecimal("5.00"));
        loan.setEffectiveRate(new BigDecimal("6.00"));
        loan.setMonthlyPayment(new BigDecimal("2500.0000"));
        loan.setRemainingDebt(BigDecimal.valueOf(60000));
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setAmount(BigDecimal.valueOf(100000));
        loan.setRepaymentPeriod(36);
        loan.setStartDate(LocalDate.now().minusMonths(6));
        loan.setEndDate(LocalDate.now().plusMonths(30));

        LoanInstallment unpaid = new LoanInstallment();
        unpaid.setId(1L);
        unpaid.setLoan(loan);
        unpaid.setPaid(false);
        unpaid.setAmount(new BigDecimal("2500.0000"));
        unpaid.setInterestRate(loan.getEffectiveRate());
        unpaid.setExpectedDueDate(LocalDate.now().plusMonths(1));

        when(loanRepository.findByInterestTypeAndStatusIn(any(), any())).thenReturn(List.of(loan));
        when(installmentRepository.findByLoanIdOrderByExpectedDueDateAsc(1L)).thenReturn(List.of(unpaid));

        scheduler.adjustVariableRates();

        ArgumentCaptor<Loan> captor = ArgumentCaptor.forClass(Loan.class);
        verify(loanRepository).save(captor.capture());
        Loan saved = captor.getValue();

        // effectiveRate = nominal(5.00) + offset([-1.5, 1.5]) + margin(REFINANCING=1.00)
        // Range: [4.50, 7.50], all >= 0.50 floor
        assertThat(saved.getEffectiveRate()).isGreaterThanOrEqualTo(new BigDecimal("4.50"));
        assertThat(saved.getEffectiveRate()).isLessThanOrEqualTo(new BigDecimal("7.50"));
        verify(installmentRepository).save(unpaid);
    }
}
