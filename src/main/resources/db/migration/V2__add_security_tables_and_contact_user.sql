CREATE TABLE users
(
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE roles
(
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE user_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

ALTER TABLE contact ADD COLUMN user_id BIGINT;

ALTER TABLE contact ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE contact ADD CONSTRAINT fk_contact_user FOREIGN KEY (user_id) REFERENCES users (id);