package io.pismo.ledger.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.pismo.ledger.domain.entity.Account;

public record AccountResponse(
        @JsonProperty("account_id")
        Long accountId,

        @JsonProperty("document_number")
        String documentNumber
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getAccountId(), account.getDocumentNumber());
    }
}
