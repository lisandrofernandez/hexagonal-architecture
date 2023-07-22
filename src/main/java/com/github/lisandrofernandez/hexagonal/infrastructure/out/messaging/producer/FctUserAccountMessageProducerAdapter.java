package com.github.lisandrofernandez.hexagonal.infrastructure.out.messaging.producer;

import com.github.lisandrofernandez.hexagonal.common.util.Assert;
import com.github.lisandrofernandez.hexagonal.domain.model.UserAccount;
import com.github.lisandrofernandez.hexagonal.domain.port.out.command.SendUserAccountMessageCommand;
import com.github.lisandrofernandez.hexagonal.domain.port.out.messaging.EventType;
import com.github.lisandrofernandez.hexagonal.domain.port.out.messaging.FctUserAccountMessageProducer;
import com.github.lisandrofernandez.hexagonal.infrastructure.out.messaging.OutgoingMessagingException;
import com.github.lisandrofernandez.hexagonal.infrastructure.out.messaging.mapper.UserAccountPayloadMapper;
import com.github.lisandrofernandez.hexagonal.infrastructure.out.messaging.payload.UserAccountPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FctUserAccountMessageProducerAdapter implements FctUserAccountMessageProducer {

    private static final String BINDING_NAME = "fctUserAccountProducer-out-0";
    private static final String HEADER_EVENT_TYPE = "event-type";
    private static final String HEADER_PARTITION_KEY = "partition-key";

    private final UserAccountPayloadMapper userAccountPayloadMapper;
    private final StreamBridge streamBridge;

    @Override
    public void sendUserAccount(SendUserAccountMessageCommand sendUserAccountMessageCommand) {
        Assert.notNull(sendUserAccountMessageCommand, "sendUserAccountMessageCommand must not be null");

        Message<UserAccountPayload> message = buildMessage(sendUserAccountMessageCommand);
        sendMessage(message);
    }

    @Override
    public void sendDeletedUserAccount(UUID userAccountId) {
        Assert.notNull(userAccountId, "userAccountId must not be null");

        Message<KafkaNull> message = buildDeleteMessage(userAccountId);
        sendMessage(message);
    }

    private Message<UserAccountPayload> buildMessage(SendUserAccountMessageCommand sendUserAccountMessageCommand) {
        UserAccount userAccount = sendUserAccountMessageCommand.getUserAccount();
        UserAccountPayload userAccountPayload = userAccountPayloadMapper.toPayload(userAccount);
        return MessageBuilder.withPayload(userAccountPayload)
                .setHeader(HEADER_PARTITION_KEY, userAccount.getId().toString())
                .setHeader(HEADER_EVENT_TYPE, sendUserAccountMessageCommand.getEventType().name())
                .build();
    }

    private Message<KafkaNull> buildDeleteMessage(UUID userAccountId) {
        return MessageBuilder.withPayload(KafkaNull.INSTANCE)
                .setHeader(HEADER_PARTITION_KEY, userAccountId.toString())
                .setHeader(HEADER_EVENT_TYPE, EventType.DELETE.name())
                .build();
    }

    private void sendMessage(Message<?> message) {
        boolean sent;
        try {
            sent = streamBridge.send(BINDING_NAME, message);
        } catch (Exception e) {
            throw new OutgoingMessagingException("Error sending message: " + message, e);
        }
        if (!sent) {
            throw new OutgoingMessagingException("Error sending message: " + message);
        }
    }
}
