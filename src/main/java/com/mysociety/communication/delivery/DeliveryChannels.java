package com.mysociety.communication.delivery;
import java.util.UUID; import com.mysociety.communication.domain.Enums.Channel;
public interface DeliveryChannels {
    interface ChannelSender { Channel channel(); DeliveryResult send(DeliveryCommand command); }
    record DeliveryCommand(UUID notificationId, UUID societyId, UUID recipientUserId, String subject, String message) {}
    record DeliveryResult(boolean accepted, String providerMessageId) {}
}
