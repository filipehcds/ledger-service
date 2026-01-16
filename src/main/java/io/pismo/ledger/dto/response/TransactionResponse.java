package io.pismo.ledger.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.pismo.ledger.domain.entity.Transaction;
import java.math.BigDecimal;

public record TransactionResponse(
        @JsonProperty("transaction_id")
        Long transactionId,

        @JsonProperty("account_id")
        Long accountId,

        @JsonProperty("operation_type_id")
        Integer operationTypeId,

        BigDecimal amount
) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getTransactionId(),
                transaction.getAccount().getAccountId(),
                transaction.getOperationType().getOperationTypeId(),
                transaction.getAmount().abs()
        );
    }
}
