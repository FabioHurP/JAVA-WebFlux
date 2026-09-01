package com.fabio.ias.order_service.domain;


public enum ExternalStatus {
    NOT_SENT,
    PENDING,
    CONFIRMED,
    REJECTED,
    CANCELLED,
    PROCESSING,
    UNCERTAIN
}
