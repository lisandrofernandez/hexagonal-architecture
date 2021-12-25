package com.github.lisandrofernandez.hexagonal.infrastructure.out.messaging.producer;

import com.github.lisandrofernandez.hexagonal.domain.model.UserAccount;
import com.github.lisandrofernandez.hexagonal.domain.port.out.command.SendUserAccountMessageCommand;
import com.github.lisandrofernandez.hexagonal.domain.port.out.messaging.FctUserAccountMessageProducer;
import com.github.lisandrofernandez.hexagonal.infrastructure.out.messaging.OutgoingMessagingException;
import com.github.lisandrofernandez.hexagonal.infrastructure.out.messaging.mapper.UserAccountPayloadMapper;
import com.github.lisandrofernandez.hexagonal.infrastructure.out.messaging.payload.UserAccountPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

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
        Message<UserAccountPayload> message = buildMessage(sendUserAccountMessageCommand);
        sendMessage(message);
    }

    private Message<UserAccountPayload> buildMessage(SendUserAccountMessageCommand sendUserAccountMessageCommand) {
        UserAccount userAccount = sendUserAccountMessageCommand.getUserAccount();
        UserAccountPayload userAccountPayload = userAccountPayloadMapper.toPayload(userAccount);
        return MessageBuilder
                .withPayload(userAccountPayload)
                .setHeader(HEADER_PARTITION_KEY, userAccount.getId().toString())
                .setHeader(HEADER_EVENT_TYPE, sendUserAccountMessageCommand.getEventType().name())
                .build();
    }

    private void sendMessage(Message<UserAccountPayload> message) {
        boolean sent = streamBridge.send(BINDING_NAME, message);
        if (!sent) {
            throw new OutgoingMessagingException("Error sending message: " + message);
        }
    }
}
