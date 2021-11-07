package com.github.lisandrofernandez.hexagonal.infrastructure.in.api.common;

import com.github.lisandrofernandez.hexagonal.common.util.Assert;
import lombok.Getter;

@Getter
public final class SingleApiResponse<T> {
    private final T content;

    private SingleApiResponse(T content) {
        this.content = Assert.notNull(content, "content must not be null");
    }

    public static <T> SingleApiResponse<T> of(T content) {
        return new SingleApiResponse<>(content);
    }
}
