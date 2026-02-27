-- V17__add_end_recurrence_date_to_transactions.sql
-- Add a column to store the end date of a recurring transaction (e.g., 3 months cycle)

ALTER TABLE fin_transactions ADD COLUMN IF NOT EXISTS end_recurrence_date TIMESTAMP;
