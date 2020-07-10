DROP DATABASE IF EXISTS jwt;
CREATE DATABASE jwt
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;
USE jwt;

DROP TABLE IF EXISTS tbl_user;
CREATE TABLE tbl_user
(
    id                     BIGINT         AUTO_INCREMENT PRIMARY KEY,
    create_time            DATETIME       DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    update_time            DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    status                 VARCHAR(20)    DEFAULT ''                                            NOT NULL,
    name                   VARCHAR(32)    NOT NULL COMMENT '姓名',
    phone_number           VARCHAR(32)    NOT NULL COMMENT '手机',
    password               VARCHAR(32)    NOT NULL COMMENT '密码',
    authority              VARCHAR(100)   NOT NULL COMMENT '权限'
);

