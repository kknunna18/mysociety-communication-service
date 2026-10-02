package com.mysociety.communication.events;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class IdempotentEventHandlerTest {
    @Test
    void acceptsAnEventOnlyOnce() {
        var handler = new IdempotentEventHandler();
        var event = new EventEnvelope(UUID.randomUUID(), "notice.published", 1, UUID.randomUUID(), Instant.now(), new ObjectMapper().createObjectNode());
        assertThat(handler.accept(event)).isTrue();
        assertThat(handler.accept(event)).isFalse();
    }
}
