# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table topic (
  id                        bigint auto_increment not null,
  title                     varchar(255),
  created_date              datetime,
  topic                     longtext,
  deleted                   tinyint(1) default 0,
  constraint pk_topic primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  email                     varchar(255),
  password                  varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  created_date              datetime,
  is_admin                  tinyint(1) default 0,
  deleted                   tinyint(1) default 0,
  default_topic_id          bigint,
  constraint pk_user primary key (id))
;

create table user_topic (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  topic_id                  bigint,
  user_topic                longtext,
  created_date              datetime,
  read_time                 varchar(255),
  write_time                varchar(255),
  deleted                   tinyint(1) default 0,
  constraint pk_user_topic primary key (id))
;

alter table user add constraint fk_user_defaultTopic_1 foreign key (default_topic_id) references topic (id) on delete restrict on update restrict;
create index ix_user_defaultTopic_1 on user (default_topic_id);
alter table user_topic add constraint fk_user_topic_user_2 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_user_topic_user_2 on user_topic (user_id);
alter table user_topic add constraint fk_user_topic_topic_3 foreign key (topic_id) references topic (id) on delete restrict on update restrict;
create index ix_user_topic_topic_3 on user_topic (topic_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table topic;

drop table user;

drop table user_topic;

SET FOREIGN_KEY_CHECKS=1;

