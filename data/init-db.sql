-- init-db.sql

IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'discord')
BEGIN
    CREATE DATABASE discord;
END
GO

USE discord;
GO

DROP TABLE IF EXISTS messages;
GO
DROP TABLE IF EXISTS user_to_room;
GO
DROP TABLE IF EXISTS user_to_group;
GO
DROP TABLE IF EXISTS security_questions;
GO
DROP TABLE IF EXISTS room;
GO
DROP TABLE IF EXISTS [group];
GO
DROP TABLE IF EXISTS users;
GO

CREATE TABLE users (
    id INT IDENTITY(1,1),
    username NVARCHAR(50) PRIMARY KEY NOT NULL,
    password NVARCHAR(255) NOT NULL,
    authority NVARCHAR(50) NOT NULL,
    birthday DATE,
    interests NVARCHAR(MAX)
);
GO

CREATE TABLE [group] (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(255) NOT NULL
);
GO

CREATE TABLE room (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(255) NOT NULL,
    group_id INT NOT NULL,
    visibility INT NOT NULL,
    FOREIGN KEY (group_id) REFERENCES [group](id)
);
GO

CREATE TABLE user_to_group (
    username NVARCHAR(50) UNIQUE,
    group_id INT NOT NULL,
    PRIMARY KEY (username, group_id),
    FOREIGN KEY (username) REFERENCES users(username),
    FOREIGN KEY (group_id) REFERENCES [group](id)
);
GO

CREATE TABLE user_to_room (
    username NVARCHAR(50) UNIQUE,
    room_id INT NOT NULL,
    PRIMARY KEY (username, room_id),
    FOREIGN KEY (username) REFERENCES users(username),
    FOREIGN KEY (room_id) REFERENCES room(id)
);
GO

CREATE TABLE messages (
    id INT IDENTITY(1,1) PRIMARY KEY,
    room_id INT,
    username NVARCHAR(50),
    message NVARCHAR(MAX),
    type NVARCHAR(50),
    time DATETIME2,
    FOREIGN KEY (room_id) REFERENCES room(id),
    FOREIGN KEY (username) REFERENCES users(username)
);
GO

CREATE TABLE security_questions (
    id INT PRIMARY KEY IDENTITY(1,1),
    username NVARCHAR(50) UNIQUE NOT NULL,
    question NVARCHAR(MAX) NOT NULL,
    answer NVARCHAR(MAX) NOT NULL,
    FOREIGN KEY (username) REFERENCES users(username)
);
GO

INSERT INTO [group] (name) VALUES ('Group 1'), ('Group 2'), ('Group 3');
GO

INSERT INTO room (name, group_id, visibility) VALUES 
('Room 1', 1, 1),
('Room 2', 1, 1),
('Room 3', 1, 1),
('Room a', 2, 1),
('Room b', 2, 1),
('Room c', 2, 1),
('Room 9', 3, 1),
('Room 99', 3, 1),
('Room 999', 3, 1);
GO
