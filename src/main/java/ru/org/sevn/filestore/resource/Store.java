/*
 * Copyright 2020 Veronica Anokhina.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.org.sevn.filestore.resource;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author Veronica Anokhina
 */
public class Store<K, V> implements Map<K, V>{
    
    private int maxSize = 1024 * 8;
    private final TreeMap<K, V> map = new TreeMap();
    
    class Key implements Comparable<Key>{
        private final K key;
        private Date date = new Date();
        public Key(K k) {
            this.key = k;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Store.Key) {
                final Store.Key o = (Store.Key)obj;
                return this.key.equals(o.key);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        public int compareTo(Key o) {
            if (this.key.equals(o.key)) {
                return 0;
            }
            return this.date.compareTo(o.date);
        }
    }
    
    public Store() {
        this(1024 * 8);
    }
    
    public Store(final int size) {
        this.maxSize = size;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        final V res = remove(key);
        if (res != null) {
            put((K)key, res);
        }
        return res;
    }

    @Override
    public V put(K key, V value) {
        if (size() > maxSize) {
            removeFirst();
        }
        return map.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach((k, v) -> {
            put(k, v);
        });
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    private void removeFirst() {
        map.pollFirstEntry();
    }

}
