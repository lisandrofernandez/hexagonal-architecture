package com.github.lisandrofernandez.hexagonal.infrastructure.in.api.controller;

import com.github.lisandrofernandez.hexagonal.BaseFunctionalTest;
import com.github.lisandrofernandez.hexagonal.common.UuidGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static com.github.lisandrofernandez.hexagonal.infrastructure.in.api.controller.UserAccountController.BASE_URL;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserAccountControllerFT extends BaseFunctionalTest {

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

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/db/insert-test-data.sql")
    void getByIdOkTest() throws Exception {
        // given
        String id = "ed570cec-fd3b-4278-ac68-f48d115cfc87";

        // when
        mockMvc.perform(get(BASE_URL + "/{id}", id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // then
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(
                        "{"
                      + "  'content': {"
                      + "    'id': 'ed570cec-fd3b-4278-ac68-f48d115cfc87',"
                      + "    'username': 'jane.roe',"
                      + "    'name': 'Jane Roe'"
                      + "  }"
                      + "}", true)
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
                .andExpect(content().json(
                        "{"
                       + "  'error': {"
                       + "    'http_status': 404,"
                       + "    'timestamp': 1635588030126,"
                       + "    'message': 'user account not found'"
                       + "  }"
                       + "}", true)
                );
    }

    @Test
    void createUserAccountOkTest() throws Exception {
        // given
        given(uuidGenerator.generateUuid()).willReturn(UUID.fromString("3db1313b-983b-468c-9df2-f4977340d882"));

        // when
        String requestBody = "{\"username\": \"LisandroFernandez\", \"name\": \"Lisandro Fernandez\"}";
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isCreated()) // then
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(
                        "{"
                      + "  'content': {"
                      + "    'id': '3db1313b-983b-468c-9df2-f4977340d882',"
                      + "    'username': 'LisandroFernandez',"
                      + "    'name': 'Lisandro Fernandez'"
                      + "  }"
                      + "}", true)
                );
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/db/insert-test-data.sql")
    void createUserAccountAlreadyExistsTest() throws Exception {
        // given
        given(uuidGenerator.generateUuid()).willReturn(UUID.fromString("3db1313b-983b-468c-9df2-f4977340d882"));

        // when
        String requestBody = "{\"username\": \"Jane.roe\", \"name\": \"Jane B. Roe\"}";
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isBadRequest()) // then
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(
                        "{"
                      + "  'error': {"
                      + "    'http_status': 400,"
                      + "    'timestamp': 1635588030126,"
                      + "    'message': 'user account Jane.roe already exists'"
                      + "  }"
                      + "}", true)
                );
    }
}
