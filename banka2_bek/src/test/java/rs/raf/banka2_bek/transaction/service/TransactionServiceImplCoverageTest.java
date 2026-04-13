package rs.raf.banka2_bek.transaction.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import rs.raf.banka2_bek.account.model.Account;
import rs.raf.banka2_bek.client.model.Client;
import rs.raf.banka2_bek.client.repository.ClientRepository;
import rs.raf.banka2_bek.currency.model.Currency;
import rs.raf.banka2_bek.payment.model.Payment;
import rs.raf.banka2_bek.transaction.dto.TransactionDirection;
import rs.raf.banka2_bek.transaction.dto.TransactionListItemDto;
import rs.raf.banka2_bek.transaction.dto.TransactionResponseDto;
import rs.raf.banka2_bek.transaction.dto.TransactionType;
import rs.raf.banka2_bek.transaction.model.Transaction;
import rs.raf.banka2_bek.transaction.repository.TransactionRepository;
import rs.raf.banka2_bek.transaction.service.implementation.TransactionServiceImpl;
import rs.raf.banka2_bek.transfers.model.Transfer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Targets TransactionServiceImpl uncovered branches:
 *  - line 95: type==null filter branch in getTransactions overload
 *  - lines 140/141: toListItem with null account / null payment
 *  - lines 151/153: resolveDirection null/zero debit → INCOMING
 *  - line 158: getAuthenticatedUser null authentication
 *  - line 172: orZero null branch
 *  - line 179: resolveToAccountNumber transfer branch
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceImplCoverageTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private ClientRepository clientRepository;

    @InjectMocks private TransactionServiceImpl service;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authAs(String email) {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(email, "x",
                        List.of(new SimpleGrantedAuthority("ROLE_CLIENT")));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    // ---------- line 158: not authenticated ----------
    @Test
    void getTransactions_throwsWhenUnauthenticated() {
        SecurityContextHolder.clearContext();
        assertThatThrownBy(() -> service.getTransactions(PageRequest.of(0, 10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Authenticated user");
    }

    @Test
    void getTransactions_throwsWhenPrincipalNotUserDetails() {
        TestingAuthenticationToken token = new TestingAuthenticationToken("stringPrincipal", null,
                List.of(new SimpleGrantedAuthority("ROLE_CLIENT")));
        token.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(token);

        assertThatThrownBy(() -> service.getTransactions(PageRequest.of(0, 10)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getTransactions_throwsWhenUserNotInRepo() {
        authAs("missing@x");
        when(clientRepository.findByEmail("missing@x")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getTransactions(PageRequest.of(0, 10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not exist");
    }

    // ---------- line 95: type==null branch + toListItem mapping ----------
    @Test
    void getTransactions_filtered_nullType_mapsToListItems() {
        Client client = new Client(); client.setId(1L);
        when(clientRepository.findByEmail("c@x")).thenReturn(Optional.of(client));
        authAs("c@x");

        // Transaction with null account, null payment (TRANSFER), null debit (INCOMING)
        Transfer transfer = new Transfer();
        Account toAcc = new Account(); toAcc.setAccountNumber("TO-ACC");
        transfer.setToAccount(toAcc);

        Transaction tx = Transaction.builder()
                .id(1L)
                .account(null)      // line 140 null account branch
                .payment(null)      // line 141 null payment branch
                .transfer(transfer) // line 179: transfer non-null + toAccount non-null
                .debit(null)        // line 151/153 + 172 null debit → INCOMING
                .credit(new BigDecimal("5"))
                .build();

        when(transactionRepository.findTransactionsByAccountUserIdAndFilters(
                eq(1L), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(tx)));

        Page<TransactionListItemDto> result = service.getTransactions(
                PageRequest.of(0, 10), null, null, null, null, null);
        assertThat(result.getContent()).hasSize(1);
        TransactionListItemDto dto = result.getContent().get(0);
        assertThat(dto.getAccountNumber()).isNull();
        assertThat(dto.getType()).isEqualTo(TransactionType.TRANSFER);
        assertThat(dto.getDirection()).isEqualTo(TransactionDirection.INCOMING);
    }

    @Test
    void getTransactions_filtered_explicitTypeIsPassedThrough() {
        Client client = new Client(); client.setId(1L);
        when(clientRepository.findByEmail("c@x")).thenReturn(Optional.of(client));
        authAs("c@x");

        when(transactionRepository.findTransactionsByAccountUserIdAndFilters(
                eq(1L), any(), any(), any(), any(), eq("PAYMENT"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        Page<TransactionListItemDto> result = service.getTransactions(
                PageRequest.of(0, 10), null, null, null, null, TransactionType.PAYMENT);
        assertThat(result.getContent()).isEmpty();
    }

    // ---------- line 179: transfer with null toAccount ----------
    @Test
    void getTransactionById_transferWithoutToAccount_mapsWithNullToAccountNumber() {
        Currency c = new Currency(); c.setCode("EUR");
        Transfer transfer = new Transfer(); // null toAccount

        Transaction tx = Transaction.builder()
                .id(2L)
                .account(null)
                .currency(c)
                .payment(null)
                .transfer(transfer)
                .debit(new BigDecimal("1"))
                .credit(BigDecimal.ZERO)
                .build();

        when(transactionRepository.findById(2L)).thenReturn(Optional.of(tx));

        TransactionResponseDto dto = service.getTransactionById(2L);
        assertThat(dto.getType()).isEqualTo(TransactionType.TRANSFER);
        assertThat(dto.getToAccountNumber()).isNull();
    }

    @Test
    void getTransactionById_paymentBranch_toAccountNumberFromPayment() {
        Payment p = Payment.builder().id(9L).toAccountNumber("PAY-TO").build();
        Transaction tx = Transaction.builder()
                .id(3L)
                .payment(p)
                .debit(new BigDecimal("10"))
                .credit(BigDecimal.ZERO)
                .build();

        when(transactionRepository.findById(3L)).thenReturn(Optional.of(tx));

        TransactionResponseDto dto = service.getTransactionById(3L);
        assertThat(dto.getType()).isEqualTo(TransactionType.PAYMENT);
        assertThat(dto.getToAccountNumber()).isEqualTo("PAY-TO");
    }

    @Test
    void getTransactionById_noPaymentNoTransfer_returnsNullToAccount() {
        Transaction tx = Transaction.builder()
                .id(4L)
                .payment(null)
                .transfer(null)
                .debit(new BigDecimal("10"))
                .credit(BigDecimal.ZERO)
                .build();

        when(transactionRepository.findById(4L)).thenReturn(Optional.of(tx));

        TransactionResponseDto dto = service.getTransactionById(4L);
        assertThat(dto.getToAccountNumber()).isNull();
    }
}
