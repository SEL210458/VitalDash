-- Einmaliges Setup der lokalen Entwicklungsdatenbank (PostgreSQL 16)
-- Aufruf: psql -U postgres -f db/setup.sql
CREATE ROLE vitaldash LOGIN PASSWORD 'vitaldash';
CREATE DATABASE vitaldash OWNER vitaldash ENCODING 'UTF8';
