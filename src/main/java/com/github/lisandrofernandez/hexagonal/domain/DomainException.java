package com.github.lisandrofernandez.hexagonal.domain;

import java.util.function.Supplier;

public class DomainException extends RuntimeException {
    static final long serialVersionUID = 4340911483717084069L;

    public DomainException() {
    }

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomainException(Throwable cause) {
        super(cause);
    }

    public static abstract class Assert {
        public static void isTrue(boolean expression, String message) {
            if (!expression) {
                throw new DomainException(message);
            }
        }

        public static void isTrue(boolean expression, Supplier<String> messageSupplier) {
            if (!expression) {
                throw new DomainException(messageSupplier.get());
            }
        }

        public static <T> T notNull(T object, String message) {
            if (object == null) {
                throw new DomainException(message);
            }
            return object;
        }

        public static <T> T notNull(T object, Supplier<String> messageSupplier) {
            if (object == null) {
                throw new DomainException(messageSupplier.get());
            }
            return object;
        }
    }
}
