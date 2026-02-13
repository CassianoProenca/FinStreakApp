-- Pontos 2, 3, 5 e 6 do feedback do frontend
ALTER TABLE users ADD COLUMN monthly_income NUMERIC(19, 2) DEFAULT 0;

-- Adiciona suporte a recorrência e ícones personalizados nas transações
ALTER TABLE fin_transactions ADD COLUMN is_recurring BOOLEAN DEFAULT FALSE;
ALTER TABLE fin_transactions ADD COLUMN frequency VARCHAR(50);
ALTER TABLE fin_transactions ADD COLUMN repeat_day INTEGER;
ALTER TABLE fin_transactions ADD COLUMN icon_key VARCHAR(100);

-- Padroniza metas com ícones e data completa
ALTER TABLE fin_goals RENAME COLUMN icon TO icon_key;
ALTER TABLE fin_goals ALTER COLUMN deadline TYPE TIMESTAMP;
