package rs.raf.banka2_bek.account.model;

import org.junit.jupiter.api.Test;
import rs.raf.banka2_bek.currency.model.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AccountRequestTest {

    @Test
    void noArgsConstructor_nullDefaults() {
        AccountRequest r = new AccountRequest();
        assertThat(r.getId()).isNull();
        assertThat(r.getStatus()).isNull();
        assertThat(r.getCreatedAt()).isNull();
    }

    @Test
    void allArgsAndBuilder_setsFields() {
        Currency c = new Currency();
        LocalDateTime now = LocalDateTime.now();
        AccountRequest r = AccountRequest.builder()
                .id(1L)
                .accountType(AccountType.CHECKING)
                .accountSubtype(AccountSubtype.PERSONAL)
                .currency(c)
                .initialDeposit(new BigDecimal("1000"))
                .createCard(true)
                .clientEmail("a@b.com")
                .clientName("Ime Prezime")
                .status("PENDING")
                .rejectionReason(null)
                .createdAt(now)
                .processedAt(now)
                .processedBy("emp@banka.rs")
                .build();

        assertThat(r.getId()).isEqualTo(1L);
        assertThat(r.getAccountType()).isEqualTo(AccountType.CHECKING);
        assertThat(r.getAccountSubtype()).isEqualTo(AccountSubtype.PERSONAL);
        assertThat(r.getCurrency()).isSameAs(c);
        assertThat(r.getInitialDeposit()).isEqualByComparingTo("1000");
        assertThat(r.getCreateCard()).isTrue();
        assertThat(r.getClientEmail()).isEqualTo("a@b.com");
        assertThat(r.getClientName()).isEqualTo("Ime Prezime");
        assertThat(r.getStatus()).isEqualTo("PENDING");
        assertThat(r.getRejectionReason()).isNull();
        assertThat(r.getCreatedAt()).isEqualTo(now);
        assertThat(r.getProcessedAt()).isEqualTo(now);
        assertThat(r.getProcessedBy()).isEqualTo("emp@banka.rs");
    }

    @Test
    void settersViaLombok() {
        AccountRequest r = new AccountRequest();
        r.setStatus("APPROVED");
        r.setRejectionReason("ok");
        r.setClientEmail("x@y.z");
        r.setClientName("X Y");
        r.setAccountType(AccountType.FOREIGN);
        assertThat(r.getStatus()).isEqualTo("APPROVED");
        assertThat(r.getRejectionReason()).isEqualTo("ok");
        assertThat(r.getAccountType()).isEqualTo(AccountType.FOREIGN);
    }

    @Test
    void onCreate_setsCreatedAtAndDefaultStatus() {
        AccountRequest r = new AccountRequest();
        r.onCreate();
        assertThat(r.getCreatedAt()).isNotNull();
        assertThat(r.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void onCreate_preservesExistingStatus() {
        AccountRequest r = new AccountRequest();
        r.setStatus("APPROVED");
        r.onCreate();
        assertThat(r.getStatus()).isEqualTo("APPROVED");
    }

    @Test
    void allArgsConstructor_direct() {
        AccountRequest r = new AccountRequest(
                2L, AccountType.CHECKING, AccountSubtype.PERSONAL, new Currency(),
                new BigDecimal("500"), false, "e@e.e", "N N", "PENDING",
                null, LocalDateTime.now(), null, null);
        assertThat(r.getId()).isEqualTo(2L);
        assertThat(r.getCreateCard()).isFalse();
    }
}
