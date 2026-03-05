-- V16__create_missions_and_avatars.sql
-- Tabelas para missões diárias e avatares desbloqueados

CREATE TABLE IF NOT EXISTS daily_missions (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    xp_reward INTEGER NOT NULL DEFAULT 0,
    mission_type VARCHAR(50) NOT NULL,
    required_count INTEGER NOT NULL DEFAULT 1,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_missions_completed (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    mission_id UUID NOT NULL,
    completion_date DATE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_umc_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_umc_mission FOREIGN KEY (mission_id) REFERENCES daily_missions(id),
    CONSTRAINT uq_user_mission_per_day UNIQUE (user_id, mission_id, completion_date)
);

CREATE TABLE IF NOT EXISTS user_unlocked_avatars (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    avatar_key VARCHAR(100) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_uua_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uq_user_avatar UNIQUE (user_id, avatar_key)
);
