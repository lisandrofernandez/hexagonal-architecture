package com.github.lisandrofernandez.hexagonal.common.util;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.List;

@UtilityClass
public class CollectionUtils {

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static <E> List<E> copyToImmutableListOrEmpty(Collection<? extends E> coll) {
        return isEmpty(coll) ? List.of() : List.copyOf(coll);
    }

    public static <E> List<E> copyToImmutableListOrNull(Collection<? extends E> coll) {
        return coll == null ? null : List.copyOf(coll);
    }
}
