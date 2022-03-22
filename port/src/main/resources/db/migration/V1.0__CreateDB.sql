create sequence hibernate_sequence start with 1 increment by 1;

create table port
(
    id bigint not null,
    name varchar(255),
    weather varchar(255),
    primary key (id)
);