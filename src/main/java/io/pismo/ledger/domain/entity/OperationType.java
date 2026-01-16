package io.pismo.ledger.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "operations_types")
public class OperationType {

    @Id
    @Column(name = "operation_type_id")
    private Integer operationTypeId;

    @Column(name = "description", nullable = false)
    private String description;

    public OperationType() {
    }

    public OperationType(Integer operationTypeId, String description) {
        this.operationTypeId = operationTypeId;
        this.description = description;
    }

    public Integer getOperationTypeId() {
        return operationTypeId;
    }

    public void setOperationTypeId(Integer operationTypeId) {
        this.operationTypeId = operationTypeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Determines if this operation type represents a debit (negative amount).
     * PURCHASE (1), INSTALLMENT PURCHASE (2), and WITHDRAWAL (3) are debit operations.
     */
    public boolean isDebit() {
        return operationTypeId != null && operationTypeId >= 1 && operationTypeId <= 3;
    }
}
