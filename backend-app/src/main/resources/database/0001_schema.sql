create table accounts (
    id uuid primary key,
    username varchar(30) not null unique,
    password varchar(60) not null
);

create table sessions (
    id uuid primary key,
    account_id uuid not null,
    active boolean not null,
    access_token varchar(400) not null,
    refresh_token varchar(400) not null,
    access_token_expiration timestamp not null,
    refresh_token_expiration timestamp not null,
    modification_time timestamp not null,
    constraint fk_account_access_token_account_id foreign key (account_id) references accounts(id)
);

create table files (
    id serial primary key,
    account_id uuid not null,
    uploaded_at timestamp not null,
    original_filename varchar(400) not null,
    encryption_key bytea not null,
    constraint fk_account_files_account_id foreign key (account_id) references accounts(id)
);