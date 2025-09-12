-- Users table (H2)
create table if not exists users (
                                     id uuid primary key,
                                     username varchar(64) not null,
    password_hash varchar(255) not null,
    status varchar(16) not null default 'ACTIVE',
    created_at timestamp not null default now(),
    updated_at timestamp null,
    constraint uk_users_username unique (username)
    );
