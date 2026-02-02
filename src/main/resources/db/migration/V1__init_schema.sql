-- Habilita extensão para gerar UUIDs no banco se necessário (opcional, pois o Java gera)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ================================================================================================
-- MODULO FINANCE (fin_)
-- ================================================================================================
CREATE TABLE fin_transactions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    description VARCHAR(100) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL, -- BigDecimal mapping
    type VARCHAR(20) NOT NULL,
    category VARCHAR(50) NOT NULL,
    date TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    -- Campos de Auditoria BaseEntity
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    version BIGINT DEFAULT 0
);

-- Índice para deixar a tela de "Extrato" super rápida
CREATE INDEX idx_fin_transaction_user_date ON fin_transactions (user_id, date);


-- ================================================================================================
-- MODULO GAMIFICATION (gam_)
-- ================================================================================================
CREATE TABLE gam_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE, -- Um perfil por usuário
    current_streak INTEGER DEFAULT 0 NOT NULL,
    max_streak INTEGER DEFAULT 0 NOT NULL,
    total_xp BIGINT DEFAULT 0 NOT NULL,
    last_activity_date DATE,

    -- Campos de Auditoria BaseEntity
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    version BIGINT DEFAULT 0
);


-- ================================================================================================
-- MODULO EDUCATION (edu_)
-- ================================================================================================
CREATE TABLE edu_tips (
    id UUID PRIMARY KEY,
    content VARCHAR(500) NOT NULL,
    category VARCHAR(50) NOT NULL,
    difficulty_level VARCHAR(20) NOT NULL,
    release_date DATE,
    active BOOLEAN DEFAULT TRUE,

    -- Campos de Auditoria BaseEntity
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    version BIGINT DEFAULT 0
);