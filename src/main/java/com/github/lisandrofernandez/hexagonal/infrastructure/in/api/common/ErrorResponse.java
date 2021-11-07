package com.github.lisandrofernandez.hexagonal.infrastructure.in.api.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.lisandrofernandez.hexagonal.common.util.Assert;
import com.github.lisandrofernandez.hexagonal.common.util.jackson.HttpStatusToValueJsonSerializer;
import com.github.lisandrofernandez.hexagonal.common.util.jackson.LocalDateTimeToEpochMilliJsonSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
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
        private LocalDateTime timestamp;
        private HttpStatus httpStatus;
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
        @JsonSerialize(using = LocalDateTimeToEpochMilliJsonSerializer.class)
        private final LocalDateTime timestamp;
        @JsonSerialize(using = HttpStatusToValueJsonSerializer.class)
        private final HttpStatus httpStatus;
        private final String code;
        private final String message;
        private final List<String> messages;

        Error(HttpStatus httpStatus, LocalDateTime timestamp, String code, String message, List<String> messages) {
            Assert.notNull(httpStatus, "HttpStatus must not be null");
            Assert.notNull(timestamp, "timestamp must not be null");
            Assert.isTrue(httpStatus.isError(), () -> "invalid error HttpStatus: " + httpStatus);

            this.timestamp = timestamp;
            this.httpStatus = httpStatus;
            this.code = code;
            this.message = message;
            this.messages = messages != null ? List.copyOf(messages) : null;
            //this.messages = ifNotNullThenElseNull(messages, List::copyOf);
        }

        Error(Builder builder) {
            this(builder.httpStatus, builder.timestamp, builder.code, builder.message, builder.messages);
        }
    }
}
