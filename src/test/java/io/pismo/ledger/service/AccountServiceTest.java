package io.pismo.ledger.service;

import io.pismo.ledger.domain.entity.Account;
import io.pismo.ledger.dto.request.CreateAccountRequest;
import io.pismo.ledger.exception.AccountNotFoundException;
import io.pismo.ledger.exception.DuplicateDocumentException;
import io.pismo.ledger.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("Should create account successfully")
    void shouldCreateAccountSuccessfully() {
        var request = new CreateAccountRequest("12345678900");
        var savedAccount = new Account("12345678900");
        savedAccount.setAccountId(1L);

        when(accountRepository.existsByDocumentNumber("12345678900")).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        var response = accountService.createAccount(request);

        assertThat(response.accountId()).isEqualTo(1L);
        assertThat(response.documentNumber()).isEqualTo("12345678900");
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw exception when document number already exists")
    void shouldThrowExceptionWhenDocumentNumberAlreadyExists() {
        var request = new CreateAccountRequest("12345678900");

        when(accountRepository.existsByDocumentNumber("12345678900")).thenReturn(true);

        assertThatThrownBy(() -> accountService.createAccount(request))
                .isInstanceOf(DuplicateDocumentException.class)
                .hasMessage("Account already exists with document number: 12345678900");
    }

    @Test
    @DisplayName("Should get account successfully")
    void shouldGetAccountSuccessfully() {
        var account = new Account("12345678900");
        account.setAccountId(1L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        var response = accountService.getAccount(1L);

        assertThat(response.accountId()).isEqualTo(1L);
        assertThat(response.documentNumber()).isEqualTo("12345678900");
    }

    @Test
    @DisplayName("Should throw exception when account not found")
    void shouldThrowExceptionWhenAccountNotFound() {
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccount(999L))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage("Account not found with id: 999");
    }
}
