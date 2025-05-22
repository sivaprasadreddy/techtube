DELETE FROM videos;
ALTER SEQUENCE video_id_seq RESTART WITH 100;

insert into videos(id, title, url, description, category_id, created_by, status, created_at) VALUES
(1, 'OAuth 2.0 and OpenID Connect (in plain English)', 'https://www.youtube.com/watch?v=996OiexHze0', 'OAuth 2.0 and OpenID Connect (in plain English)', 16, 2, 'APPROVED', '2024-03-06'),
(2, 'The Why of Go', 'https://www.youtube.com/watch?v=bmZNaUcwBt4', 'The Why of Go', 3, 2, 'APPROVED', '2024-03-09'),
(3, 'Rob Pike, Public Static Void', 'https://www.youtube.com/watch?v=5kj5ApnhPAE', 'Rob Pike, Public Static Void', 3, 2, 'APPROVED', '2024-04-18'),
(4, 'TDD, Where Did It All Go Wrong (Ian Cooper)', 'https://www.youtube.com/watch?v=EZ05e7EMOLM', 'TDD, Where Did It All Go Wrong (Ian Cooper)', 1, 2, 'APPROVED', '2024-05-16'),
(5, 'Simple Made Easy - Rich Hickey', 'https://www.youtube.com/watch?v=LKtk3HCgTa8', 'Simple Made Easy - Rich Hickey', 18, 2, 'APPROVED', '2024-05-24'),
(6, 'Good Enough Architecture - Stefan Tilkov', 'https://www.youtube.com/watch?v=PzEox3szeRc', 'Good Enough Architecture - Stefan Tilkov', 18, 2, 'APPROVED', '2024-06-11'),
(7, 'The Well-Balanced Programmer (J.B. Rainsberger )', 'https://www.youtube.com/watch?v=XhN6fJYG81A', 'The Well-Balanced Programmer (J.B. Rainsberger )', 19, 2, 'APPROVED', '2024-07-10'),
(8, 'Spring Boot RESTful CRUD Application with IntelliJ IDEA and JPA Buddy', 'https://www.youtube.com/watch?v=GM4FaPsRiyw', 'Spring Boot RESTful CRUD Application with IntelliJ IDEA and JPA Buddy', 7, 2, 'APPROVED', '2025-02-14'),
(9, 'JPA Buddy – From Zero to Hero', 'https://www.youtube.com/watch?v=TpD6bT9M1CE', 'JPA Buddy – From Zero to Hero', 7, 2, 'APPROVED', '2025-02-16'),
(10, 'Developing Spring Boot Applications With Joy', 'https://www.youtube.com/watch?v=VvWtoaeHQUQ', 'Developing Spring Boot Applications With Joy', 1, 2, 'APPROVED', '2025-02-18'),
(11, 'Working with Flyway Migrations in a Spring Boot Application', 'https://www.youtube.com/watch?v=AMopB9C2bH8', 'Working with Flyway Migrations in a Spring Boot Application', 7, 2, 'APPROVED', '2025-03-04'),
(12, 'My IntelliJ IDEA SetUp for Java Development', 'https://www.youtube.com/watch?v=HJYybOuPym4', 'My IntelliJ IDEA SetUp for Java Development', 1, 2, 'PENDING', '2025-03-15'),
(13, 'Spring Modulith Crash Course : Building Modular Monoliths using Spring Boot', 'https://www.youtube.com/watch?v=FkP2aZiBrhg', 'Spring Modulith Crash Course : Building Modular Monoliths using Spring Boot', 7, 1, 'APPROVED', '2025-04-24'),
(14, 'IntelliJ IDEA Tips for Java Developers - Code Faster', 'https://www.youtube.com/watch?v=Kiry69tEIOE', 'IntelliJ IDEA Tips for Java Developers', 1, 1, 'APPROVED', '2025-05-10'),
(15, 'Spring Boot Microservices Complete Tutorial', 'https://www.youtube.com/watch?v=ZKQWwCUEABY', 'Spring Boot Microservices Complete Tutorial', 7, 2, 'APPROVED', '2025-05-17'),
(16, 'Spring Boot: Build URL Shortener Application - Complete Course (5 Hours)', 'https://www.youtube.com/watch?v=XEgS8yq-zgw', 'Spring Boot: Build URL Shortener Application - Complete Course', 7, 2, 'APPROVED', '2025-05-18'),
(17, 'Spring JdbcClient: A better alternative to JdbcTemplate', 'https://youtu.be/oF7vlaJhySE', 'Spring framework 6.1 introduced a new JdbcClient API, which is a wrapper on top of JdbcTemplate, for performing database operations using a fluent API. In this video, you will learn how to perform CRUD operations using JdbcClient and test them.', 7, 2, 'APPROVED', '2025-05-20')
;