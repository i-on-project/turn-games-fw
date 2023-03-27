create schema dbo;

create table dbo.Users(
    id int generated always as identity primary key,
    username unique VARCHAR(64) not null,
    password_validation VARCHAR(256) not null,
    state VARCHAR(64) not null CHECK (state in ('OFFLINE', 'ONLINE', 'SEARCHING', 'IN_GAME')) default 'OFFLINE',
    rating int not null
);

create table dbo.Tokens(
    token_validation VARCHAR(256) primary key,
    user_id int references dbo.Users(id),
    created_at bigint not null,
    last_used_at bigint not null
);

create table dbo.Games(
    id UUID primary key not null,
    state VARCHAR(64) not null CHECK (state in ('SETUP', 'ON_GOING', 'END')) default 'SETUP',
    info jsonb not null
);

create table dbo.GamesUsers(
    gameId: UUID primary key references dbo.Games(id),
    userId: int primary key references dbo.Users(id)
);

