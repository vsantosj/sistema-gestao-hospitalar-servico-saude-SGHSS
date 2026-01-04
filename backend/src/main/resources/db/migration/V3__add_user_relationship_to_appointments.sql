--V3__add_user_relationship_to_appointments.sql
ALTER TABLE tb_appointment
    ADD COLUMN user_id BIGINT;

ALTER TABLE tb_appointment
    ADD CONSTRAINT fk_appointment_user
        FOREIGN KEY (user_id) REFERENCES tb_user(id)
            ON DELETE CASCADE;

CREATE INDEX idx_appointment_user_id ON tb_appointment(user_id);

DROP INDEX IF EXISTS idx_ap_user_start_end;
CREATE INDEX idx_appointment_user_date
    ON tb_appointment(user_id, date, start_time);