package com.github.lisandrofernandez.hexagonal.common.util;

import java.util.function.Function;
import java.util.function.Supplier;

public final class Objects {
    private Objects() {}

    public static <T, R> R ifNotNullThenElse(T object, R thenn, R elsee) {
        return object != null ? thenn : elsee;
    }

    public static <T, R> R ifNotNullThenElse(T object, Supplier<? extends R> thenSupplier, R elsee) {
        Assert.notNull(thenSupplier, "thenSupplier must not be null");

        return object != null ? thenSupplier.get() : elsee;
    }

    public static <T, R> R ifNotNullThenElse(T object, R thenn, Supplier<? extends R> elseSupplier) {
        Assert.notNull(elseSupplier, "elseSupplier must not be null");

        return object != null ? thenn : elseSupplier.get();
    }

    public static <T, R> R ifNotNullThenElse(T object, Supplier<? extends R> thenSupplier, Supplier<? extends R> elseSupplier) {
        Assert.notNull(thenSupplier, "thenSupplier must not be null");
        Assert.notNull(elseSupplier, "elseSupplier must not be null");

        return object != null ? thenSupplier.get() : elseSupplier.get();
    }

    public static <T, R> R ifNotNullThenElse(T object, Function<? super T, ? extends R> thenMapper, R elsee) {
        Assert.notNull(thenMapper, "thenMapper must not be null");

        return object != null ? thenMapper.apply(object) : elsee;
    }

    public static <T, R> R ifNotNullThenElse(T object, Function<? super T, ? extends R> thenMapper, Supplier<? extends R> elseSupplier) {
        Assert.notNull(thenMapper, "thenMapper must not be null");
        Assert.notNull(elseSupplier, "elseSupplier must not be null");

        return object != null ? thenMapper.apply(object) : elseSupplier.get();
    }

    public static <T, R> R ifNotNullThenElseNull(T object, R thenn) {
        return ifNotNullThenElse(object, thenn, (R)null);
    }

    public static <T, R> R ifNotNullThenElseNull(T object, Supplier<? extends R> thenSupplier) {
        return ifNotNullThenElse(object, thenSupplier, (R)null);
    }

    public static <T, R> R ifNotNullThenElseNull(T object, Function<? super T, ? extends R> thenMapper) {
        return ifNotNullThenElse(object, thenMapper, (R)null);
    }
}
