package com.github.lisandrofernandez.hexagonal.common.util.jackson;

import com.fasterxml.jackson.core.JsonParser;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class EpochMilliToInstantJsonDeserializerTest {

    @Test
    void deserializeTest() throws Exception {
        // given
        JsonParser jsonParser = Mockito.mock(JsonParser.class);
        long epochMilli = 1619191073123L;
        given(jsonParser.getLongValue()).willReturn(epochMilli);
        EpochMilliToInstantJsonDeserializer deserializer = new EpochMilliToInstantJsonDeserializer();

        // when
        Instant result = deserializer.deserialize(jsonParser, null);

        // then
        assertThat(result).isEqualTo(Instant.ofEpochMilli(epochMilli));
    }
}
