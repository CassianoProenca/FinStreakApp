-- Esta migração documenta a adição dos novos tipos de transação suportados pela aplicação
-- para controle de integridade de metas (Goals).

COMMENT ON COLUMN fin_transactions.type IS 'Tipo da transação: INCOME, EXPENSE, GOAL_ALLOCATION, GOAL_WITHDRAWAL';

-- Se houvesse um CHECK CONSTRAINT no V1, aqui seria o lugar de dar um ALTER TABLE para atualizá-lo.
-- Como é um VARCHAR(20), o banco aceita os novos Enums do Java automaticamente, mas o comentário documenta a mudança.
