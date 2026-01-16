package io.pismo.ledger.exception;

public class DuplicateDocumentException extends RuntimeException {

    public DuplicateDocumentException(String documentNumber) {
        super("Account already exists with document number: " + documentNumber);
    }
}
