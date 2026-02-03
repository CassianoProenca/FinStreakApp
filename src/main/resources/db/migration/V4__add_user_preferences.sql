CREATE TABLE user_preferences (
    id UUID PRIMARY KEY,
    theme VARCHAR(50) NOT NULL,
    notifications_enabled BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    version BIGINT
);

ALTER TABLE users ADD COLUMN preferences_id UUID;

ALTER TABLE users 
    ADD CONSTRAINT fk_users_preferences 
    FOREIGN KEY (preferences_id) 
    REFERENCES user_preferences (id);
