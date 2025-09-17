-- Revoked Tokens table (PostgreSQL)
create table if not exists revoked_tokens (
    id bigserial primary key,
    token varchar(500) not null unique,
    expires_at timestamptz not null
);

create index if not exists idx_revoked_tokens_expires_at on revoked_tokens(expires_at);
