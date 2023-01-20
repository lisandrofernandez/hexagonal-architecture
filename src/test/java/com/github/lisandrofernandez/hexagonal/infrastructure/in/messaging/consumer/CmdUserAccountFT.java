package com.github.lisandrofernandez.hexagonal.infrastructure.in.messaging.consumer;

import com.github.lisandrofernandez.hexagonal.AbstractFunctionalTest;
import com.github.lisandrofernandez.hexagonal.infrastructure.out.persistence.jpa.repository.UserAccountJpaRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.jdbc.Sql;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class CmdUserAccountFT extends AbstractFunctionalTest {

    private static final String HEADER_EVENT_TYPE = "event-type";
    private static final String HEADER_PARTITION_KEY = "partition-key";
    private static final String EVENT_TYPE_DELETE = "DELETE";

    @Autowired
    private UserAccountJpaRepository userAccountJpaRepository;

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/db/insert-test-data.sql")
    void processMessageOkTest() throws Exception {
        // when a message to delete an existing user account is received
        Message<String> incomingMessage = MessageBuilder
                .withPayload("{\"username\": \"jane.roe\"}")
                .setHeader(HEADER_EVENT_TYPE, EVENT_TYPE_DELETE)
                .build();
        var sendResultListenableFuture = cmdUserAccountKafkaTemplate.send(incomingMessage);
        await().atMost(Duration.ofSeconds(1)).until(sendResultListenableFuture::isDone);

        // then the user account is deleted
        String id = "ed570cec-fd3b-4278-ac68-f48d115cfc87";
        await().atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> assertThat(userAccountJpaRepository.existsById(UUID.fromString(id))).isFalse());

        // and a deleted user account message is sent
        List<ConsumerRecord<String, String>> records = getConsumerRecords(TOPIC_NAME_FCT_USER_ACCOUNT);
        assertThat(records).hasSize(1);
        ConsumerRecord<String, String> record = records.get(0);
        // with a key
        assertThat(record.key()).isEqualTo(id);
        // with headers
        assertThat(getHeaders(record))
                .containsEntry(HEADER_PARTITION_KEY, id)
                .containsEntry(HEADER_EVENT_TYPE, EVENT_TYPE_DELETE);
        // with a payload
        String expectedOutgoingPayload = """
                {
                  "id": "ed570cec-fd3b-4278-ac68-f48d115cfc87",
                  "username": "jane.roe",
                  "name": "Jane Roe"
                }
                """;
        JSONAssert.assertEquals(expectedOutgoingPayload, record.value(), JSONCompareMode.STRICT);
    }

    @Test
    void processMessageWhenUserDoesNotExistTest() {
        // when a message to delete a non-existing user account is received
        Message<String> incomingMessage = MessageBuilder
                .withPayload("{\"username\": \"cosme.fulanito\"}")
                .setHeader(HEADER_EVENT_TYPE, EVENT_TYPE_DELETE)
                .build();
        var sendResultListenableFuture = cmdUserAccountKafkaTemplate.send(incomingMessage);
        await().atMost(Duration.ofSeconds(1)).until(sendResultListenableFuture::isDone);

        // then no user account message is sent
        List<ConsumerRecord<String, String>> records = getConsumerRecords(TOPIC_NAME_FCT_USER_ACCOUNT);
        assertThat(records).isEmpty();
    }
}
