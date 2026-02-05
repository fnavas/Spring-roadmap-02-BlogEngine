INSERT INTO user (username, password, role, created_at, updated_at)
VALUES ('admin', 'scrypt_hash_here', 'ROLE_ADMIN', NOW(), NOW());
INSERT INTO user (username, password, role, created_at, updated_at)
VALUES ('jane', 'scrypt_hash_here', 'ROLE_USER', NOW(), NOW());

INSERT INTO post (title, content, author_id, created_at, updated_at)
VALUES ('My First Tech Blog', 'This is a post about Spring Boot and MySQL.', 1, NOW(), NOW());
INSERT INTO post (title, content, author_id, created_at, updated_at)
VALUES ('Learning JPA Relationships', 'Understanding ManyToOne and OneToMany is key.', 2, NOW(), NOW());

INSERT INTO comment (text, post_id, author_id, created_at, updated_at)
VALUES ('Great article! Thanks for sharing.', 1, 2, NOW(), NOW());

INSERT INTO comment (text, post_id, author_id, created_at, updated_at)
VALUES ('I have a question about LAZY fetching...', 2, 1, NOW(), NOW());