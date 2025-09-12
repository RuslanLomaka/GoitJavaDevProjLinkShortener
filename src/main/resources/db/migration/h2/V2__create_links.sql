-- Links table (H2)
create table if not exists links (
                                     id uuid primary key,
                                     code varchar(12) not null,
    original_url text not null,
    owner_id uuid not null,
    created_at timestamp not null default now(),
    expires_at timestamp null,
    clicks bigint not null default 0,
    last_accessed_at timestamp null,
    status varchar(16) not null default 'ACTIVE',
    constraint uk_links_code unique (code),
    constraint fk_links_owner
    foreign key (owner_id) references users(id)
    on delete cascade
    );

create index if not exists idx_links_owner_id   on links(owner_id);
create index if not exists idx_links_status     on links(status);
create index if not exists idx_links_expires_at on links(expires_at);
