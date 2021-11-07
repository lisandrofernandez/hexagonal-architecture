package com.github.lisandrofernandez.hexagonal.common.util.jackson;

import com.fasterxml.jackson.core.JsonParser;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class EpochMilliToLocalDateTimeJsonDeserializerTest {

    @Test
    void deserializeTest() throws Exception {
        // given
        JsonParser jsonParser = Mockito.mock(JsonParser.class);
        given(jsonParser.getLongValue()).willReturn(1619191073123L);
        EpochMilliToLocalDateTimeJsonDeserializer deserializer = new EpochMilliToLocalDateTimeJsonDeserializer();

        // when
        LocalDateTime result = deserializer.deserialize(jsonParser, null);

        // then
        assertThat(result).isEqualTo(LocalDateTime.of(2021, 4, 23, 15, 17, 53, 123000000));
    }
}
