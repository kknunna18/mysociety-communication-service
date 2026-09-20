package com.mysociety.communication.web;
import java.time.Instant; import java.util.UUID; import jakarta.validation.constraints.*; import com.mysociety.communication.domain.Enums.*;
public final class Api {
 private Api(){}
 public record NoticeRequest(@NotBlank @Size(max=80) String noticeNumber,@NotBlank @Size(max=250) String title,@NotBlank String content,@NotNull NoticeCategory category,@NotNull NoticePriority priority,Instant publishAt,Instant expiresAt){}
 public record NoticeResponse(UUID id,String noticeNumber,String title,NoticeCategory category,NoticePriority priority,NoticeStatus status,Instant publishAt,Instant expiresAt,Long version){}
 public record AudienceRequest(@NotNull AudienceType audienceType,UUID targetId){} public record AudienceResponse(UUID id,UUID noticeId,AudienceType audienceType,UUID targetId){}
 public record DocumentRequest(@NotBlank @Size(max=50) String category,@NotBlank @Size(max=255) String fileName,@NotBlank @Size(max=500) String objectKey,@Size(max=150) String contentType,@PositiveOrZero Long sizeBytes,@Pattern(regexp="^[a-fA-F0-9]{64}$") String checksumSha256,@NotBlank String accessLevel,Instant expiresAt){}
 public record DocumentResponse(UUID id,String category,String fileName,String objectKey,String contentType,Long sizeBytes,String accessLevel,String malwareStatus,Instant expiresAt,Long version){}
 public record AttachmentRequest(@NotNull UUID documentId,@NotBlank @Size(max=40) String entityType,@NotNull UUID entityId,@Size(max=40) String attachmentType){} public record AttachmentResponse(UUID id,UUID documentId,String entityType,UUID entityId,String attachmentType){}
 public record TemplateRequest(@NotBlank @Size(max=80) String templateCode,@NotNull Channel channel,@Size(max=300) String subjectTemplate,@NotBlank String bodyTemplate,boolean isActive){} public record TemplateResponse(UUID id,String templateCode,Channel channel,String subjectTemplate,String bodyTemplate,boolean isActive,Long version){}
 public record NotificationResponse(UUID id,UUID recipientUserId,String templateCode,Channel channel,NotificationStatus status,Instant scheduledAt,Instant sentAt,Instant deliveredAt,Instant readAt){}
 public record StatusRequest(@NotNull NotificationStatus status){}
}
