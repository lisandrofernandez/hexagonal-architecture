package com.github.lisandrofernandez.hexagonal;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.List;
import java.util.Map;

@TestConfiguration
public class KafkaTestConfiguration {

    public static final String TOPIC_NAME_CMD_USER_ACCOUNT = "cmd.user-account";
    public static final String TOPIC_NAME_FCT_USER_ACCOUNT = "fct.user-account";

    @Value("${spring.cloud.stream.kafka.binder.brokers}")
    private List<String> bootstrapServers;

    @Value("${spring.cloud.stream.kafka.binder.brokers}")
    private String bootstrapServer;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public KafkaAdmin.NewTopics topics() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(TOPIC_NAME_CMD_USER_ACCOUNT)
                        .replicas(1)
                        .partitions(1)
                        .build(),
                TopicBuilder.name(TOPIC_NAME_FCT_USER_ACCOUNT)
                        .replicas(1)
                        .partitions(3)
                        .compact()
                        .build()
        );
    }

    @Bean
    public KafkaTemplate<String, String> cmdUserAccountKafkaTemplate() {
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(bootstrapServer);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(producerProps);
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setDefaultTopic(TOPIC_NAME_CMD_USER_ACCOUNT);
        return kafkaTemplate;
    }
}
