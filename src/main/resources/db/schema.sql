create table role
(
    id   serial primary key,
    role varchar(50) not null unique
);

create table person
(
    id       serial primary key,
    username varchar(50)  not null unique,
    password varchar(100) not null,
    enabled  boolean default true,
    role_id  int          not null references role (id)
);

create table room
(
    id          serial primary key,
    name        varchar(2000),
    description text,
    created     timestamp without time zone not null default now(),
    person_id   int not null references person (id)
);

create table message
(
    id        serial primary key,
    text      varchar(4000),
    created   timestamp without time zone not null default now(),
    room_id   int not null references room (id),
    person_id int not null references person (id)
);

