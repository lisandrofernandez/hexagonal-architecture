package com.github.lisandrofernandez.hexagonal.common.util.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.io.Writer;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class InstantToEpochMilliJsonSerializerTest {

    @Test
    void serializeTest() throws Exception {
        // given
        Writer writer = new StringWriter();
        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
        SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();
        Instant instant = Instant.ofEpochMilli(1619191073123L);
        InstantToEpochMilliJsonSerializer serializer = new InstantToEpochMilliJsonSerializer();

        // when
        serializer.serialize(instant, jsonGenerator, serializerProvider);
        jsonGenerator.flush();

        // then
        assertThat(writer.toString()).hasToString("1619191073123");
    }
}
