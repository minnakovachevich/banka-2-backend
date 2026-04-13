package rs.raf.banka2_bek.account.repository;

import org.junit.jupiter.api.Test;
import rs.raf.banka2_bek.account.model.Account;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

class AccountRepositoryDefaultMethodTest {

    @Test
    void findBankAccountByCurrencyId_returnsFirstWhenListNonEmpty() {
        AccountRepository repo = mock(AccountRepository.class, withSettings().defaultAnswer(CALLS_REAL_METHODS));
        Account a1 = new Account();
        a1.setId(1L);
        Account a2 = new Account();
        a2.setId(2L);
        doReturn(List.of(a1, a2)).when(repo).findBankAccountsByCurrencyId("111", 2L);

        Optional<Account> result = repo.findBankAccountByCurrencyId("111", 2L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void findBankAccountByCurrencyId_returnsEmptyWhenListEmpty() {
        AccountRepository repo = mock(AccountRepository.class, withSettings().defaultAnswer(CALLS_REAL_METHODS));
        doReturn(List.of()).when(repo).findBankAccountsByCurrencyId("999", 5L);

        Optional<Account> result = repo.findBankAccountByCurrencyId("999", 5L);

        assertThat(result).isEmpty();
    }
}
