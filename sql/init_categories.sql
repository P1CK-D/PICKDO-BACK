CREATE TABLE IF NOT EXISTS category (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(100) NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS user_category (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id),
    category_id BIGINT NOT NULL REFERENCES category(id),
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP NOT NULL,
    CONSTRAINT uk_user_category UNIQUE (user_id, category_id)
);

CREATE INDEX idx_user_category_user_id ON user_category(user_id);
CREATE INDEX idx_user_category_category_id ON user_category(category_id);
