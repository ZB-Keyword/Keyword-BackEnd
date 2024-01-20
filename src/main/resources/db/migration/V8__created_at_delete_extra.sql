alter table chatroom modify created_at timestamp default current_timestamp();
alter table chat modify created_at timestamp default current_timestamp();
alter table member modify created_at timestamp default current_timestamp();
alter table friend modify created_at timestamp default current_timestamp();
alter table notice modify created_at timestamp default current_timestamp();
