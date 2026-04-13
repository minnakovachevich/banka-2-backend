package rs.raf.banka2_bek.account.model;

import org.junit.jupiter.api.Test;
import rs.raf.banka2_bek.client.model.Client;
import rs.raf.banka2_bek.company.model.Company;

import static org.assertj.core.api.Assertions.assertThat;

class AccountModelTest {

    @Test
    void isOwnerValid_clientOnly_true() {
        Account a = Account.builder().client(new Client()).company(null).build();
        assertThat(a.isOwnerValid()).isTrue();
    }

    @Test
    void isOwnerValid_companyOnly_true() {
        Account a = Account.builder().client(null).company(new Company()).build();
        assertThat(a.isOwnerValid()).isTrue();
    }

    @Test
    void isOwnerValid_bothNull_false() {
        Account a = Account.builder().client(null).company(null).build();
        assertThat(a.isOwnerValid()).isFalse();
    }

    @Test
    void isOwnerValid_bothSet_false() {
        Account a = Account.builder().client(new Client()).company(new Company()).build();
        assertThat(a.isOwnerValid()).isFalse();
    }

    @Test
    void builderAndSetters_smoke() {
        Account a = Account.builder()
                .accountNumber("123456789012345678")
                .accountType(AccountType.CHECKING)
                .status(AccountStatus.ACTIVE)
                .accountCategory(AccountCategory.CLIENT)
                .build();
        a.setName("Moj racun");
        assertThat(a.getAccountNumber()).isEqualTo("123456789012345678");
        assertThat(a.getName()).isEqualTo("Moj racun");
        assertThat(a.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }
}
