create sequence hibernate_sequence start with 1 increment by 1;

create table boat
(
    id             bigint not null,
    builder        varchar(255),
    name           varchar(255),
    number_of_crew integer not null check (number_of_crew >= 1),
    max_passengers integer not null check (max_passengers >= 1),
    min_passengers integer not null check (min_passengers >= 1),
    primary key (id)
);