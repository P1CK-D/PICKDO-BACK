CREATE TABLE IF NOT EXISTS user_mission (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id),
    mission_id  BIGINT NOT NULL REFERENCES mission(id),
    status      VARCHAR(20) NOT NULL CHECK (status IN ('SELECTED', 'COMPLETED', 'FAILED', 'RENEWED')),
    proof_data  TEXT,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP NOT NULL,
    deleted_at  TIMESTAMP
);

CREATE INDEX idx_user_mission_user_id ON user_mission(user_id);
CREATE INDEX idx_user_mission_mission_id ON user_mission(mission_id);
