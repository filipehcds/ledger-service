package io.pismo.ledger.service;

import io.pismo.ledger.domain.entity.Account;
import io.pismo.ledger.domain.entity.OperationType;
import io.pismo.ledger.domain.entity.Transaction;
import io.pismo.ledger.dto.request.CreateTransactionRequest;
import io.pismo.ledger.exception.AccountNotFoundException;
import io.pismo.ledger.exception.OperationTypeNotFoundException;
import io.pismo.ledger.repository.AccountRepository;
import io.pismo.ledger.repository.OperationTypeRepository;
import io.pismo.ledger.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private OperationTypeRepository operationTypeRepository;

    @InjectMocks
    private TransactionService transactionService;

    @ParameterizedTest
    @CsvSource({
            "1, PURCHASE, 50.0, -50.0",
            "2, INSTALLMENT PURCHASE, 23.5, -23.5",
            "3, WITHDRAWAL, 18.7, -18.7",
            "4, PAYMENT, 60.0, 60.0"
    })
    @DisplayName("Should create transaction with correct amount sign based on operation type")
    void shouldCreateTransactionWithCorrectAmountSign(int operationTypeId, String description,
                                                       String inputAmount, String storedAmount) {
        var account = new Account("12345678900");
        account.setAccountId(1L);

        var operationType = new OperationType(operationTypeId, description);

        var savedTransaction = new Transaction(account, operationType, new BigDecimal(storedAmount));
        savedTransaction.setTransactionId(1L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(operationTypeRepository.findById(operationTypeId)).thenReturn(Optional.of(operationType));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        var request = new CreateTransactionRequest(1L, operationTypeId, new BigDecimal(inputAmount));
        var response = transactionService.createTransaction(request);

        // Response should return positive amount (absolute value)
        assertThat(response.amount()).isEqualByComparingTo(new BigDecimal(inputAmount));

        // But stored amount should be negative for debit operations
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        assertThat(transactionCaptor.getValue().getAmount()).isEqualByComparingTo(new BigDecimal(storedAmount));
    }

    @Test
    @DisplayName("Should throw exception when account not found")
    void shouldThrowExceptionWhenAccountNotFound() {
        var request = new CreateTransactionRequest(999L, 1, new BigDecimal("50.0"));

        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage("Account not found with id: 999");
    }

    @Test
    @DisplayName("Should throw exception when operation type not found")
    void shouldThrowExceptionWhenOperationTypeNotFound() {
        var account = new Account("12345678900");
        account.setAccountId(1L);

        var request = new CreateTransactionRequest(1L, 99, new BigDecimal("50.0"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(operationTypeRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(OperationTypeNotFoundException.class)
                .hasMessage("Operation type not found with id: 99");
    }
}
