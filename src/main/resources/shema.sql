CREATE TABLE "User"
(
    id          SERIAL PRIMARY KEY,
    name        TEXT NOT NULL,
    surname     TEXT NOT NULL
);

CREATE TABLE Telegram
(
    id          SERIAL PRIMARY KEY,
    user_id     INTEGER,
    telegram_id  TEXT,
    FOREIGN KEY (user_id) REFERENCES "User" (id) ON DELETE CASCADE
);

CREATE TABLE Email
(
    id          SERIAL PRIMARY KEY,
    user_id     INTEGER,
    email       TEXT,
    FOREIGN KEY (user_id) REFERENCES "User" (id) ON DELETE CASCADE
);

CREATE TABLE Slack
(
    id          SERIAL PRIMARY KEY,
    user_id     INTEGER,
    slack_id    TEXT,
    FOREIGN KEY (user_id) REFERENCES "User" (id) ON DELETE CASCADE
);

CREATE TABLE Schedule
(
    id          SERIAL PRIMARY KEY,
    user_id     INTEGER,
    time        TIMESTAMP,
    message     TEXT,
    dispatches  BOOLEAN,
    FOREIGN KEY (user_id) REFERENCES "User" (id) ON DELETE CASCADE
);