-- Repeatable migration, only on the dev classpath location (see application-dev.yml).
-- Dev uses an in-memory DB, so it always starts empty.
INSERT INTO tariff_plans (code, name, price, data_gb, validity_days, active, category) VALUES
('UNL299',  'Unlimited 299', 299.00, 42,  28, TRUE, 'PREPAID'),
('UNL719',  'Unlimited 719', 719.00, 126, 84, TRUE, 'PREPAID'),
('POST599', 'Postpaid 599',  599.00, 75,  30, TRUE, 'POSTPAID');
