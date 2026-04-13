package rs.raf.banka2_bek.payment.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import rs.raf.banka2_bek.client.model.Client;
import rs.raf.banka2_bek.client.repository.ClientRepository;
import rs.raf.banka2_bek.payment.dto.PaymentRecipientResponseDto;
import rs.raf.banka2_bek.payment.service.PaymentRecipientService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Targets uncovered lines in PaymentRecipientController:
 *  - lines 35/36: auth==null / principal==null branch in getCurrentUserEmail
 *  - lines 44/45: isClient() catch block returning false
 */
@ExtendWith(MockitoExtension.class)
class PaymentRecipientControllerCoverageTest {

    @Mock private PaymentRecipientService paymentRecipientService;
    @Mock private ClientRepository clientRepository;

    @InjectMocks private PaymentRecipientController controller;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createPaymentRecipient_throwsWhenNoAuthentication() {
        // POST path calls getCurrentUserEmail directly (not via isClient),
        // so the IllegalStateException propagates — exercising lines 35/36.
        SecurityContextHolder.clearContext();

        rs.raf.banka2_bek.payment.dto.CreatePaymentRecipientRequestDto req =
                new rs.raf.banka2_bek.payment.dto.CreatePaymentRecipientRequestDto();
        req.setName("X");
        req.setAccountNumber("123");

        assertThatThrownBy(() -> controller.createPaymentRecipient(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not authenticated");
    }

    @Test
    void deletePaymentRecipient_throwsWhenPrincipalNull() {
        // auth present but principal==null → second half of lines 35/36
        org.springframework.security.authentication.TestingAuthenticationToken token =
                new org.springframework.security.authentication.TestingAuthenticationToken(null, null);
        token.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(token);

        assertThatThrownBy(() -> controller.deletePaymentRecipient(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not authenticated");
    }

    @Test
    void getPaymentRecipients_returnsEmptyPageWhenRepositoryThrows() {
        // isClient() catch branch — repository throws, returns false → empty page
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                        "broken@x", "x", List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );
        SecurityContextHolder.getContext().setAuthentication(token);

        when(clientRepository.findByEmail("broken@x"))
                .thenThrow(new RuntimeException("DB down"));

        ResponseEntity<Page<PaymentRecipientResponseDto>> resp = controller.getPaymentRecipients(0, 10);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().isEmpty()).isTrue();
    }

    @Test
    void getPaymentRecipients_returnsEmptyPageWhenNotClient() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                        "employee@x", "x", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))
        );
        SecurityContextHolder.getContext().setAuthentication(token);

        when(clientRepository.findByEmail("employee@x")).thenReturn(Optional.empty());

        ResponseEntity<Page<PaymentRecipientResponseDto>> resp = controller.getPaymentRecipients(0, 10);
        assertThat(resp.getBody().isEmpty()).isTrue();
    }
}
