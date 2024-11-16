INSERT INTO users (email, birth_date, created_at, name, password, sex, updated_at) VALUES
    ('test@test.com', '2000-01-01', now(), 'User', 'super-secret-password', 'male', now()),
    ('test2@test.com', '2000-02-02', now(), 'User 2', 'super-secret-password', 'female', now());

INSERT INTO favorite_items (created_at, updated_at, owner_email, product_id) VALUES 
    (now(), now(), 'test@test.com', 1),
    (now(), now(), 'test@test.com', 2),
    (now(), now(), 'test@test.com', 3),
    (now(), now(), 'test@test.com', 7),
    (now(), now(), 'test@test.com', 18),
    (now(), now(), 'test2@test.com', 52),
    (now(), now(), 'test@test.com', 26),
    (now(), now(), 'test@test.com', 45),
    (now(), now(), 'test@test.com', 31),
    (now(), now(), 'test2@test.com', 128),
    (now(), now(), 'test@test.com', 200),
    (now(), now(), 'test@test.com', 108);

INSERT INTO cart_items (count, created_at, size, updated_at, owner_email, product_id) VALUES
    (1, now(), null, now(), 'test@test.com', 1),
    (1, now(), '16.5', now(), 'test@test.com', 2),
    (1, now(), '15.5', now(), 'test2@test.com', 4),
    (3, now(), null, now(), 'test@test.com', 7),
    (10, now(), null, now(), 'test@test.com', 20),
    (6, now(), '45', now(), 'test@test.com', 52),
    (8, now(), null, now(), 'test@test.com', 29),
    (1, now(), null, now(), 'test@test.com', 45),
    (1, now(), null, now(), 'test2@test.com', 31),
    (2, now(), '16', now(), 'test@test.com', 129),
    (2, now(), null, now(), 'test@test.com', 199),
    (1, now(), null, now(), 'test2@test.com', 108);

INSERT INTO order_items (count, created_at, size, status, updated_at, owner_email, product_id) VALUES
    (1, now(), '40-45', 0, now(), 'test@test.com', 98),
    (2, now(), null, 1, now(), 'test2@test.com', 80);
    