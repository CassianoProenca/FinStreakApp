CREATE TABLE fin_budgets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    category VARCHAR(50) NOT NULL,
    limit_amount NUMERIC(19, 2) NOT NULL,
    month INTEGER NOT NULL,
    year INTEGER NOT NULL,

    -- Campos de Auditoria BaseEntity
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    version BIGINT DEFAULT 0,

    -- Garante que o usuário só tenha um orçamento por categoria no mesmo mês
    CONSTRAINT uk_budget_user_category_period UNIQUE (user_id, category, month, year)
);

CREATE INDEX idx_fin_budget_user_period ON fin_budgets (user_id, month, year);
