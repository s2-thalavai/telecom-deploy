-- Backward-compatible (expand) change: nullable column with default, so the previous
-- app version keeps working during a rolling deploy and after a rollback.
ALTER TABLE tariff_plans ADD COLUMN category VARCHAR(20) DEFAULT 'PREPAID';
