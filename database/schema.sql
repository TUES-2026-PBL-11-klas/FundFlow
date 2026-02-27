
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS currencies;

CREATE TABLE currencies (
    id SERIAL PRIMARY KEY,
    code CHAR(3) UNIQUE NOT NULL,
    exchnage_rate DECIMAL(10,4) NOT NULL
);
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT now()
);
CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    currency_id INT NOT NULL REFERENCES currencies(id),
    iban TEXT UNIQUE NOT NULL,
    balance DECIMAL(15,2) DEFAULT 0
);
CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    from_account_id INT NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    to_account_id INT NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    currency_id INT NOT NULL REFERENCES currencies(id),
    amount DECIMAL(15,2) NOT NULL,
    timestamp TIMESTAMP DEFAULT now()
);