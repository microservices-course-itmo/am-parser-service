BEGIN;
alter table if exists wines add column if not exists actual bool;
alter table if exists wines add column if not exists date_rec timestamp;

alter table if exists brands add column if not exists actual bool;
alter table if exists brands add column if not exists date_rec timestamp;

alter table if exists colors add column if not exists actual bool;
alter table if exists colors add column if not exists date_rec timestamp;

alter table if exists countries add column if not exists actual bool;
alter table if exists countries add column if not exists date_rec timestamp;

alter table if exists grapes add column if not exists actual bool;
alter table if exists grapes add column if not exists date_rec timestamp;

alter table if exists sugar add column if not exists actual bool;
alter table if exists sugar add column if not exists date_rec timestamp;
END;