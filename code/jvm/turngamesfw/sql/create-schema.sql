create schema dbo;

create table dbo.Users(
    id int generated always as identity primary key,
    username VARCHAR(64) not null unique,
    password_validation VARCHAR(256) not null,
    status VARCHAR(64) not null CHECK (status in ('OFFLINE', 'ONLINE')) default 'OFFLINE'
);

create table dbo.Games(
    name VARCHAR(32) primary key,
    num_players int not null,
    description TEXT,
    rules TEXT
);

create table dbo.UserStats(
    user_id int references dbo.Users(id),
    game_name VARCHAR(32) references dbo.Games(name),
    state VARCHAR(64) CHECK (state in ('INACTIVE', 'SEARCHING', 'IN_GAME')) default 'INACTIVE',
    rating int not null,
    PRIMARY KEY (user_id, game_name)
);

create table dbo.Tokens(
    token_validation VARCHAR(256) primary key,
    user_id int references dbo.Users(id),
    created_at bigint not null,
    last_used_at bigint not null
);

create table dbo.Matches(
    id UUID primary key not null,
    game_name VARCHAR(32) references dbo.Games(name),
    state VARCHAR(64) not null CHECK (state in ('SETUP', 'ON_GOING', 'FINISHED')),
    curr_player int references dbo.Users(id),
    curr_turn int not null default 0,
    deadline_turn TIMESTAMP,
    created TIMESTAMP default CURRENT_TIMESTAMP,
    info jsonb not null
);

create table dbo.UserMatches(
    matches_id UUID references dbo.Matches(id),
    user_id int references dbo.Users(id),
    PRIMARY KEY (matches_id, user_id)
);

