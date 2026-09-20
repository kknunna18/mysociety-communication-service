package com.mysociety.communication.events;
import java.util.*; import java.util.concurrent.ConcurrentHashMap; import org.springframework.stereotype.Component;
@Component
public class IdempotentEventHandler {
 private final Set<UUID> processed=ConcurrentHashMap.newKeySet();
 public boolean accept(EventEnvelope event){ if(event.eventVersion()!=1) throw new IllegalArgumentException("Unsupported event version"); return processed.add(event.eventId()); }
}
