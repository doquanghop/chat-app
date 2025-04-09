create table accounts
(
    id            varchar(45) primary key,
    full_name     varchar(255),
    avatar_url    text,
    phone_number  varchar(20)  not null unique,
    user_name     varchar(20)  not null unique,
    hash_password varchar(255) not null,
    is_online     boolean default false,
    last_seen     timestamp,
    created_at    timestamp
);
create table login_sessions
(
    id               varchar(45) primary key,
    account_id       varchar(45)         not null,
    device_id        varchar(255),
    ip_address       varchar(45),
    user_agent       text,
    login_at         timestamp,
    is_online        boolean default false,
    last_seen        timestamp,
    is_active        boolean default true,
    foreign key (account_id) references accounts (id)
);
create table conversations
(
    id         varchar(45) primary key,
    type       varchar(20) not null,
    created_at timestamp
);
create table conversation_roles
(
    id              varchar(45) primary key,
    conversation_id varchar(45) not null,
    role            varchar(20) not null,
    can_send        boolean default true,
    can_read        boolean default true,
    can_delete      boolean default false,
    can_edit        boolean default false,
    foreign key (conversation_id) references conversations (id)
);
create table participants
(
    id              varchar(45) primary key,
    conversation_id varchar(45) not null,
    role_id         varchar(20) not null,
    account_id      varchar(45) not null,
    joined_at       timestamp,
    foreign key (conversation_id) references conversations (id),
    foreign key (role_id) references conversation_roles (id),
    foreign key (account_id) references accounts (id)
);
create table messages
(
    id              varchar(45) primary key,
    conversation_id varchar(45) not null,
    sender_id       varchar(45) not null,
    type            varchar(20), -- text, image, file, etc
    content         text,
    created_at      timestamp,
    foreign key (conversation_id) references conversations (id),
    foreign key (sender_id) references accounts (id)
);
create table message_reads
(
    message_id varchar(45),
    reader_id  varchar(45),
    read_at    timestamp,
    primary key (message_id, reader_id),
    foreign key (message_id) references messages (id),
    foreign key (reader_id) references accounts (id)
);