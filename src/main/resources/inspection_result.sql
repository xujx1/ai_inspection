--删除原表
DROP TABLE IF EXISTS inspection_result;

CREATE TABLE IF NOT EXISTS inspection_result
(
    id              INTEGER NOT NULL AUTO_INCREMENT,
    gmt_create      TIMESTAMP    NOT NULL,
    gmt_modified    TIMESTAMP    NOT NULL,
    is_deleted      varchar(4)  NOT NULL,
    tag       varchar(50) NOT NULL,
    work_no         varchar(50) NOT NULL,
    method_name     varchar(50) NOT NULL,
    req             CLOB    NOT NULL,
    last_resp       CLOB,
    this_resp       CLOB,
    check_flag      varchar(4)  NOT NULL DEFAULT 'U',
    error_msg       CLOB,
    inspection_type varchar(10) NOT NULL DEFAULT 'api',
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_is_d ON inspection_result(is_deleted);
CREATE INDEX IF NOT EXISTS idx_w ON inspection_result(work_no);

INSERT INTO inspection_result (gmt_create, gmt_modified, is_deleted, tag, work_no,
                                     method_name, req, last_resp, this_resp, check_flag, error_msg,
                                     inspection_type)
VALUES ('2025-05-30 15:45:38', '2025-08-07 09:40:02', 'n', 'test', '1', 'organizationHealth',
        '{"prompt":"在对值进行判断的时候需要注意下面几种类型\n1、如果两个值都是字符串类型：\n  a：包含公共的单位，比如''TB、万、人、%''等，则去掉公共的单位进行数值的比如。具体的例子：存在异地汇报有1人，其中''1''是我们要比对的数值，而''存在异地汇报''是我们要比对的key。\n  b：如果值中间都包含了分隔符，比如''/''，则根据分隔符把值拆成多个内容进行比对。具体的例子：1/0.1，则拆分成1、0.1两个值进行比对\n  c：如果是百分比的话，比如包含''%''，则将''%''去掉，用数值进行比对。具体的例子：比如''4.59%''、''4.58%''，这两个值进行比对的话，则将''%''去掉，用''40''、''41''两个数值进行数值比对，使用工具判断出''40''、''41''两个数值的波动范围、数值的值如果相差在10%以内，则认为一致。\n2、如果两个值都是数值类型，需要按照下面几个步骤处理：\n  a、需要根据两个值的大小，来判断出波动阈值。如果两个值有任何一个大于10的话，则波动阈值为20%，否则如果两个值都在10以内，则波动阈值为100%。具体的例子：11、9两个值进行比对的时候，两个值有一个是11大于10，波动阈值为20%。1和2两个值都小于10，则波动阈值为100%。\n  b、使用工具计算出两个数值波动值，返回实际的波动值。\n  c、判断实际的波动值是否大于上面预设的波动阈值，如果大于上面预设的波动阈值则认为不一致，否则一致\n\n如果波动大于前面的阈值，则判断认为不一致，并且能够返回认定不一致的理由，理由中需要包含阈值和计算后的实际波动值，比如：数值波动为100%，超过预设的阈值20%"}',
        '', '',
        'Y', '[]', 'page');

INSERT INTO inspection_result (gmt_create, gmt_modified, is_deleted, tag, work_no,
                                     method_name, req, last_resp, this_resp, check_flag, error_msg,
                                     inspection_type)
VALUES ('2025-05-30 15:45:38', '2025-08-07 09:40:02', 'n',  'test', '2', 'businessOverview',
        '{"prompt":"在对值进行判断的时候需要注意下面几种类型\n1、如果两个值都是字符串类型：\n  a：包含公共的单位，比如''TB、万、人、%''等，则去掉公共的单位进行数值的比如。具体的例子：存在异地汇报有1人，其中''1''是我们要比对的数值，而''存在异地汇报''是我们要比对的key。\n  b：如果值中间都包含了分隔符，比如''/''，则根据分隔符把值拆成多个内容进行比对。具体的例子：1/0.1，则拆分成1、0.1两个值进行比对\n  c：如果是百分比的话，比如包含''%''，则将''%''去掉，用数值进行比对。具体的例子：比如''4.59%''、''4.58%''，这两个值进行比对的话，则将''%''去掉，用''40''、''41''两个数值进行数值比对，使用工具判断出''40''、''41''两个数值的波动范围、数值的值如果相差在10%以内，则认为一致。\n2、如果两个值都是数值类型，需要按照下面几个步骤处理：\n  a、需要根据两个值的大小，来判断出波动阈值。如果两个值有任何一个大于10的话，则波动阈值为20%，否则如果两个值都在10以内，则波动阈值为100%。具体的例子：11、9两个值进行比对的时候，两个值有一个是11大于10，波动阈值为20%。1和2两个值都小于10，则波动阈值为100%。\n  b、使用工具计算出两个数值波动值，返回实际的波动值。\n  c、判断实际的波动值是否大于上面预设的波动阈值，如果大于上面预设的波动阈值则认为不一致，否则一致\n\n如果波动大于前面的阈值，则判断认为不一致，并且能够返回认定不一致的理由，理由中需要包含阈值和计算后的实际波动值，比如：数值波动为100%，超过预设的阈值20%"}',
        '',
        '', 'N', '[{"actualValue":"136.73","expectedValue":"179.19","indicatorName":"差旅费","reason":"数值波动为31.06%，超过预设的阈值10%"}]', 'page');

-- 重置自增序列，确保下一个ID从正确的值开始
-- 由于已经有ID=1和ID=2的记录，下一个应该从ID=3开始
-- 这里设置为从4开始，避免与现有数据冲突
ALTER TABLE inspection_result ALTER COLUMN id RESTART WITH 3;