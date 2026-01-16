package io.pismo.ledger.exception;

public class OperationTypeNotFoundException extends RuntimeException {

    public OperationTypeNotFoundException(Integer operationTypeId) {
        super("Operation type not found with id: " + operationTypeId);
    }
}
