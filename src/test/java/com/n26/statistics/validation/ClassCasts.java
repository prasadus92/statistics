package com.n26.statistics.validation;

public interface ClassCasts {

    @SuppressWarnings("unchecked")
    static <T> Class<T> cast(Class<?> target) {
        return (Class<T>) target;
    }
}
