package com.mysociety.communication.web;

import java.util.*;

import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.mysociety.communication.domain.CommunicationService;

import static com.mysociety.communication.web.Api.*;
import static com.mysociety.communication.domain.Enums.*;

@RestController
@RequestMapping
public class CommunicationController {
    private final CommunicationService service;

    public CommunicationController(CommunicationService service) {
        this.service = service;
    }

    @GetMapping("/notices")
    Page<NoticeResponse> notices(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable p) {
        return service.notices(p);
    }

    @PostMapping("/notices")
    @ResponseStatus(HttpStatus.CREATED)
    NoticeResponse create(@Valid @RequestBody NoticeRequest r) {
        return service.createNotice(r);
    }

    @PutMapping("/notices/{id}")
    NoticeResponse update(@PathVariable UUID id, @Valid @RequestBody NoticeRequest r) {
        return service.updateNotice(id, r);
    }

    @PostMapping("/notices/{id}/publish")
    NoticeResponse publish(@PathVariable UUID id) {
        return service.publish(id);
    }

    @DeleteMapping("/notices/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID id) {
        service.deleteNotice(id);
    }

    @GetMapping("/notices/{id}/audiences")
    List<AudienceResponse> audiences(@PathVariable UUID id) {
        return service.audiences(id);
    }

    @PostMapping("/notices/{id}/audiences")
    @ResponseStatus(HttpStatus.CREATED)
    AudienceResponse audience(@PathVariable UUID id, @Valid @RequestBody AudienceRequest r) {
        return service.addAudience(id, r);
    }

    @DeleteMapping("/notices/{noticeId}/audiences/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void audienceDelete(@PathVariable UUID noticeId, @PathVariable UUID id) {
        service.deleteAudience(noticeId, id);
    }

    @GetMapping("/documents")
    Page<DocumentResponse> documents(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable p) {
        return service.documents(p);
    }

    @PostMapping("/documents")
    @ResponseStatus(HttpStatus.CREATED)
    DocumentResponse document(@Valid @RequestBody DocumentRequest r) {
        return service.createDocument(r);
    }

    @DeleteMapping("/documents/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void documentDelete(@PathVariable UUID id) {
        service.deleteDocument(id);
    }

    @GetMapping("/attachments")
    List<AttachmentResponse> attachments(@RequestParam String entityType, @RequestParam UUID entityId) {
        return service.attachments(entityType, entityId);
    }

    @PostMapping("/attachments")
    @ResponseStatus(HttpStatus.CREATED)
    AttachmentResponse attach(@Valid @RequestBody AttachmentRequest r) {
        return service.attach(r);
    }

    @GetMapping("/notification-templates")
    Page<TemplateResponse> templates(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable p) {
        return service.templates(p);
    }

    @PostMapping("/notification-templates")
    @ResponseStatus(HttpStatus.CREATED)
    TemplateResponse template(@Valid @RequestBody TemplateRequest r) {
        return service.createTemplate(r);
    }

    @PutMapping("/notification-templates/{id}")
    TemplateResponse templateUpdate(@PathVariable UUID id, @Valid @RequestBody TemplateRequest r) {
        return service.updateTemplate(id, r);
    }

    @GetMapping("/notifications")
    Page<NotificationResponse> notifications(@RequestParam(required = false) NotificationStatus status, @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable p) {
        return service.notifications(status, p);
    }

    @PatchMapping("/notifications/{id}/status")
    NotificationResponse status(@PathVariable UUID id, @Valid @RequestBody StatusRequest r) {
        return service.status(id, r.status());
    }
}
