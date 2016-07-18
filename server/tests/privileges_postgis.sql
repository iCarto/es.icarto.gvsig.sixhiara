-- Test create_owner_user_and_permissions

\set ECHO none
\set QUIET 1
-- Turn off echo and keep things quiet.

-- Format the output for nice TAP.
\pset format unaligned
\pset tuples_only true
\pset pager

-- Revert all changes on failure.
\set ON_ERROR_ROLLBACK 1
\set ON_ERROR_STOP true
\set QUIET 1

-- Load the TAP functions.
SET client_min_messages TO warning;
CREATE EXTENSION IF NOT EXISTS pgtap;
RESET client_min_messages;

BEGIN;

SELECT plan(3);

SELECT table_owner_is ( 'public', 'spatial_ref_sys', 'sixhiara_owner');
SELECT view_owner_is ( 'public', 'geometry_columns', 'sixhiara_owner');
SELECT view_owner_is ( 'public', 'geography_columns', 'sixhiara_owner');

SELECT finish();

ROLLBACK;
