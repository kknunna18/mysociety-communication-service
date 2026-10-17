package com.mysociety.communication.domain;

import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import static com.mysociety.communication.domain.Enums.*;

interface NoticeRepository extends JpaRepository<Notice, UUID> {
    Page<Notice> findBySocietyId(UUID societyId, Pageable pageable);

    Optional<Notice> findByIdAndSocietyId(UUID id, UUID societyId);
}

interface AudienceRepository extends JpaRepository<NoticeAudience, UUID> {
    List<NoticeAudience> findByNoticeIdAndSocietyId(UUID noticeId, UUID societyId);

    Optional<NoticeAudience> findByIdAndNoticeIdAndSocietyId(UUID id, UUID noticeId, UUID societyId);
}

interface DocumentRepository extends JpaRepository<Document, UUID> {
    Page<Document> findBySocietyIdAndIsDeletedFalse(UUID societyId, Pageable pageable);

    Optional<Document> findByIdAndSocietyIdAndIsDeletedFalse(UUID id, UUID societyId);
}

interface AttachmentRepository extends JpaRepository<EntityAttachment, UUID> {
    List<EntityAttachment> findBySocietyIdAndEntityTypeAndEntityId(UUID societyId, String entityType, UUID entityId);
}

interface TemplateRepository extends JpaRepository<NotificationTemplate, UUID> {
    Page<NotificationTemplate> findBySocietyId(UUID societyId, Pageable pageable);

    Optional<NotificationTemplate> findByIdAndSocietyId(UUID id, UUID societyId);
}

interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findBySocietyIdAndStatus(UUID societyId, NotificationStatus status, Pageable pageable);

    Page<Notification> findBySocietyId(UUID societyId, Pageable pageable);

    Optional<Notification> findByIdAndSocietyId(UUID id, UUID societyId);
}
