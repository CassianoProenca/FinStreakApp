CREATE TABLE fin_goal_history (
    id UUID PRIMARY KEY,
    goal_id UUID NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    description VARCHAR(255),
    transaction_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    -- Campos de Auditoria BaseEntity
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    version BIGINT DEFAULT 0,

    CONSTRAINT fk_goal_history_goal FOREIGN KEY (goal_id) REFERENCES fin_goals (id)
);

CREATE INDEX idx_fin_goal_history_goal ON fin_goal_history (goal_id);
