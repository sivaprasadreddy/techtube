INSERT INTO users (id, email, password, name, role, created_at) VALUES
(1, 'admin@gmail.com', '$2a$10$ZuGgeoawgOg.6AM3QEGZ3O4QlBSWyRx3A70oIcBjYPpUB8mAZWY16', 'Admin', 'ROLE_ADMIN', CURRENT_TIMESTAMP),
(2, 'siva@gmail.com', '$2a$10$CIXGKN9rPfV/mmBMYas.SemoT9mfVUUwUxueFpU3DcWhuNo5fexYC', 'Siva', 'ROLE_USER', CURRENT_TIMESTAMP),
(3, 'user@gmail.com', '$2a$10$9.asbEZnVSA24cavY2xStO1FQS54WZnxUzSxqYepEoCFYAvUVnVr6', 'TestUser', 'ROLE_USER', CURRENT_TIMESTAMP)
;
