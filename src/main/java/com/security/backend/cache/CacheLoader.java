package com.security.backend.cache;

@FunctionalInterface
public interface CacheLoader<T> {

    T load();
}
