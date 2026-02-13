INSERT INTO users (username, password, role, created_at, updated_at)
VALUES ('admin', '$2a$10$v437ar7Mkhi/AfKDxgFw/u9gK3LSRfo3Hv1P1YNOsMj6ZT5F5kFxa', 'ROLE_ADMIN', NOW(), null);
INSERT INTO users (username, password, role, created_at, updated_at)
VALUES ('user1', '$2a$10$v437ar7Mkhi/AfKDxgFw/u9gK3LSRfo3Hv1P1YNOsMj6ZT5F5kFxa', 'ROLE_USER', NOW(), null);
INSERT INTO users (username, password, role, created_at, updated_at)
VALUES ('user2', '$2a$10$v437ar7Mkhi/AfKDxgFw/u9gK3LSRfo3Hv1P1YNOsMj6ZT5F5kFxa', 'ROLE_USER', NOW(), null);

INSERT INTO posts (title, content, author_id, created_at, updated_at)
VALUES ('My First Tech Blog', 'This is a post about Spring Boot and MySQL.', 1, NOW(), NOW());
INSERT INTO posts (title, content, author_id, created_at, updated_at)
VALUES ('Learning JPA Relationships', 'Understanding ManyToOne and OneToMany is key.', 2, NOW(), NOW());
INSERT INTO posts (title, content, author_id, created_at, updated_at)
VALUES ('My Second Tech Blog', 'This is a post about Security in Spring.', 1, NOW(), NOW());

INSERT INTO comments (text, post_id, author_id, created_at, updated_at)
VALUES ('Great article! Thanks for sharing.', 1, 2, NOW(), NOW());

INSERT INTO comments (text, post_id, author_id, created_at, updated_at)
VALUES ('I have a question about LAZY fetching...', 2, 1, NOW(), NOW());