-- Suporte a linhagem de recorrência
ALTER TABLE fin_transactions ADD COLUMN parent_transaction_id UUID;
ALTER TABLE fin_transactions ADD CONSTRAINT fk_parent_transaction FOREIGN KEY (parent_transaction_id) REFERENCES fin_transactions (id);

-- Índice para busca rápida de filhos de uma recorrência
CREATE INDEX idx_fin_transaction_parent ON fin_transactions (parent_transaction_id);
