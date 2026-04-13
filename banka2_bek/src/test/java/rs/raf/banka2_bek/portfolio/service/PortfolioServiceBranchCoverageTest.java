package rs.raf.banka2_bek.portfolio.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import rs.raf.banka2_bek.account.repository.AccountRepository;
import rs.raf.banka2_bek.client.repository.ClientRepository;
import rs.raf.banka2_bek.employee.repository.EmployeeRepository;
import rs.raf.banka2_bek.order.repository.OrderRepository;
import rs.raf.banka2_bek.portfolio.repository.PortfolioRepository;
import rs.raf.banka2_bek.stock.repository.ListingRepository;
import rs.raf.banka2_bek.tax.repository.TaxRecordRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PortfolioServiceBranchCoverageTest {

    @Mock PortfolioRepository portfolioRepository;
    @Mock OrderRepository orderRepository;
    @Mock ListingRepository listingRepository;
    @Mock TaxRecordRepository taxRecordRepository;
    @Mock ClientRepository clientRepository;
    @Mock EmployeeRepository employeeRepository;
    @Mock AccountRepository accountRepository;

    @InjectMocks PortfolioService service;

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void isEmployee_authorityNotRoleEmployeeOrAdmin_returnsFalse() throws Exception {
        // L158 uncovered branch: authority nije ni ROLE_EMPLOYEE ni ROLE_ADMIN
        var token = new UsernamePasswordAuthenticationToken(
                "client@test.com", null, List.of(new SimpleGrantedAuthority("ROLE_CLIENT")));
        SecurityContextHolder.getContext().setAuthentication(token);

        var method = PortfolioService.class.getDeclaredMethod("isEmployee");
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(service);
        assertThat(result).isFalse();
    }
}
