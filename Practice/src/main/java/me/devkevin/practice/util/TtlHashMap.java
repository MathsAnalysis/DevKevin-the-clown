package me.devkevin.practice.util;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 29/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class TtlHashMap<K, V> implements Map<K, V>, TtlHandler<K> {

    private final HashMap<K, Long> timestamps;
    private final HashMap<K, V> store;
    private final long ttl;

    public TtlHashMap(TimeUnit ttlUnit, long ttlValue) {
        this.timestamps = new HashMap<>();
        this.store = new HashMap<>();
        this.ttl = ttlUnit.toNanos(ttlValue);
    }

    @Override
    public V get(Object key) {
        V value = this.store.get(key);
        if (value != null && this.expired(key, value)) {
            this.store.remove(key);
            this.timestamps.remove(key);
            return null;
        }
        return value;
    }

    private boolean expired(Object key, V value) {
        return System.nanoTime() - this.timestamps.get(key) > this.ttl;
    }

    @Override
    public void onExpire(K element) {
    }

    @Override
    public long getTimestamp(K element) {
        return this.timestamps.get(element);
    }

    @Override
    public V put(K key, V value) {
        this.timestamps.put(key, System.nanoTime());
        return this.store.put(key, value);
    }

    @Override
    public int size() {
        return this.store.size();
    }

    @Override
    public boolean isEmpty() {
        return this.store.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        V value = this.store.get(key);
        if (value != null && this.expired(key, value)) {
            this.store.remove(key);
            this.timestamps.remove(key);
            return false;
        }
        return this.store.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.store.containsValue(value);
    }

    @Override
    public V remove(Object key) {
        this.timestamps.remove(key);
        return this.store.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> e : m.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        this.timestamps.clear();
        this.store.clear();
    }

    @Override
    public Set<K> keySet() {
        this.clearExpired();
        return Collections.unmodifiableSet(this.store.keySet());
    }

    @Override
    public Collection<V> values() {
        this.clearExpired();
        return Collections.unmodifiableCollection(this.store.values());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        this.clearExpired();
        return Collections.unmodifiableSet(this.store.entrySet());
    }

    private void clearExpired() {
        for (K k : this.store.keySet()) {
            this.get(k);
        }
    }
}

