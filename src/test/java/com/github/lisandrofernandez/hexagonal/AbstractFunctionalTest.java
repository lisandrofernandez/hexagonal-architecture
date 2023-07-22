package com.github.lisandrofernandez.hexagonal;

import com.github.lisandrofernandez.hexagonal.infrastructure.out.persistence.jpa.repository.UserAccountJpaRepository;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.StreamSupport;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = KafkaTestConfiguration.class)
@AutoConfigureWebTestClient
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/db/clear-tables-and-init-config-data.sql")
@SqlMergeMode(MergeMode.MERGE)
@ActiveProfiles("test")
@Testcontainers
public abstract class AbstractFunctionalTest {

    protected static final String TOPIC_NAME_FCT_USER_ACCOUNT = KafkaTestConfiguration.TOPIC_NAME_FCT_USER_ACCOUNT;

    @Container
    static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = createPostgreSqlContainer();

    @Container
    static final KafkaContainer KAFKA_CONTAINER = createKafkaContainer();

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);

        registry.add("spring.cloud.stream.kafka.binder.brokers", KAFKA_CONTAINER::getBootstrapServers);
    }

    @Value("${spring.cloud.stream.kafka.binder.brokers}")
    private String bootstrapServer;

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected UserAccountJpaRepository userAccountJpaRepository;

    @Autowired
    protected KafkaTemplate<String, String> cmdUserAccountKafkaTemplate;

    private static PostgreSQLContainer<?> createPostgreSqlContainer() {
        return new PostgreSQLContainer<>("postgres:15.3-alpine")
                .withDatabaseName("user_account")
                .withUrlParam("currentSchema", "hex_user_account")
                .withInitScript("db/create-schema.sql");
    }

    private static KafkaContainer createKafkaContainer() {
        return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
                .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "false");
    }

    protected void createTopics(Collection<NewTopic> newTopics) {
        try (Admin admin = createAdmin()) {
            CreateTopicsResult topicResults = admin.createTopics(newTopics);
            try {
                topicResults.all().get(5, TimeUnit.SECONDS);
            } catch (ExecutionException e) {
                throw new IllegalStateException("Failed to create topics", e);
            } catch (InterruptedException e) {
                throw new IllegalStateException("Interrupted while waiting for topic creation results", e);
            } catch (TimeoutException e) {
                throw new IllegalStateException("Timed out waiting for create topics results", e);
            }
        }
    }

    protected void deleteTopics(Collection<String> topicNames) {
        try (Admin admin = createAdmin()) {
            DeleteTopicsResult deleteTopicsResult = admin.deleteTopics(topicNames);
            try {
                deleteTopicsResult.all().get(5, TimeUnit.SECONDS);
            } catch (ExecutionException e) {
                throw new IllegalStateException("Failed to delete topics", e);
            } catch (InterruptedException e) {
                throw new IllegalStateException("Interrupted while waiting for topic deletion results", e);
            } catch (TimeoutException e) {
                throw new IllegalStateException("Timed out waiting for delete topics results", e);
            }
        }
    }

    protected Admin createAdmin() {
        Map<String, Object> config = Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        return Admin.create(config);
    }

    protected List<ConsumerRecord<String, String>> getConsumerRecords(String topicName) {
        return getConsumerRecords(topicName, Duration.ofSeconds(1));
    }

    protected List<ConsumerRecord<String, String>> getConsumerRecords(String topicName, Duration timeout) {
        KafkaConsumer<String, String> kafkaConsumer = createKafkaConsumer(topicName);
        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(kafkaConsumer, timeout);
        kafkaConsumer.close();
        return StreamSupport.stream(records.records(topicName).spliterator(), false).toList();
    }

    private KafkaConsumer<String, String> createKafkaConsumer(String topicName) {
        String consumerGroup = "test-consumer-group";
        Map<String, Object> props = KafkaTestUtils.consumerProps(bootstrapServer, consumerGroup, "true");
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(props, new StringDeserializer(), new StringDeserializer());
        kafkaConsumer.subscribe(List.of(topicName));
        return kafkaConsumer;
    }

    public static Map<String, String> getHeaders(ConsumerRecord<String, String> consumerRecord) {
        HashMap<String, String> headers = new HashMap<>();
        for (Header header : consumerRecord.headers()) {
            headers.put(header.key(), new String(header.value(), StandardCharsets.UTF_8));
        }
        return headers;
    }
}
