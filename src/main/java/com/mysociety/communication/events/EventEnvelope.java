package com.mysociety.communication.events;
import java.time.Instant; import java.util.UUID; import com.fasterxml.jackson.databind.JsonNode; import jakarta.validation.constraints.*;
public record EventEnvelope(@NotNull UUID eventId,@NotBlank String eventType,@Positive int eventVersion,@NotNull UUID societyId,@NotNull Instant occurredAt,@NotNull JsonNode payload) {}
