# Database provisioning

Provision PostgreSQL 16 or later by applying the Communication Service schema
explicitly:

```powershell
psql -v ON_ERROR_STOP=1 -U <user> -d <database> -f database/migrations/V1__communication_schema.sql
```

`V1__communication_schema.sql` creates the `pgcrypto` extension and the
`mysociety` schema idempotently. It is named for versioned migration
conventions, but this application does not configure Flyway or any other
migration runner. Keep `hibernate.ddl-auto=validate`; it verifies the
provisioned schema without modifying it.

## Service boundaries

The schema intentionally contains foreign keys only for data owned by this
service:

- `notice_audiences.notice_id` references `notices.id`.
- `entity_attachments.document_id` references `documents.id`.

`society_id`, user IDs (`created_by`, `published_by`, `uploaded_by`, and
`recipient_user_id`), and polymorphic `entity_id` values are UUIDs owned by
other services. Do not add cross-service database foreign keys. Validate these
identifiers at the Communication Service API boundary and reconcile lifecycle
changes through the corresponding Society and Identity service events.
