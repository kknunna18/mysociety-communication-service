package com.mysociety.communication.domain;

public final class Enums {
    private Enums() {
    }

    public enum NoticeCategory {GENERAL, EMERGENCY, MAINTENANCE, EVENT, PAYMENT, SECURITY, MEETING}

    public enum NoticePriority {LOW, NORMAL, HIGH, URGENT}

    public enum NoticeStatus {DRAFT, SCHEDULED, PUBLISHED, EXPIRED, CANCELLED}

    public enum AudienceType {ALL, BUILDING, UNIT, ROLE, USER}

    public enum Channel {IN_APP, EMAIL, SMS, PUSH}

    public enum NotificationStatus {PENDING, SENDING, SENT, DELIVERED, FAILED, CANCELLED}
}
