package com.github.lisandrofernandez.hexagonal;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/db/clear-tables-and-init-config-data.sql")
@SqlMergeMode(MergeMode.MERGE)
@Testcontainers
public abstract class BaseFunctionalTest {
    protected static final String TOPIC_NAME_CMD_USER_ACCOUNT = "cmd.user-account";
    protected static final String TOPIC_NAME_FTC_USER_ACCOUNT = "fct.user-account";

    @Container
    static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>("postgres:11.4")
            .withDatabaseName("user_account")
            .withUrlParam("currentSchema", "hex_user_account")
            .withInitScript("db/create-schema.sql");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);
    }
}
