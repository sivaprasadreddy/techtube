create sequence post_id_seq start with 100 increment by 10;

CREATE TABLE posts
(
    id          bigint       not null default nextval('post_id_seq'),
    title       varchar(255) not null,
    url         varchar(255) not null,
    description text         not null,
    category_id bigint       not null references categories (id),
    created_by  bigint       not null references users (id),
    status      varchar(20)  not null default 'PENDING',
    created_at  timestamp    not null default current_timestamp,
    updated_at  timestamp,
    primary key (id)
);

insert into posts(id, title, url, description, category_id, created_by, status) VALUES
(1, 'Spring JdbcClient: A better alternative to JdbcTemplate', 'https://youtu.be/oF7vlaJhySE', 'Spring framework 6.1 introduced a new JdbcClient API, which is a wrapper on top of JdbcTemplate, for performing database operations using a fluent API. In this video, you will learn how to perform CRUD operations using JdbcClient and test them.', 7, 2, 'APPROVED'),
(2, 'Spring Boot: Build URL Shortener Application - Complete Course (5 Hours)', 'https://www.youtube.com/watch?v=XEgS8yq-zgw', 'Spring Boot: Build URL Shortener Application - Complete Course', 7, 2, 'APPROVED'),
(3, 'Spring Boot Microservices Complete Tutorial', 'https://www.youtube.com/watch?v=ZKQWwCUEABY', 'Spring Boot Microservices Complete Tutorial', 7, 2, 'APPROVED'),
(4, 'IntelliJ IDEA Tips for Java Developers - Code Faster', 'https://www.youtube.com/watch?v=Kiry69tEIOE', 'IntelliJ IDEA Tips for Java Developers', 1, 1, 'APPROVED'),
(5, 'Spring Modulith Crash Course : Building Modular Monoliths using Spring Boot', 'https://www.youtube.com/watch?v=FkP2aZiBrhg', 'Spring Modulith Crash Course : Building Modular Monoliths using Spring Boot', 7, 1, 'APPROVED'),
(6, 'My IntelliJ IDEA SetUp for Java Development', 'https://www.youtube.com/watch?v=HJYybOuPym4', 'My IntelliJ IDEA SetUp for Java Development', 1, 2, 'PENDING')
;