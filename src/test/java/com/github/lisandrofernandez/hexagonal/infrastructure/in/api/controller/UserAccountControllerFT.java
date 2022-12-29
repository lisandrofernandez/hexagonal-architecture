package com.github.lisandrofernandez.hexagonal.infrastructure.in.api.controller;

import com.github.lisandrofernandez.hexagonal.BaseFunctionalTest;
import com.github.lisandrofernandez.hexagonal.common.UuidGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static com.github.lisandrofernandez.hexagonal.infrastructure.in.api.controller.UserAccountController.BASE_URL;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Import(TestChannelBinderConfiguration.class)
class UserAccountControllerFT extends BaseFunctionalTest {
    private static final String HEADER_EVENT_TYPE = "event-type";
    private static final String HEADER_PARTITION_KEY = "partition-key";

    @TestConfiguration
    static class FixedClockConfiguration {
        @Bean
        Clock clock() {
            return Clock.fixed(Instant.parse("2021-10-30T10:00:30.126Z"), ZoneOffset.UTC);
        }
    }

    @MockBean
    UuidGenerator uuidGenerator;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OutputDestination outputDestination;

    @BeforeEach
    void setup() {
        outputDestination.clear();
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/db/insert-test-data.sql")
    void getByIdOkTest() throws Exception {
        // given
        String id = "ed570cec-fd3b-4278-ac68-f48d115cfc87";

        // when
        mockMvc.perform(get(BASE_URL + "/{id}", id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // then
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                         {
                           "content": {
                             "id": "ed570cec-fd3b-4278-ac68-f48d115cfc87",
                             "username": "jane.roe",
                             "name": "Jane Roe"
                           }
                         }
                         """, true)
                );
    }

    @Test
    void getByIdNotFoundTest() throws Exception {
        // given
        String id = "ed570cec-fd3b-4278-ac68-f48d115cfc87";

        // when
        mockMvc.perform(get(BASE_URL + "/{id}", id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // then
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                         {
                           "error": {
                             "httpStatus": 404,
                             "timestamp": 1635588030126,
                             "message": "user account not found"
                           }
                         }
                         """, true)
                );
    }

    @Test
    void createUserAccountOkTest() throws Exception {
        // given
        given(uuidGenerator.generateUuid()).willReturn(UUID.fromString("3db1313b-983b-468c-9df2-f4977340d882"));

        // when
        String requestBody = """
            {"username": "EduardoCoudet", "name": "Eduardo Coudet"}
            """;
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isCreated()) // then
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                          "content": {
                            "id": "3db1313b-983b-468c-9df2-f4977340d882",
                            "username": "EduardoCoudet",
                            "name": "Eduardo Coudet"
                          }
                        }
                        """ , true)
                );

        // and a message is sent
        Message<byte[]> outgoingMessage = outputDestination.receive(0, TOPIC_NAME_FTC_USER_ACCOUNT);
        MessageHeaders outgoingHeaders = outgoingMessage.getHeaders();
        assertThat(outgoingHeaders.get(HEADER_PARTITION_KEY)).isEqualTo("3db1313b-983b-468c-9df2-f4977340d882");
        assertThat(outgoingHeaders.get(HEADER_EVENT_TYPE)).isEqualTo("CREATE");
        String outgoingPayload = new String(outgoingMessage.getPayload(), StandardCharsets.UTF_8);
        String expectedOutgoingPayload = """
                {
                  "id": "3db1313b-983b-468c-9df2-f4977340d882",
                  "username": "EduardoCoudet",
                  "name": "Eduardo Coudet"
                }
                """;
        JSONAssert.assertEquals(expectedOutgoingPayload, outgoingPayload, JSONCompareMode.STRICT);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/db/insert-test-data.sql")
    void createUserAccountAlreadyExistsTest() throws Exception {
        // given
        given(uuidGenerator.generateUuid()).willReturn(UUID.fromString("3db1313b-983b-468c-9df2-f4977340d882"));

        // when
        String requestBody = """
            {"username": "Jane.roe", "name": "Jane B. Roe"}
            """;
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isBadRequest()) // then
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                          "error": {
                            "httpStatus": 400,
                            "timestamp": 1635588030126,
                            "message": "user account Jane.roe already exists"
                          }
                        }
                        """, true)
                );

        // and no message is sent
        assertThat(outputDestination.receive(0, TOPIC_NAME_FTC_USER_ACCOUNT)).isNull();
    }
}
