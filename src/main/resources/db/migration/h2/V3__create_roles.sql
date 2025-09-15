-- V3__create_roles.sql
create table if not exists roles (
    id smallint primary key,
    name varchar(32) not null,
    description varchar(255)
);

create unique index if not exists uk_roles_name on roles (name);

create table if not exists users_roles (
    user_id uuid not null,
    role_id smallint not null,
    primary key (user_id, role_id),
    foreign key (user_id) references users (id),
    foreign key (role_id) references roles (id)
);

insert into roles (id, name, description) values
    (1, 'ROLE_USER', 'Standard user role for general access'),
    (2, 'ROLE_ADMIN', 'Administrator role with full system access');