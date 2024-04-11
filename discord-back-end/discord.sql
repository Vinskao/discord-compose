drop table security_questions
drop table messages
drop table user_to_group
drop table user_to_room
drop table room
drop table [group]
drop table users

create DATABASE discord;
use discord;

select * from security_questions
select * from messages
select * from users
select * from user_to_group
select * from user_to_room
select * from room
select * from [group]

DELETE FROM messages;
DELETE FROM user_to_room;
DELETE FROM user_to_group;
DELETE FROM security_questions;

drop DATABASE discord;
create DATABASE discord;
use discord;
CREATE TABLE users (
    id INT IDENTITY(1,1),
    username NVARCHAR(50) PRIMARY KEY NOT NULL,
    password NVARCHAR(255) NOT NULL,
    authority NVARCHAR(50) NOT NULL,
    birthday DATE,
    interests NVARCHAR(MAX)
);

CREATE TABLE [group] (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(255) NOT NULL
);

CREATE TABLE room (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(255) NOT NULL,
    group_id INT,
    visibility INT NOT NULL,
    FOREIGN KEY (group_id) REFERENCES [group](id)
);

CREATE TABLE user_to_group (
    username NVARCHAR(50) UNIQUE,
    group_id INT,
    PRIMARY KEY (username, group_id),
    FOREIGN KEY (username) REFERENCES users(username),
    FOREIGN KEY (group_id) REFERENCES [group](id)
);

CREATE TABLE user_to_room (
    username NVARCHAR(50) UNIQUE,
    room_id INT,
    PRIMARY KEY (username, room_id),
    FOREIGN KEY (username) REFERENCES users(username),
    FOREIGN KEY (room_id) REFERENCES room(id)
);

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
CREATE TABLE security_questions (
    id INT PRIMARY KEY IDENTITY(1,1),
    username NVARCHAR(50) UNIQUE,
    question NVARCHAR(MAX),
    answer NVARCHAR(MAX),
    FOREIGN KEY (username) REFERENCES users(username)
);


INSERT INTO [group] (name) VALUES ('Group 1');
INSERT INTO [group] (name) VALUES ('Group 2');
INSERT INTO [group] (name) VALUES ('Group 3');
INSERT INTO room (name, group_id, visibility) VALUES ('Room 1', 1, 1);
INSERT INTO room (name, group_id, visibility) VALUES ('Room 2', 1, 1);
INSERT INTO room (name, group_id, visibility) VALUES ('Room 3', 1, 1);
INSERT INTO room (name, group_id, visibility) VALUES ('Room a', 2, 1);
INSERT INTO room (name, group_id, visibility) VALUES ('Room b', 2, 1);
INSERT INTO room (name, group_id, visibility) VALUES ('Room c', 2, 1);
INSERT INTO room (name, group_id, visibility) VALUES ('Room 9', 3, 1);
INSERT INTO room (name, group_id, visibility) VALUES ('Room 99', 3, 1);
INSERT INTO room (name, group_id, visibility) VALUES ('Room 999', 3, 1);


INSERT INTO user_to_group (username, group_id) VALUES
('chiaki@mli.com', 1),
('min@mli.com', 1),
('alice@mli.com', 1);
INSERT INTO user_to_room (username, room_id) VALUES
('chiaki@mli.com', 1),
('min@mli.com', 1),
('alice@mli.com', 1);

INSERT INTO user_to_room (username, room_id) VALUES
('chiaki@mli.com', 2),
('alice@mli.com', 2);

INSERT INTO user_to_room (username, room_id) VALUES
('chiaki@mli.com', 3),
('min@mli.com', 3);