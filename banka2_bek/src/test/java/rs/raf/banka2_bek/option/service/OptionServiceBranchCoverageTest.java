package rs.raf.banka2_bek.option.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import rs.raf.banka2_bek.account.repository.AccountRepository;
import rs.raf.banka2_bek.actuary.repository.ActuaryInfoRepository;
import rs.raf.banka2_bek.employee.model.Employee;
import rs.raf.banka2_bek.employee.repository.EmployeeRepository;
import rs.raf.banka2_bek.option.repository.OptionRepository;
import rs.raf.banka2_bek.portfolio.repository.PortfolioRepository;
import rs.raf.banka2_bek.stock.repository.ListingRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OptionServiceBranchCoverageTest {

    @Mock OptionRepository optionRepository;
    @Mock ListingRepository listingRepository;
    @Mock EmployeeRepository employeeRepository;
    @Mock ActuaryInfoRepository actuaryInfoRepository;
    @Mock PortfolioRepository portfolioRepository;
    @Mock AccountRepository accountRepository;

    OptionService service;

    @Test
    void ensureUserCanExerciseOptions_permissionsNull_notActuary_throws() throws Exception {
        // L275 uncovered: employee.getPermissions() == null branch
        service = new OptionService(
                optionRepository, listingRepository, employeeRepository,
                actuaryInfoRepository, accountRepository, portfolioRepository,
                "BANK-REG"
        );

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setEmail("e@test.com");
        employee.setActive(true);
        employee.setPermissions(null);
        when(employeeRepository.findByEmail("e@test.com")).thenReturn(Optional.of(employee));
        when(actuaryInfoRepository.findByEmployeeId(1L)).thenReturn(Optional.empty());

        var method = OptionService.class.getDeclaredMethod("ensureUserCanExerciseOptions", String.class);
        method.setAccessible(true);

        assertThatThrownBy(() -> {
            try {
                method.invoke(service, "e@test.com");
            } catch (java.lang.reflect.InvocationTargetException ex) {
                throw ex.getCause();
            }
        }).hasMessageContaining("aktuar");
    }

    @Test
    void getBankAccount_notFound_throws() throws Exception {
        // L264 partial: orElseThrow lambda
        service = new OptionService(
                optionRepository, listingRepository, employeeRepository,
                actuaryInfoRepository, accountRepository, portfolioRepository,
                "BANK-REG"
        );
        when(accountRepository.findBankAccountByCurrency("BANK-REG", "USD"))
                .thenReturn(Optional.empty());

        var method = OptionService.class.getDeclaredMethod("getBankAccount");
        method.setAccessible(true);
        assertThatThrownBy(() -> {
            try {
                method.invoke(service);
            } catch (java.lang.reflect.InvocationTargetException ex) {
                throw ex.getCause();
            }
        }).isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Bank USD account not found");
    }
}
