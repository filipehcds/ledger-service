CREATE TABLE IF NOT EXISTS accounts (
    account_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_number VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS operations_types (
    operation_type_id INT PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    operation_type_id INT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    event_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES accounts(account_id),
    CONSTRAINT fk_transaction_operation_type FOREIGN KEY (operation_type_id) REFERENCES operations_types(operation_type_id)
);

CREATE INDEX IF NOT EXISTS idx_transactions_account_id ON transactions(account_id);
CREATE INDEX IF NOT EXISTS idx_accounts_document_number ON accounts(document_number);
