CREATE TABLE IF NOT EXISTS users(
  id UUID PRIMARY KEY,
  first_name character varying(255) NOT NULL,
  last_name character varying(255) NOT NULL,
  created_at timestamp NOT NULL
);
