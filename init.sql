CREATE TABLE rates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rate DOUBLE NOT NULL,
    name VARCHAR(255),
    description VARCHAR(255)
);

CREATE TABLE loans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    value DOUBLE NOT NULL,
    start_date DATETIME NOT NULL,
    rate_id BIGINT NOT NULL,
    FOREIGN KEY (rate_id) REFERENCES rates(id)
);

CREATE TABLE installments (
    loan_id BIGINT NOT NULL,
    installment_number INT NOT NULL,
    value DOUBLE NOT NULL,
    due_date DATETIME NOT NULL,
    PRIMARY KEY (loan_id, installment_number),
    FOREIGN KEY (loan_id) REFERENCES loans(id)
);


CREATE TABLE auths (
    client_id VARCHAR(255) NOT NULL,
    client_secret VARCHAR(255) NOT NULL,
    PRIMARY KEY (client_id)
);

INSERT INTO auths (client_id, client_secret) VALUES ('bff', 'averylongandsecureclientsecret');

INSERT INTO rates (name, description, rate) VALUES ('Rate 1', 'Sample Description', 1.5);
INSERT INTO rates (name, description, rate) VALUES ('Rate 2', 'Sample Description', 1.7);
