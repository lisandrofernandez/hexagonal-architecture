package com.github.lisandrofernandez.hexagonal.common.util.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LocalDateTimeToEpochMilliJsonSerializerTest {

    @Test
    void serializeTest() throws Exception {
        // given
        Writer writer = new StringWriter();
        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
        SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();
        LocalDateTime ldt = LocalDateTime.of(2021, 4, 23, 15, 17, 53, 123456789);
        LocalDateTimeToEpochMilliJsonSerializer serializer = new LocalDateTimeToEpochMilliJsonSerializer();

        // when
        serializer.serialize(ldt, jsonGenerator, serializerProvider);
        jsonGenerator.flush();

        // then
        assertThat(writer.toString()).hasToString("1619191073123");
    }
}
