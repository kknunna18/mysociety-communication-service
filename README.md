# MySociety Communication Service

Spring Boot 3 / Java 21 tenant-isolated API for notices, documents, templates, and notification status. It uses the existing `mysociety` schema and validates it with Hibernate; run the canonical schema script before starting.

## Local run

Copy `.env.example` values into your environment, set a high-entropy `COMMUNICATION_JWT_SECRET`, then run `gradlew.bat bootRun --args="--spring.profiles.active=local"`. JWTs must be HS256, issuer `mysociety-identity`, and include UUID `sub` and `society_id` claims. The server listens on `8086` under `/api/v1`; OpenAPI is at `/api/v1/openapi`.

## Delivery and events

`DeliveryChannels.ChannelSender` is the provider boundary for in-app, email, SMS, and push adapters. Production adapters should obtain recipient contact data from the identity service and must not log content or PII. `EventEnvelope` is the versioned Kafka/Cloud Stream contract; bind it to Kafka only via deployment configuration. The default build has no Kafka or Docker dependency. Replace the in-memory event idempotency boundary with the canonical `mysociety.idempotency_records` transactional store when enabling a consumer group.
