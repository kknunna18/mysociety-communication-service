package com.mysociety.communication.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;

import static com.mysociety.communication.domain.Enums.*;

@MappedSuperclass
abstract class TenantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    @Column(name = "society_id", nullable = false)
    UUID societyId;

    public UUID getId() {
        return id;
    }
}

@Entity
@Table(name = "notices", schema = "mysociety")
class Notice extends TenantEntity {
    @Column(name = "notice_number", nullable = false)
    String noticeNumber;
    @Column(nullable = false)
    String title;
    @Column(nullable = false, columnDefinition = "text")
    String content;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    NoticeCategory category;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    NoticePriority priority = NoticePriority.NORMAL;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    NoticeStatus status = NoticeStatus.DRAFT;
    Instant publishAt;
    Instant expiresAt;
    UUID createdBy;
    UUID publishedBy;
    @Column(nullable = false, updatable = false)
    Instant createdAt = Instant.now();
    @Column(nullable = false)
    Instant updatedAt = Instant.now();
    @Version
    Long version;
}

@Entity
@Table(name = "notice_audiences", schema = "mysociety")
class NoticeAudience extends TenantEntity {
    @Column(nullable = false)
    UUID noticeId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    AudienceType audienceType;
    UUID targetId;
    @Column(nullable = false, updatable = false)
    Instant createdAt = Instant.now();
}

@Entity
@Table(name = "documents", schema = "mysociety")
class Document extends TenantEntity {
    UUID uploadedBy;
    @Column(nullable = false)
    String category;
    @Column(nullable = false)
    String fileName;
    @Column(nullable = false)
    String objectKey;
    String contentType;
    Long sizeBytes;
    String checksumSha256;
    String accessLevel = "RESIDENTS";
    String malwareStatus = "PENDING";
    Instant expiresAt;
    boolean isDeleted;
    @Column(nullable = false, updatable = false)
    Instant createdAt = Instant.now();
    @Column(nullable = false)
    Instant updatedAt = Instant.now();
    @Version
    Long version;
}

@Entity
@Table(name = "entity_attachments", schema = "mysociety")
class EntityAttachment extends TenantEntity {
    @Column(nullable = false)
    UUID documentId;
    @Column(nullable = false)
    String entityType;
    @Column(nullable = false)
    UUID entityId;
    String attachmentType;
    @Column(nullable = false, updatable = false)
    Instant createdAt = Instant.now();
}

@Entity
@Table(name = "notification_templates", schema = "mysociety")
class NotificationTemplate extends TenantEntity {
    @Column(nullable = false)
    String templateCode;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Channel channel;
    String subjectTemplate;
    @Column(nullable = false, columnDefinition = "text")
    String bodyTemplate;
    @Column(nullable = false)
    boolean isActive = true;
    @Column(nullable = false, updatable = false)
    Instant createdAt = Instant.now();
    @Column(nullable = false)
    Instant updatedAt = Instant.now();
    @Version
    Long version;
}

@Entity
@Table(name = "notifications", schema = "mysociety")
class Notification extends TenantEntity {
    @Column(nullable = false)
    UUID recipientUserId;
    String templateCode;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Channel channel;
    String subject;
    @Column(nullable = false, columnDefinition = "text")
    String message;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    NotificationStatus status = NotificationStatus.PENDING;
    String providerMessageId;
    @Column(nullable = false)
    int attemptCount;
    @Column(nullable = false)
    Instant scheduledAt = Instant.now();
    Instant sentAt;
    Instant deliveredAt;
    Instant readAt;
    String errorMessage;
    @Column(nullable = false, updatable = false)
    Instant createdAt = Instant.now();
    @Column(nullable = false)
    Instant updatedAt = Instant.now();
}
