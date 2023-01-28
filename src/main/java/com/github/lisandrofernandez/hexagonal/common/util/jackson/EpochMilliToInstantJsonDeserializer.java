package com.github.lisandrofernandez.hexagonal.common.util.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;

public class EpochMilliToInstantJsonDeserializer extends StdDeserializer<Instant> {

    public EpochMilliToInstantJsonDeserializer() {
        this(null);
    }

    public EpochMilliToInstantJsonDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return Instant.ofEpochMilli(jsonParser.getLongValue());
    }
}
