CREATE TABLE tb_appointment(
       id SERIAL PRIMARY KEY,
       title VARCHAR(120) NOT NULL,
       description TEXT,
       date DATE NOT NULL,
       start_time TIME NOT NULL,
       end_time TIME NOT NULL,
       status VARCHAR(20) NOT NULL DEFAULT 'AGENDADO',
       patient VARCHAR(100) NOT NULL,
       doctor  VARCHAR(100) NOT NULL,
       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
       updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
       CONSTRAINT ck_status CHECK (status IN ('AGENDADO', 'CANCELADO', 'CONCLUIDO')),
       CONSTRAINT ck_duration CHECK(start_time < end_time )
);

CREATE INDEX idx_ap_user_start_end
    ON tb_appointment(patient, start_time, end_time);

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at := NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_set_update_at
    BEFORE UPDATE ON tb_appointment
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();