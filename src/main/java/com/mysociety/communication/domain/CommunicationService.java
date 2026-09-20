package com.mysociety.communication.domain;

import java.time.Instant;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.mysociety.communication.tenant.TenantContext;
import com.mysociety.communication.web.Api.*;
import static com.mysociety.communication.domain.Enums.*;

@Service
@Transactional
public class CommunicationService {
    private final TenantContext tenant; private final NoticeRepository notices; private final AudienceRepository audiences;
    private final DocumentRepository documents; private final AttachmentRepository attachments; private final TemplateRepository templates; private final NotificationRepository notifications;
    CommunicationService(TenantContext tenant, NoticeRepository notices, AudienceRepository audiences, DocumentRepository documents, AttachmentRepository attachments, TemplateRepository templates, NotificationRepository notifications) {
        this.tenant=tenant; this.notices=notices; this.audiences=audiences; this.documents=documents; this.attachments=attachments; this.templates=templates; this.notifications=notifications;
    }
    public Page<NoticeResponse> notices(Pageable page) { return notices.findBySocietyId(tenant.societyId(), page).map(this::notice); }
    public NoticeResponse createNotice(NoticeRequest r) {
        Notice n=new Notice(); n.societyId=tenant.societyId(); n.noticeNumber=r.noticeNumber(); n.title=r.title(); n.content=r.content(); n.category=r.category(); n.priority=r.priority(); n.publishAt=r.publishAt(); n.expiresAt=r.expiresAt(); n.createdBy=tenant.userId();
        return notice(notices.save(n));
    }
    public NoticeResponse updateNotice(UUID id, NoticeRequest r) {
        Notice n=noticeEntity(id); n.noticeNumber=r.noticeNumber(); n.title=r.title(); n.content=r.content(); n.category=r.category(); n.priority=r.priority(); n.publishAt=r.publishAt(); n.expiresAt=r.expiresAt(); n.updatedAt=Instant.now(); return notice(n);
    }
    public NoticeResponse publish(UUID id) { Notice n=noticeEntity(id); n.status=NoticeStatus.PUBLISHED; n.publishAt=Instant.now(); n.publishedBy=tenant.userId(); n.updatedAt=Instant.now(); return notice(n); }
    public void deleteNotice(UUID id) { notices.delete(noticeEntity(id)); }
    public List<AudienceResponse> audiences(UUID noticeId) { noticeEntity(noticeId); return audiences.findByNoticeIdAndSocietyId(noticeId, tenant.societyId()).stream().map(this::audience).toList(); }
    public AudienceResponse addAudience(UUID noticeId, AudienceRequest r) {
        noticeEntity(noticeId); if ((r.audienceType()==AudienceType.ALL) != (r.targetId()==null)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ALL audiences must not have targetId");
        NoticeAudience a=new NoticeAudience(); a.societyId=tenant.societyId(); a.noticeId=noticeId; a.audienceType=r.audienceType(); a.targetId=r.targetId(); return audience(audiences.save(a));
    }
    public void deleteAudience(UUID noticeId, UUID id) { audiences.delete(audiences.findByIdAndNoticeIdAndSocietyId(id, noticeId, tenant.societyId()).orElseThrow(() -> notFound("Audience"))); }
    public Page<DocumentResponse> documents(Pageable p) { return documents.findBySocietyIdAndIsDeletedFalse(tenant.societyId(),p).map(this::document); }
    public DocumentResponse createDocument(DocumentRequest r) {
        Document d=new Document(); d.societyId=tenant.societyId(); d.uploadedBy=tenant.userId(); d.category=r.category(); d.fileName=r.fileName(); d.objectKey=r.objectKey(); d.contentType=r.contentType(); d.sizeBytes=r.sizeBytes(); d.checksumSha256=r.checksumSha256(); d.accessLevel=r.accessLevel(); d.expiresAt=r.expiresAt(); return document(documents.save(d));
    }
    public void deleteDocument(UUID id) { Document d=documentEntity(id); d.isDeleted=true; d.updatedAt=Instant.now(); }
    public List<AttachmentResponse> attachments(String entityType, UUID entityId) { return attachments.findBySocietyIdAndEntityTypeAndEntityId(tenant.societyId(), entityType, entityId).stream().map(this::attachment).toList(); }
    public AttachmentResponse attach(AttachmentRequest r) { documentEntity(r.documentId()); EntityAttachment a=new EntityAttachment(); a.societyId=tenant.societyId(); a.documentId=r.documentId(); a.entityType=r.entityType(); a.entityId=r.entityId(); a.attachmentType=r.attachmentType(); return attachment(attachments.save(a)); }
    public Page<TemplateResponse> templates(Pageable p) { return templates.findBySocietyId(tenant.societyId(),p).map(this::template); }
    public TemplateResponse createTemplate(TemplateRequest r) { NotificationTemplate t=new NotificationTemplate(); t.societyId=tenant.societyId(); t.templateCode=r.templateCode(); t.channel=r.channel(); t.subjectTemplate=r.subjectTemplate(); t.bodyTemplate=r.bodyTemplate(); t.isActive=r.isActive(); return template(templates.save(t)); }
    public TemplateResponse updateTemplate(UUID id, TemplateRequest r) { NotificationTemplate t=templates.findByIdAndSocietyId(id,tenant.societyId()).orElseThrow(()->notFound("Template")); t.templateCode=r.templateCode();t.channel=r.channel();t.subjectTemplate=r.subjectTemplate();t.bodyTemplate=r.bodyTemplate();t.isActive=r.isActive();t.updatedAt=Instant.now();return template(t); }
    public Page<NotificationResponse> notifications(NotificationStatus status, Pageable p) { UUID s=tenant.societyId(); return (status==null?notifications.findBySocietyId(s,p):notifications.findBySocietyIdAndStatus(s,status,p)).map(this::notification); }
    public NotificationResponse status(UUID id, NotificationStatus status) { Notification n=notifications.findByIdAndSocietyId(id,tenant.societyId()).orElseThrow(()->notFound("Notification")); n.status=status;n.updatedAt=Instant.now(); if(status==NotificationStatus.DELIVERED)n.deliveredAt=Instant.now(); return notification(n); }
    private Notice noticeEntity(UUID id){return notices.findByIdAndSocietyId(id,tenant.societyId()).orElseThrow(()->notFound("Notice"));}
    private Document documentEntity(UUID id){return documents.findByIdAndSocietyIdAndIsDeletedFalse(id,tenant.societyId()).orElseThrow(()->notFound("Document"));}
    private ResponseStatusException notFound(String name){return new ResponseStatusException(HttpStatus.NOT_FOUND,name+" not found");}
    private NoticeResponse notice(Notice n){return new NoticeResponse(n.id,n.noticeNumber,n.title,n.category,n.priority,n.status,n.publishAt,n.expiresAt,n.version);}
    private AudienceResponse audience(NoticeAudience a){return new AudienceResponse(a.id,a.noticeId,a.audienceType,a.targetId);}
    private DocumentResponse document(Document d){return new DocumentResponse(d.id,d.category,d.fileName,d.objectKey,d.contentType,d.sizeBytes,d.accessLevel,d.malwareStatus,d.expiresAt,d.version);}
    private AttachmentResponse attachment(EntityAttachment a){return new AttachmentResponse(a.id,a.documentId,a.entityType,a.entityId,a.attachmentType);}
    private TemplateResponse template(NotificationTemplate t){return new TemplateResponse(t.id,t.templateCode,t.channel,t.subjectTemplate,t.bodyTemplate,t.isActive,t.version);}
    private NotificationResponse notification(Notification n){return new NotificationResponse(n.id,n.recipientUserId,n.templateCode,n.channel,n.status,n.scheduledAt,n.sentAt,n.deliveredAt,n.readAt);}
}
