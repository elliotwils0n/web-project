create table accounts (
    id uuid primary key,
    username varchar(30) not null,
    password varchar(60) not null
);

create table access_tokens (
    account_id uuid not null,
    access_token varchar(400) not null,
    generated_at timestamp not null,
    valid_to timestamp not null,
    constraint pk_account_access_tokens primary key (account_id),
    constraint fk_account_access_token_account_id foreign key (account_id) references accounts(id)
);

create table refresh_tokens (
    account_id uuid not null,
    refresh_token varchar(400) not null,
    generated_at timestamp not null,
    valid_to timestamp not null,
    constraint fk_account_refresh_tokens primary key (account_id),
    constraint fk_account_refresh_token_account_id foreign key (account_id) references accounts(id)
);

create table files (
    id serial primary key,
    account_id uuid not null,
    uploaded_at timestamp not null,
    original_filename varchar(400) not null,
    encryption_key bytea not null,
    constraint fk_account_files_account_id foreign key (account_id) references accounts(id)
);