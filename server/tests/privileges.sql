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

SELECT plan(6);

SELECT users_are(ARRAY[ 'postgres', 'sixhiara_owner', ]);

SELECT db_owner_is( current_database(), 'sixhiara_owner' );

SELECT schema_owner_is ( 'public', 'sixhiara_owner' );

SELECT database_privs_are ( current_database(), 'public', ARRAY[]);
SELECT database_privs_are ( current_database(), 'sixhiara_owner', ARRAY['CREATE', 'CONNECT', 'TEMPORARY']);

SELECT schema_privs_are( 'public', 'public', ARRAY[] );

SELECT schema_privs_are( 'public', 'sixhiara_owner', ARRAY['CREATE', 'USAGE'] );

SELECT finish();

ROLLBACK;
