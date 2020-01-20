# notecloud

user表sql：
create table user
(
    user_id         int(10) primary key  auto_increment comment '用户id',
    user_name       varchar(255) unique comment '用户名',
    user_password   varchar(255) not null comment '用户密码',
    sex             varchar(255)        default null comment '性别',
    email           varchar(255) unique default null comment '电子邮箱',
    phone           int(44) unique      default null comment '手机号',
    create_time     varchar(255)        default null comment '创建时间',
    edit_time       varchar(255)        default null comment '更新时间',
    is_logout       int(1)              default 0 comment '"1"代表注销，"0"代表未注销',
    last_login_time varchar(255)        default null comment '最后登录时间',
    remark          varchar(255)        default null comment '备注',
    user_role       varchar(255)        default null comment '角色'
)

create table if not exists note
(
    note_id      int          not null primary key comment '笔记id',
    user_id      int          not null unique comment '用户id',
    type_id      int          not null unique comment '笔记id',
    note_title   varchar(255) not null comment '笔记标题',
    note_content text         not null comment '笔记内容',
    create_time  varchar(255) not null comment '创建时间',
    update_time  varchar(255) not null comment '更新时间',
    is_recycle   varchar(1)   not null comment '1代表在回收站，0代表不在回收站'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8


CREATE TABLE `typeUtils` (
  `type_id` int(10) NOT NULL COMMENT '类型id',
  `user_id` int(10) NOT NULL COMMENT '用户id',
  `type_name` varchar(255) NOT NULL COMMENT '类型名称',
  `create_time` varchar(255) DEFAULT NULL COMMENT '创建时间',
  `update_time` varchar(255) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='类型id';