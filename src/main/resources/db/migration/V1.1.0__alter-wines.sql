ALTER TABLE wines
    ALTER COLUMN import_id SET NOT NULL,
    ALTER COLUMN name SET NOT NULL,
    ALTER COLUMN price DROP NOT NULL,
    ALTER COLUMN strength DROP NOT NULL,
    ALTER COLUMN volume DROP NOT NULL;