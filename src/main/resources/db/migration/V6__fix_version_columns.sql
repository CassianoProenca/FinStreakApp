-- Corrige a coluna version na tabela users
UPDATE users SET version = 0 WHERE version IS NULL;
ALTER TABLE users ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE users ALTER COLUMN version SET NOT NULL;

-- Corrige a coluna version na tabela user_preferences
UPDATE user_preferences SET version = 0 WHERE version IS NULL;
ALTER TABLE user_preferences ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE user_preferences ALTER COLUMN version SET NOT NULL;

-- Corrige a coluna version na tabela fin_goals
UPDATE fin_goals SET version = 0 WHERE version IS NULL;
ALTER TABLE fin_goals ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE fin_goals ALTER COLUMN version SET NOT NULL;

-- Corrige a coluna version na tabela gam_achievements
UPDATE gam_achievements SET version = 0 WHERE version IS NULL;
ALTER TABLE gam_achievements ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE gam_achievements ALTER COLUMN version SET NOT NULL;

-- Corrige a coluna version na tabela gam_profiles (se existir da V1)
UPDATE gam_profiles SET version = 0 WHERE version IS NULL;
ALTER TABLE gam_profiles ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE gam_profiles ALTER COLUMN version SET NOT NULL;

-- Corrige a coluna version na tabela fin_transactions
UPDATE fin_transactions SET version = 0 WHERE version IS NULL;
ALTER TABLE fin_transactions ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE fin_transactions ALTER COLUMN version SET NOT NULL;

-- Corrige a coluna version na tabela edu_tips
UPDATE edu_tips SET version = 0 WHERE version IS NULL;
ALTER TABLE edu_tips ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE edu_tips ALTER COLUMN version SET NOT NULL;

