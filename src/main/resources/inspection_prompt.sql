--删除原表
DROP TABLE IF EXISTS inspection_prompt;

CREATE TABLE IF NOT EXISTS inspection_prompt
(
    id             INTEGER     NOT NULL AUTO_INCREMENT,
    gmt_create     TIMESTAMP   NOT NULL,
    gmt_modified   TIMESTAMP   NOT NULL,
    is_deleted     varchar(4)  NOT NULL,
    tag            varchar(50) NOT NULL,
    prompt_name    varchar(50) NOT NULL,
    method_name    varchar(50) NOT NULL,
    prompt_content CLOB        NOT NULL,
    PRIMARY KEY (id)
);


CREATE INDEX IF NOT EXISTS idx_tag ON inspection_prompt (tag);
CREATE INDEX IF NOT EXISTS idx_w ON inspection_prompt (method_name);

INSERT INTO inspection_prompt (gmt_create, gmt_modified, is_deleted, tag, prompt_name,
                               method_name, prompt_content)
VALUES ('2025-05-30 15:45:38', '2025-08-07 09:40:02', 'n', 'test', '组织健康AI巡检prompt', 'businessOverview',
        '简化的Prompt内容用于测试');

-- 重置自增序列，确保下一个ID从正确的值开始
ALTER TABLE inspection_prompt ALTER COLUMN id RESTART WITH 2;

