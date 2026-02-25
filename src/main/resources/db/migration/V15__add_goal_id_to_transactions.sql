-- V15__add_goal_id_to_transactions.sql
-- Adiciona a coluna goal_id à tabela de transações para vincular aportes e resgates diretamente às metas.

ALTER TABLE fin_transactions ADD COLUMN goal_id UUID;

-- Criação de índice para performance em buscas vinculadas a metas
CREATE INDEX idx_fin_transaction_goal ON fin_transactions (goal_id);

COMMENT ON COLUMN fin_transactions.goal_id IS 'ID da meta que originou esta transação de alocação ou resgate';
