# notecloud

user表sql：
create table user
(
    user_id         int(10) primary key comment '用户id',
    user_name       varchar(255) unique comment '用户名',
    user_password   varchar(255) not null comment '用户密码',
    sex             varchar(255)        default null comment '性别',
    email           varchar(255) unique default null comment '电子邮箱',
    phone           int(11) unique      default null comment '手机号',
    create_time     varchar(255)        default null comment '创建时间',
    edit_time       varchar(255)        default null comment '更新时间',
    is_logout       int(1)              default 1 comment '"1"代表注销，"0"代表未注销',
    last_login_time varchar(255)        default null comment '最后登录时间',
    remark          varchar(255)        default null comment '备注',
    user_role       varchar(255)        default null comment '角色'
)
