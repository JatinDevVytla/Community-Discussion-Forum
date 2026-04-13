-- =============================================================
-- Forum Application - Database Schema
-- Run: mysql -u root -p < schema.sql
-- =============================================================

CREATE DATABASE IF NOT EXISTS forum_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE forum_db;

-- Drop in reverse FK order if re-running
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS threads;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

-- -------------------------------------------------------------
-- 1. USERS  (no passwords — auth is out of scope)
-- -------------------------------------------------------------
CREATE TABLE users (
    id         INT          PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(150) NOT NULL UNIQUE,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- -------------------------------------------------------------
-- 2. CATEGORIES
-- -------------------------------------------------------------
CREATE TABLE categories (
    id          INT          PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- -------------------------------------------------------------
-- 3. THREADS
-- -------------------------------------------------------------
CREATE TABLE threads (
    id          INT           PRIMARY KEY AUTO_INCREMENT,
    category_id INT           NOT NULL,
    user_id     INT           NOT NULL,
    title       VARCHAR(255)  NOT NULL,
    body        TEXT          NOT NULL,
    created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id)     REFERENCES users(id)      ON DELETE CASCADE
);

-- -------------------------------------------------------------
-- 4. POSTS  (replies inside a thread)
-- -------------------------------------------------------------
CREATE TABLE posts (
    id         INT       PRIMARY KEY AUTO_INCREMENT,
    thread_id  INT       NOT NULL,
    user_id    INT       NOT NULL,
    body       TEXT      NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (thread_id) REFERENCES threads(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id)   REFERENCES users(id)   ON DELETE CASCADE
);

-- =============================================================
-- SEED DATA
-- =============================================================

INSERT INTO users (username, email) VALUES
    ('alice',   'alice@example.com'),
    ('bob',     'bob@example.com'),
    ('carol',   'carol@example.com');

INSERT INTO categories (name, description) VALUES
    ('General',    'General discussion about anything'),
    ('Technology', 'Programming, tools, and tech trends'),
    ('Q&A',        'Ask the community for help'),
    ('Off-Topic',  'Anything that does not fit elsewhere');

INSERT INTO threads (category_id, user_id, title, body) VALUES
    (2, 1, 'Best Java frameworks in 2024?',
           'I am looking for lightweight Java frameworks for building REST APIs. Any recommendations?'),
    (3, 2, 'How does JDBC connection pooling work?',
           'I keep seeing references to connection pooling in tutorials. Can someone explain the concept?'),
    (1, 3, 'Welcome to the forum!',
           'Hello everyone — glad to be here. Looking forward to great discussions.');

INSERT INTO posts (thread_id, user_id, body) VALUES
    (1, 2, 'Definitely check out Spark Java — super minimal and clean for REST.'),
    (1, 3, 'Javalin is also great if you want Kotlin interoperability.'),
    (2, 1, 'Pooling reuses existing DB connections instead of creating a new one per request. Apache DBCP and HikariCP are popular options.');

-- =============================================================
-- USEFUL QUERIES (for reference)
-- =============================================================

-- All threads with author, category and reply count, newest first
-- SELECT t.id, t.title, t.created_at,
--        u.username AS author,
--        c.name     AS category,
--        COUNT(p.id) AS reply_count
-- FROM   threads t
-- JOIN   users      u ON u.id = t.user_id
-- JOIN   categories c ON c.id = t.category_id
-- LEFT JOIN posts   p ON p.thread_id = t.id
-- GROUP BY t.id
-- ORDER BY t.created_at DESC;

-- All replies for thread #1
-- SELECT p.id, p.body, p.created_at, u.username
-- FROM   posts p
-- JOIN   users u ON u.id = p.user_id
-- WHERE  p.thread_id = 1
-- ORDER BY p.created_at ASC;
