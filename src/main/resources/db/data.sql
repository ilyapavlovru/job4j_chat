insert into role (role)
values ('ROLE_USER');
insert into role (role)
values ('ROLE_ADMIN');

insert into person (username, enabled, password, role_id)
values ('root', true, '$2a$10$v0C5DXq7VSEgnf1iBHxujONhRJAxcPqzpSzLo6AGf3MBNd8Niy/6O',
        (select id from role where role = 'ROLE_ADMIN'));

insert into room (name, person_id)
values ('Programmers chat', 1);
insert into room (name, person_id)
values ('Designers chat', 1);

insert into message(text, room_id, person_id)
values ('Первое сообщение в комнате программеров', 1, 1);
insert into message(text, room_id, person_id)
values ('Второе сообщение в комнате программеров', 1, 1);
insert into message(text, room_id, person_id)
values ('Первое сообщение в комнате дизайнеров', 2, 1);
insert into message(text, room_id, person_id)
values ('Второе сообщение в комнате дизайнеров', 2, 1);
