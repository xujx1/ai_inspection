--删除原表
DROP TABLE IF EXISTS inspection_tag;

CREATE TABLE IF NOT EXISTS inspection_tag
(
    id             INTEGER     NOT NULL AUTO_INCREMENT,
    gmt_create     TIMESTAMP   NOT NULL,
    gmt_modified   TIMESTAMP   NOT NULL,
    is_deleted     varchar(4)  NOT NULL,
    tag            varchar(50) NOT NULL,
    tag_name       varchar(50) NOT NULL,
    tag_desc       varchar(50) NOT NULL,
    account        varchar(50) NOT NULL,
    password       varchar(50) NOT NULL,
    login_url      varchar(100) NOT NULL,
    inspection_url varchar(100) NOT NULL,
    PRIMARY KEY (id)
);


CREATE INDEX IF NOT EXISTS idx_tag_tag ON inspection_tag (tag);

INSERT INTO inspection_tag (gmt_create, gmt_modified, is_deleted, tag, tag_name, tag_desc, account, password, login_url, inspection_url)
VALUES ('2025-05-30 15:45:38', '2025-08-07 09:40:02', 'n', 'test', '测试', '测试描述', 'account', 'password', 
'http://localhost:8080/inspection_page/login.html', 'http://localhost:8080/inspection_page/dashboard_dynamic.html');

-- 重置自增序列，确保下一个ID从正确的值开始
ALTER TABLE inspection_tag ALTER COLUMN id RESTART WITH 2;

