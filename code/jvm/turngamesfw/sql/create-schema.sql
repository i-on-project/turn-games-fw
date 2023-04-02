create schema dbo;

create table dbo.Users(
    id int generated always as identity primary key,
    username unique VARCHAR(64) not null,
    password_validation VARCHAR(256) not null,
    state VARCHAR(64) not null CHECK (state in ('OFFLINE', 'ONLINE')) default 'OFFLINE'
);

create table dbo.Users_Stats(
    user_id int primary key references dbo.Users(id),
    game_id int primary key references dbo.Games(name),
    state VARCHAR(64) CHECK (state in ('INACTIVE', 'SEARCHING', 'IN_GAME')) default 'INACTIVE',
    rating int not null
);

create table dbo.Tokens(
    token_validation VARCHAR(256) primary key,
    user_id int references dbo.Users(id),
    created_at bigint not null,
    last_used_at bigint not null
);

create table dbo.Games(
    name VARCHAR(32) primary key,
    nun_players int not null,
    description VARCHAR(MAX),
    rules VARCHAR(MAX)
);

create table dbo.Matches(
    id UUID primary key not null,
    game_id int references dbo.Games(name),
    state VARCHAR(64) not null CHECK (state in ('SETUP', 'ON_GOING', 'END')),
    curr_player int references dbo.Users(id),
    curr_turn int not null default 0,
    deadline_turn TIMESTAMP,
    created TIMESTAMP default CURRENT_TIMESTAMP,
    info jsonb not null
);

create table dbo.MatchesUsers(
    matches_id UUID primary key references dbo.Matches(id),
    user_id int primary key references dbo.Users(id)
);

