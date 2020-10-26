BEGIN;
alter table if exists wines add column if not exists actual bool;
alter table if exists wines add column if not exists date_rec timestamp;
END;