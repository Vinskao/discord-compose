-- init-db.sql
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'discord')
BEGIN
    CREATE DATABASE discord;
END
GO

USE discord;
GO

-- Function to check if table exists and is empty
-- It returns 1 if the table exists and has no rows, otherwise 0
CREATE FUNCTION dbo.IsEmpty(@tableName NVARCHAR(255))
RETURNS BIT AS
BEGIN
    DECLARE @sql NVARCHAR(MAX), @paramDefinition NVARCHAR(500);
    DECLARE @count INT;
    SET @sql = N'SELECT @result = COUNT(*) FROM ' + QUOTENAME(@tableName);
    SET @paramDefinition = N'@result INT OUTPUT';
    EXEC sp_executesql @sql, @paramDefinition, @count OUTPUT;
    RETURN IIF(@count = 0, 1, 0);
END
GO

-- Users table
IF OBJECT_ID('dbo.users') IS NULL
BEGIN
    CREATE TABLE users (
        id INT IDENTITY(1,1),
        username NVARCHAR(50) PRIMARY KEY NOT NULL,
        password NVARCHAR(255) NOT NULL,
        authority NVARCHAR(50) NOT NULL,
        birthday DATE,
        interests NVARCHAR(MAX)
    );
END
GO

-- Group table
IF OBJECT_ID('dbo.[group]') IS NULL
BEGIN
    CREATE TABLE [group] (
        id INT PRIMARY KEY IDENTITY(1,1),
        name NVARCHAR(255) NOT NULL
    );
END
ELSE IF dbo.IsEmpty('dbo.[group]') = 1
BEGIN
    INSERT INTO [group] (name) VALUES ('Group 1'), ('Group 2'), ('Group 3');
END
GO

-- Room table
IF OBJECT_ID('dbo.room') IS NULL
BEGIN
    CREATE TABLE room (
        id INT PRIMARY KEY IDENTITY(1,1),
        name NVARCHAR(255) NOT NULL,
        group_id INT NOT NULL,
        visibility INT NOT NULL,
        FOREIGN KEY (group_id) REFERENCES [group](id)
    );
END
ELSE IF dbo.IsEmpty('dbo.room') = 1
BEGIN
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
END
GO

-- User to Group table
IF OBJECT_ID('dbo.user_to_group') IS NULL
BEGIN
    CREATE TABLE user_to_group (
        username NVARCHAR(50) UNIQUE,
        group_id INT NOT NULL,
        PRIMARY KEY (username, group_id),
        FOREIGN KEY (username) REFERENCES users(username),
        FOREIGN KEY (group_id) REFERENCES [group](id)
    );
END
GO

-- User to Room table
IF OBJECT_ID('dbo.user_to_room') IS NULL
BEGIN
    CREATE TABLE user_to_room (
        username NVARCHAR(50) UNIQUE,
        room_id INT NOT NULL,
        PRIMARY KEY (username, room_id),
        FOREIGN KEY (username) REFERENCES users(username),
        FOREIGN KEY (room_id) REFERENCES room(id)
    );
END
GO

-- Messages table
IF OBJECT_ID('dbo.messages') IS NULL
BEGIN
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
END
GO

-- Security Questions table
IF OBJECT_ID('dbo.security_questions') IS NULL
BEGIN
    CREATE TABLE security_questions (
        id INT PRIMARY KEY IDENTITY(1,1),
        username NVARCHAR(50) UNIQUE NOT NULL,
        question NVARCHAR(MAX) NOT NULL,
        answer NVARCHAR(MAX) NOT NULL,
        FOREIGN KEY (username) REFERENCES users(username)
    );
END
GO