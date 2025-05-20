create sequence user_id_seq start with 100 increment by 10;

create table users
(
    id         bigint             default nextval('user_id_seq') not null,
    email      varchar   not null,
    password   varchar   not null,
    name       varchar   not null,
    role       varchar   not null,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp,
    primary key (id),
    constraint user_email_unique unique (email)
);
