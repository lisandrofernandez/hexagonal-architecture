package com.github.lisandrofernandez.hexagonal.common.util.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class EpochMilliToLocalDateTimeJsonDeserializer extends StdDeserializer<LocalDateTime> {

    public EpochMilliToLocalDateTimeJsonDeserializer() {
        this(null);
    }

    public EpochMilliToLocalDateTimeJsonDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return Instant.ofEpochMilli(jsonParser.getLongValue())
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();
    }
}
