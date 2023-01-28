package com.github.lisandrofernandez.hexagonal.infrastructure.in.api.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.lisandrofernandez.hexagonal.common.util.Assert;
import com.github.lisandrofernandez.hexagonal.common.util.CollectionUtils;
import com.github.lisandrofernandez.hexagonal.common.util.jackson.HttpStatusCodeToValueJsonSerializer;
import com.github.lisandrofernandez.hexagonal.common.util.jackson.InstantToEpochMilliJsonSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatusCode;

import java.time.Instant;
import java.util.List;

@Getter
public final class ErrorResponse {
    private final Error error;

    private ErrorResponse(Error error) {
        this.error = Assert.notNull(error, "error must not be null");
    }

    public static Builder builder() {
        return new Builder();
    }

    @Getter @Setter
    @Accessors(fluent = true)
    public static final class Builder {
        private Instant timestamp;
        private HttpStatusCode httpStatusCode;
        private String code;
        private String message;
        private List<String> messages;

        Builder() {}

        public ErrorResponse build() {
            return new ErrorResponse(new Error(this));
        }
    }

    @Getter
    public static final class Error {
        @JsonSerialize(using = InstantToEpochMilliJsonSerializer.class)
        private final Instant timestamp;
        @JsonSerialize(using = HttpStatusCodeToValueJsonSerializer.class)
        private final HttpStatusCode httpStatusCode;
        private final String code;
        private final String message;
        private final List<String> messages;

        Error(HttpStatusCode httpStatusCode, Instant timestamp, String code, String message, List<String> messages) {
            Assert.notNull(httpStatusCode, "HttpStatus must not be null");
            Assert.notNull(timestamp, "timestamp must not be null");
            Assert.isTrue(httpStatusCode.isError(), () -> "invalid error HttpStatusCode: " + httpStatusCode);

            this.timestamp = timestamp;
            this.httpStatusCode = httpStatusCode;
            this.code = code;
            this.message = message;
            this.messages = CollectionUtils.copyToImmutableListOrNull(messages);
        }

        Error(Builder builder) {
            this(builder.httpStatusCode, builder.timestamp, builder.code, builder.message, builder.messages);
        }
    }
}
