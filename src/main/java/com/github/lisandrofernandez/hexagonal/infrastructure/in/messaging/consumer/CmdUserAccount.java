package com.github.lisandrofernandez.hexagonal.infrastructure.in.messaging.consumer;

import com.github.lisandrofernandez.hexagonal.domain.DomainException;
import com.github.lisandrofernandez.hexagonal.domain.port.in.DeleteUserAccountByUsernameHandler;
import com.github.lisandrofernandez.hexagonal.infrastructure.in.messaging.IncomingMessagingException;
import com.github.lisandrofernandez.hexagonal.infrastructure.in.messaging.payload.CmdUserAccountPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class CmdUserAccount {
    private static final String HEADER_EVENT_TYPE = "event-type";
    private static final String EVENT_TYPE_DELETE = "DELETE";

    private final DeleteUserAccountByUsernameHandler deleteUserAccountByUsernameHandler;

    @Bean
    public Consumer<Message<CmdUserAccountPayload>> cmdUserAccountConsumer() {
        return this::processMessage;
    }

    private void processMessage(Message<CmdUserAccountPayload> message) {
        try {
            deleteUserAccountByUsername(message);
        } catch (DomainException e) {
            log.warn("Invalid incoming message: {}", message, e);
        } catch (Exception e) {
            String errorMessage = "Error processing incoming message: " + message;
            log.error(errorMessage, e);
            throw new IncomingMessagingException(errorMessage, e);
        }
    }

    private void deleteUserAccountByUsername(Message<CmdUserAccountPayload> message) {
        Object eventType = message.getHeaders().get(HEADER_EVENT_TYPE);

        if (!EVENT_TYPE_DELETE.equals(eventType)) {
            log.warn("Invalid event type for message: {}", message);
            return;
        }

        String username = message.getPayload().getUsername();

        if (username == null) {
            log.warn("username must not be null: {}", message);
            return;
        }

        if (!deleteUserAccountByUsernameHandler.delete(username)) {
            log.warn("User account does not exist {}", message);
        }
    }
}
