CREATE SCHEMA IF NOT EXISTS public;


-- =========================================================
-- ENUMS
-- =========================================================

CREATE TYPE level_status AS ENUM (
    'draft',
    'published',
    'unlisted',
    'deleted'
);

CREATE TYPE level_role AS ENUM (
    'owner',
    'builder',
    'decorator',
    'verifier',
    'viewer'
);

CREATE TYPE level_action AS ENUM (
    'created',
    'member_added',
    'member_removed',
    'role_changed',
    'version_created',
    'published',
    'unpublished'
);


-- =========================================================
-- USERS
-- =========================================================

CREATE TABLE public.users (
                              id uuid NOT NULL,
                              name text NOT NULL,
                              email text NOT NULL,
                              is_banned boolean NOT NULL DEFAULT false,

                              PRIMARY KEY (id),
                              UNIQUE (email)
);


-- =========================================================
-- USER CREDENTIALS
-- =========================================================

CREATE TABLE public.user_credential (
                                        user_id uuid NOT NULL,
                                        pass_hash text NOT NULL,

                                        PRIMARY KEY (user_id),

                                        CONSTRAINT fk_user_credential_user_id
                                            FOREIGN KEY (user_id)
                                                REFERENCES public.users(id)
);


-- =========================================================
-- PLAYER STATS
-- =========================================================

CREATE TABLE public.player_stats (
                                     user_id uuid NOT NULL,

                                     stars bigint NOT NULL DEFAULT 0,
                                     diamonds bigint NOT NULL DEFAULT 0,
                                     secret_coins bigint NOT NULL DEFAULT 0,
                                     demons_completed bigint NOT NULL DEFAULT 0,
                                     levels_completed bigint NOT NULL DEFAULT 0,
                                     creator_points bigint NOT NULL DEFAULT 0,

                                     PRIMARY KEY (user_id),

                                     CONSTRAINT fk_player_stats_user_id
                                         FOREIGN KEY (user_id)
                                             REFERENCES public.users(id)
);


-- =========================================================
-- DIFFICULTIES
-- =========================================================

CREATE TABLE public.difficulties (
                                     id uuid NOT NULL,
                                     name text NOT NULL,
                                     stars bigint NOT NULL,
                                     icon text NOT NULL,

                                     PRIMARY KEY (id)
);


-- =========================================================
-- LEVELS
-- =========================================================

CREATE TABLE public.levels (
                               id uuid NOT NULL,

                               name varchar NOT NULL,
                               description text,

                               song_id uuid NOT NULL,
                               difficulty_id uuid NOT NULL,

                               status level_status NOT NULL DEFAULT 'draft',

                               downloads bigint NOT NULL DEFAULT 0,
                               likes bigint NOT NULL DEFAULT 0,
                               plays bigint NOT NULL DEFAULT 0,

                               created_at timestamp NOT NULL DEFAULT NOW(),
                               updated_at timestamp NOT NULL DEFAULT NOW(),
                               published_at timestamp,

                               length smallint NOT NULL,

                               current_version uuid,

                               PRIMARY KEY (id),

                               CONSTRAINT fk_levels_difficulty_id
                                   FOREIGN KEY (difficulty_id)
                                       REFERENCES public.difficulties(id)
);


-- =========================================================
-- LEVEL MEMBERS
-- =========================================================

CREATE TABLE public.level_members (
                                      level_id uuid NOT NULL,
                                      user_id uuid NOT NULL,

                                      role level_role NOT NULL,

                                      joined_at timestamp NOT NULL DEFAULT NOW(),

                                      invited_by uuid,

                                      PRIMARY KEY (level_id, user_id),

                                      CONSTRAINT fk_level_members_level_id
                                          FOREIGN KEY (level_id)
                                              REFERENCES public.levels(id),

                                      CONSTRAINT fk_level_members_user_id
                                          FOREIGN KEY (user_id)
                                              REFERENCES public.users(id),

                                      CONSTRAINT fk_level_members_invited_by
                                          FOREIGN KEY (invited_by)
                                              REFERENCES public.users(id)
);


-- =========================================================
-- LEVEL VERSIONS
-- =========================================================

CREATE TABLE public.level_versions (
                                       id uuid NOT NULL,

                                       level_id uuid NOT NULL,

                                       version_number integer NOT NULL,

                                       created_by uuid NOT NULL,

                                       level_data bytea NOT NULL,

                                       checksum varchar NOT NULL,

                                       changelog text,

                                       created_at timestamp NOT NULL DEFAULT NOW(),

                                       PRIMARY KEY (id),

                                       UNIQUE (level_id, version_number),

                                       CONSTRAINT fk_level_versions_level_id
                                           FOREIGN KEY (level_id)
                                               REFERENCES public.levels(id),

                                       CONSTRAINT fk_level_versions_created_by
                                           FOREIGN KEY (created_by)
                                               REFERENCES public.users(id)
);


-- =========================================================
-- LEVEL HISTORY
-- =========================================================

CREATE TABLE public.level_history (
                                      id uuid NOT NULL,

                                      level_id uuid NOT NULL,

                                      actor_id uuid,

                                      action level_action NOT NULL,

                                      target_user_id uuid,

                                      version_id uuid,

                                      metadata jsonb,

                                      created_at timestamp NOT NULL DEFAULT NOW(),

                                      PRIMARY KEY (id),

                                      CONSTRAINT fk_level_history_level_id
                                          FOREIGN KEY (level_id)
                                              REFERENCES public.levels(id),

                                      CONSTRAINT fk_level_history_actor_id
                                          FOREIGN KEY (actor_id)
                                              REFERENCES public.users(id),

                                      CONSTRAINT fk_level_history_target_user_id
                                          FOREIGN KEY (target_user_id)
                                              REFERENCES public.users(id),

                                      CONSTRAINT fk_level_history_version_id
                                          FOREIGN KEY (version_id)
                                              REFERENCES public.level_versions(id)
);


-- =========================================================
-- CURRENT VERSION
-- =========================================================

ALTER TABLE public.levels
    ADD CONSTRAINT fk_levels_current_version
        FOREIGN KEY (current_version)
            REFERENCES public.level_versions(id);