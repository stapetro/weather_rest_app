CREATE DATABASE IF NOT EXISTS securerest CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
use `securerest`;
create table IF NOT exists `account` (
  `id` bigint not null auto_increment primary key,
  `username` varchar(30) not null unique,
  `password` char(160) not null,
  `role` enum('USER', 'ADMIN') not null
);
CREATE USER IF NOT EXISTS 'spring'@'localhost' IDENTIFIED BY 'spring';
GRANT ALL PRIVILEGES ON `securerest`.* TO 'spring'@'localhost';
FLUSH PRIVILEGES;