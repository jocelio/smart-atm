insert into user(id, name, login, password) values (1,'Admin','admin','spring');
insert into user(id, name, login, password) values (2,'User','casacivil','casacivil1234');

insert into role(id, name) values (1,'ROLE_USER');
insert into role(id, name) values (2,'ROLE_ADMIN');

insert into user_role(user_id, role_id) values (1,1);
insert into user_role(user_id, role_id) values (1,2);
insert into user_role(user_id, role_id) values (2,2);
