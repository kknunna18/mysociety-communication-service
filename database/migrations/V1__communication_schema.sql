/*
 * Communication Service schema for PostgreSQL 16+.
 *
 * This script is standalone and rerunnable for an empty Communication Service
 * database. Although its versioned filename is migration-tool compatible, this
 * service does not configure a migration tool; apply it explicitly during
 * database provisioning.
 */

CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE SCHEMA IF NOT EXISTS mysociety;
SET search_path TO mysociety, public;

CREATE TABLE IF NOT EXISTS notices (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    society_id        UUID NOT NULL,
    notice_number     VARCHAR(80) NOT NULL,
    title             VARCHAR(250) NOT NULL,
    content           TEXT NOT NULL,
    category          VARCHAR(30) NOT NULL
                      CHECK (category IN ('GENERAL','EMERGENCY','MAINTENANCE','EVENT','PAYMENT','SECURITY','MEETING')),
    priority          VARCHAR(20) NOT NULL DEFAULT 'NORMAL'
                      CHECK (priority IN ('LOW','NORMAL','HIGH','URGENT')),
    status            VARCHAR(20) NOT NULL DEFAULT 'DRAFT'
                      CHECK (status IN ('DRAFT','SCHEDULED','PUBLISHED','EXPIRED','CANCELLED')),
    publish_at        TIMESTAMPTZ,
    expires_at        TIMESTAMPTZ,
    created_by        UUID,
    published_by      UUID,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version           BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_notice_dates CHECK (expires_at IS NULL OR publish_at IS NULL OR expires_at > publish_at),
    UNIQUE (society_id, notice_number)
);

CREATE TABLE IF NOT EXISTS notice_audiences (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    notice_id     UUID NOT NULL REFERENCES notices(id) ON DELETE CASCADE,
    society_id    UUID NOT NULL,
    audience_type VARCHAR(30) NOT NULL
                  CHECK (audience_type IN ('ALL','BUILDING','UNIT','ROLE','USER')),
    target_id     UUID,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_notice_target CHECK (
        (audience_type = 'ALL' AND target_id IS NULL)
        OR (audience_type <> 'ALL' AND target_id IS NOT NULL)
    ),
    UNIQUE (notice_id, audience_type, target_id)
);

CREATE TABLE IF NOT EXISTS documents (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    society_id       UUID NOT NULL,
    uploaded_by      UUID,
    category         VARCHAR(50) NOT NULL,
    file_name        VARCHAR(255) NOT NULL,
    object_key       VARCHAR(500) NOT NULL,
    content_type     VARCHAR(150),
    size_bytes       BIGINT CHECK (size_bytes IS NULL OR size_bytes >= 0),
    checksum_sha256  VARCHAR(64),
    access_level     VARCHAR(30) NOT NULL DEFAULT 'RESIDENTS'
                     CHECK (access_level IN ('PRIVATE','COMMITTEE','RESIDENTS','PUBLIC')),
    malware_status   VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                     CHECK (malware_status IN ('PENDING','CLEAN','INFECTED','FAILED')),
    expires_at       TIMESTAMPTZ,
    is_deleted       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version          BIGINT NOT NULL DEFAULT 0,
    UNIQUE (society_id, object_key)
);

CREATE TABLE IF NOT EXISTS entity_attachments (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    society_id    UUID NOT NULL,
    document_id   UUID NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    entity_type   VARCHAR(40) NOT NULL,
    entity_id     UUID NOT NULL,
    attachment_type VARCHAR(40),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (document_id, entity_type, entity_id)
);

CREATE INDEX IF NOT EXISTS ix_entity_attachments_target
    ON entity_attachments (society_id, entity_type, entity_id);

CREATE TABLE IF NOT EXISTS notification_templates (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    society_id     UUID,
    template_code  VARCHAR(80) NOT NULL,
    channel        VARCHAR(20) NOT NULL CHECK (channel IN ('IN_APP','EMAIL','SMS','PUSH')),
    subject_template VARCHAR(300),
    body_template  TEXT NOT NULL,
    is_active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version        BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_notification_templates_global
    ON notification_templates (template_code, channel) WHERE society_id IS NULL;
CREATE UNIQUE INDEX IF NOT EXISTS ux_notification_templates_society
    ON notification_templates (society_id, template_code, channel) WHERE society_id IS NOT NULL;

CREATE TABLE IF NOT EXISTS notifications (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    society_id        UUID NOT NULL,
    recipient_user_id UUID NOT NULL,
    template_code     VARCHAR(80),
    channel           VARCHAR(20) NOT NULL CHECK (channel IN ('IN_APP','EMAIL','SMS','PUSH')),
    subject           VARCHAR(300),
    message           TEXT NOT NULL,
    status            VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                      CHECK (status IN ('PENDING','SENDING','SENT','DELIVERED','FAILED','CANCELLED')),
    provider_message_id VARCHAR(180),
    attempt_count     INTEGER NOT NULL DEFAULT 0 CHECK (attempt_count >= 0),
    scheduled_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at           TIMESTAMPTZ,
    delivered_at      TIMESTAMPTZ,
    read_at           TIMESTAMPTZ,
    error_message     VARCHAR(1000),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS ix_notifications_delivery
    ON notifications (status, scheduled_at) WHERE status IN ('PENDING','FAILED');
CREATE INDEX IF NOT EXISTS ix_notifications_user
    ON notifications (recipient_user_id, created_at DESC);
