package com.github.lisandrofernandez.hexagonal.infrastructure.in.api.controller;

import com.github.lisandrofernandez.hexagonal.AbstractFunctionalTest;
import com.github.lisandrofernandez.hexagonal.FixedClockTestConfiguration;
import com.github.lisandrofernandez.hexagonal.common.UuidGenerator;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.UUID;

import static com.github.lisandrofernandez.hexagonal.infrastructure.in.api.controller.UserAccountController.BASE_URL;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = FixedClockTestConfiguration.class)
class UserAccountControllerFT extends AbstractFunctionalTest {

    private static final String HEADER_EVENT_TYPE = "event-type";
    private static final String HEADER_PARTITION_KEY = "partition-key";

    @MockBean
    private UuidGenerator uuidGenerator;

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/db/insert-test-data.sql")
    void getByIdOkTest() {
        // given there exists initial DB data
        // and given
        String id = "ed570cec-fd3b-4278-ac68-f48d115cfc87";

        // when requesting an existing user account by its ID
        webTestClient.get().uri(BASE_URL + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk() // then
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json("""
                        {
                          "content": {
                            "id": "ed570cec-fd3b-4278-ac68-f48d115cfc87",
                            "username": "jane.roe",
                            "name": "Jane Roe"
                          }
                        }
                        """, true);
    }

    @Test
    void getByIdNotFoundTest() {
        // given
        String id = "ed570cec-fd3b-4278-ac68-f48d115cfc87";

        // when requesting a non-existing user account by an ID
        webTestClient.get().uri(BASE_URL + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound() // then
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json("""
                        {
                          "error": {
                            "httpStatus": 404,
                            "timestamp": 1635588030126,
                            "message": "user account not found"
                          }
                        }
                        """, true);
    }

    @Test
    void createUserAccountOkTest() throws Exception {
        // given
        String id = "3db1313b-983b-468c-9df2-f4977340d882";
        given(uuidGenerator.generateUuid()).willReturn(UUID.fromString(id));

        // when requesting the creation of a user account
        String requestBody = """
                {"username": "EduardoCoudet", "name": "Eduardo Coudet"}
                """;
        webTestClient.post().uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk() // then
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json("""
                        {
                          "content": {
                            "id": "3db1313b-983b-468c-9df2-f4977340d882",
                            "username": "EduardoCoudet",
                            "name": "Eduardo Coudet"
                          }
                        }
                        """, true);

        // and a created user account message is sent
        List<ConsumerRecord<String, String>> records = getConsumerRecords(TOPIC_NAME_FCT_USER_ACCOUNT);
        assertThat(records).hasSize(1);
        ConsumerRecord<String, String> record = records.get(0);
        // with a key
        assertThat(record.key()).isEqualTo(id);
        // with headers
        assertThat(getHeaders(record))
                .containsEntry(HEADER_PARTITION_KEY, id)
                .containsEntry(HEADER_EVENT_TYPE, "CREATE");
        // with a payload
        String expectedOutgoingPayload = """
                {
                  "id": "3db1313b-983b-468c-9df2-f4977340d882",
                  "username": "EduardoCoudet",
                  "name": "Eduardo Coudet"
                }
                """;
        JSONAssert.assertEquals(expectedOutgoingPayload, record.value(), JSONCompareMode.STRICT);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/db/insert-test-data.sql")
    void createUserAccountAlreadyExistsTest() {
        // given there exists initial DB data
        // and given
        given(uuidGenerator.generateUuid()).willReturn(UUID.fromString("3db1313b-983b-468c-9df2-f4977340d882"));

        // when requesting the creation of an existing user account
        String requestBody = """
                {"username": "Jane.roe", "name": "Jane B. Roe"}
                """;
        webTestClient.post().uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest() // then
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json("""
                        {
                          "error": {
                            "httpStatus": 400,
                            "timestamp": 1635588030126,
                            "message": "user account Jane.roe already exists"
                          }
                        }
                        """, true);

        // and no user account message is sent
        List<ConsumerRecord<String, String>> records = getConsumerRecords(TOPIC_NAME_FCT_USER_ACCOUNT);
        assertThat(records).isEmpty();
    }
}
