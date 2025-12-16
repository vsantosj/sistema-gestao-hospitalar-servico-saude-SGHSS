CREATE TABLE tb_user(
    id SERIAL PRIMARY KEY,
    number_register VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,  -- ← Mudei de user_email para email
    password VARCHAR(255) NOT NULL,
    role VARCHAR(100) NOT NULL
);