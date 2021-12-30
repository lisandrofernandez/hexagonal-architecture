package com.github.lisandrofernandez.hexagonal.infrastructure.in.messaging.consumer;

import com.github.lisandrofernandez.hexagonal.BaseFunctionalTest;
import com.github.lisandrofernandez.hexagonal.infrastructure.out.persistence.jpa.repository.UserAccountJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.jdbc.Sql;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestChannelBinderConfiguration.class)
class CmdUserAccountFT extends BaseFunctionalTest {
    private static final String HEADER_EVENT_TYPE = "event-type";
    private static final String HEADER_PARTITION_KEY = "partition-key";
    private static final String EVENT_TYPE_DELETE = "DELETE";

    @Autowired
    private InputDestination inputDestination;

    @Autowired
    private OutputDestination outputDestination;

    @Autowired
    private UserAccountJpaRepository userAccountJpaRepository;

    @BeforeEach
    void setup() {
        outputDestination.clear();
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/db/insert-test-data.sql")
    void processMessageOkTest() throws Exception {
        // when a message to delete an existing user account is received
        Message<String> incomingMessage = MessageBuilder
                .withPayload("{\"username\": \"jane.roe\"}")
                .setHeader(HEADER_EVENT_TYPE, EVENT_TYPE_DELETE)
                .build();
        inputDestination.send(incomingMessage, TOPIC_NAME_CMD_USER_ACCOUNT);

        // then the user account is deleted
        String id = "ed570cec-fd3b-4278-ac68-f48d115cfc87";
        assertThat(userAccountJpaRepository.existsById(UUID.fromString(id))).isFalse();

        // and a message is sent
        Message<byte[]> outgoingMessage = outputDestination.receive(0, TOPIC_NAME_FTC_USER_ACCOUNT);
        MessageHeaders outgoingHeaders = outgoingMessage.getHeaders();
        assertThat(outgoingHeaders.get(HEADER_PARTITION_KEY)).isEqualTo(id);
        assertThat(outgoingHeaders.get(HEADER_EVENT_TYPE)).isEqualTo(EVENT_TYPE_DELETE);
        String outgoingPayload = new String(outgoingMessage.getPayload(), StandardCharsets.UTF_8);
        String expectedOutgoingPayload = """
                {
                  "id": "ed570cec-fd3b-4278-ac68-f48d115cfc87",
                  "username": "jane.roe",
                  "name": "Jane Roe"
                }
                """;
        JSONAssert.assertEquals(expectedOutgoingPayload, outgoingPayload, JSONCompareMode.STRICT);
    }

    @Test
    void processMessageWhenUserDoesNotExistTest() {
        // when a message to delete a non-existing user account is received
        Message<String> incomingMessage = MessageBuilder
                .withPayload("{\"username\": \"cosme.fulanito\"}")
                .setHeader(HEADER_EVENT_TYPE, EVENT_TYPE_DELETE)
                .build();
        inputDestination.send(incomingMessage, TOPIC_NAME_CMD_USER_ACCOUNT);

        // then no message is sent
        assertThat(outputDestination.receive(0, TOPIC_NAME_FTC_USER_ACCOUNT)).isNull();
    }
}
