package io.pismo.ledger.service;

import io.pismo.ledger.domain.entity.Transaction;
import io.pismo.ledger.dto.request.CreateTransactionRequest;
import io.pismo.ledger.dto.response.TransactionResponse;
import io.pismo.ledger.exception.AccountNotFoundException;
import io.pismo.ledger.exception.OperationTypeNotFoundException;
import io.pismo.ledger.repository.AccountRepository;
import io.pismo.ledger.repository.OperationTypeRepository;
import io.pismo.ledger.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final OperationTypeRepository operationTypeRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              OperationTypeRepository operationTypeRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.operationTypeRepository = operationTypeRepository;
    }

    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        log.info("Creating transaction for account: {}, operation type: {}, amount: {}",
                request.accountId(), request.operationTypeId(), request.amount());

        var account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new AccountNotFoundException(request.accountId()));

        var operationType = operationTypeRepository.findById(request.operationTypeId())
                .orElseThrow(() -> new OperationTypeNotFoundException(request.operationTypeId()));

        BigDecimal effectiveAmount = calculateEffectiveAmount(request.amount(), operationType.isDebit());

        var transaction = new Transaction(account, operationType, effectiveAmount);
        transaction = transactionRepository.save(transaction);

        log.info("Transaction created successfully with id: {}", transaction.getTransactionId());
        return TransactionResponse.from(transaction);
    }

    /**
     * Calculates the effective amount based on the operation type.
     * Debit operations (purchase, installment purchase, withdrawal) are stored as negative values.
     * Credit operations (payment) are stored as positive values.
     */
    private BigDecimal calculateEffectiveAmount(BigDecimal amount, boolean isDebit) {
        return isDebit ? amount.negate() : amount;
    }
}
