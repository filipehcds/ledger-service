-- Insert operation types (only if they don't exist)
MERGE INTO operations_types (operation_type_id, description) KEY(operation_type_id) VALUES (1, 'PURCHASE');
MERGE INTO operations_types (operation_type_id, description) KEY(operation_type_id) VALUES (2, 'INSTALLMENT PURCHASE');
MERGE INTO operations_types (operation_type_id, description) KEY(operation_type_id) VALUES (3, 'WITHDRAWAL');
MERGE INTO operations_types (operation_type_id, description) KEY(operation_type_id) VALUES (4, 'PAYMENT');
