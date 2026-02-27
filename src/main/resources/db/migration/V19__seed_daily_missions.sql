-- V19__seed_daily_missions.sql
-- Seed default daily missions

INSERT INTO daily_missions (id, title, description, xp_reward, mission_type, required_count, version, created_at, updated_at)
VALUES
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Lançamento do Dia', 'Registre qualquer despesa hoje', 20, 'TRANSACTION_COUNT', 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'Meta do Bem', 'Faça um depósito em qualquer meta', 50, 'GOAL_DEPOSIT', 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
