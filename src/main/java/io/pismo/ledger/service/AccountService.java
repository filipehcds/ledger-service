package io.pismo.ledger.service;

import io.pismo.ledger.domain.entity.Account;
import io.pismo.ledger.dto.request.CreateAccountRequest;
import io.pismo.ledger.dto.response.AccountResponse;
import io.pismo.ledger.exception.AccountNotFoundException;
import io.pismo.ledger.exception.DuplicateDocumentException;
import io.pismo.ledger.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        log.info("Creating account with document number: {}", request.documentNumber());

        if (accountRepository.existsByDocumentNumber(request.documentNumber())) {
            throw new DuplicateDocumentException(request.documentNumber());
        }

        var account = new Account(request.documentNumber());
        account = accountRepository.save(account);

        log.info("Account created successfully with id: {}", account.getAccountId());
        return AccountResponse.from(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long accountId) {
        log.info("Retrieving account with id: {}", accountId);

        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        return AccountResponse.from(account);
    }
}
