create sequence category_id_seq start with 100 increment by 10;

CREATE TABLE categories
(
    id         bigint    not null default nextval('category_id_seq'),
    name       varchar   not null,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp,
    primary key (id),
    constraint categories_name_unique unique (name)
);

INSERT INTO categories (id, name)
VALUES (1, 'Java'),
       (2, 'Python'),
       (3, 'Go'),
       (4, 'NodeJS'),
       (5, 'Docker'),
       (6, 'Kubernetes'),
       (7, 'SpringBoot'),
       (8, 'React'),
       (9, 'Angular'),
       (10, 'Vue'),
       (11, 'DevOps'),
       (12, 'Cloud'),
       (13, 'Database'),
       (14, 'Mobile'),
       (15, 'Web'),
       (16, 'Security'),
       (17, 'Testing'),
       (18, 'Architecture'),
       (19, 'Career')
;