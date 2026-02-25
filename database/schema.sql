
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS users;

CREATE TABLE currencies (
    id SERIAL PRIMARY KEY,
    code CHAR(3) UNIQUE NOT NULL,  
    name TEXT NOT NULL 
);
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT now()
);


CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    currency TEXT NOT NULL,
    balance DECIMAL(15,2) DEFAULT 0
);


CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    from_account_id INT NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    to_account_id INT NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    amount DECIMAL(15,2) NOT NULL,
    timestamp TIMESTAMP DEFAULT now()
);