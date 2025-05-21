create sequence favorite_id_seq start with 100 increment by 10;

create table favorites
(
    id         bigint    not null default nextval('favorite_id_seq'),
    user_id    bigint    not null,
    post_id    bigint    not null,
    created_at timestamp not null default current_timestamp,
    primary key (id),
    constraint fk_favorites_user foreign key (user_id) references users (id),
    constraint fk_favorites_post foreign key (post_id) references posts (id),
    constraint uk_favorites_user_post unique (user_id, post_id)
);

