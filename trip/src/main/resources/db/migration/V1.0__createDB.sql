create sequence hibernate_sequence start with 1 increment by 1;

create table trip
(
    id          bigint not null,
    boat        bigint  not null,
    departure   bigint  not null,
    destination bigint  not null,
    passengers  integer not null check (passengers >= 1),
    status      integer,
    user_id     varchar(255),
    primary key (id)
);