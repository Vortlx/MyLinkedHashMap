package com.solanteq.mylinkedhashmap;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MyLinkedHashMap<K, V> extends AbstractMap<K, V> {
    private MyEntry[] buckets;
    private int bucketSize;
    private int mapSize;

    private Set<K> keys;
    private Collection<V> values;

    public MyLinkedHashMap(){
        bucketSize = 16;
        mapSize = 0;
        buckets = new MyEntry[bucketSize];

        keys = new HashSet<>();
        values = new ArrayList<>();
    }

    private class MyEntry<K, V> implements Map.Entry<K, V>{
        private final K key;
        private V value;
        private MyEntry<K, V> next;
        private MyEntry<K, V> prev;

        public MyEntry(K key, V value){
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            return this.value = value;
        }

        public MyEntry<K, V> getNext() {
            return next;
        }

        public void setNext(MyEntry<K, V> next) {
            this.next = next;
        }

        public MyEntry<K, V> getPrev() {
            return prev;
        }

        public void setPrev(MyEntry<K, V> prev) {
            this.prev = prev;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MyEntry<?, ?> myEntry = (MyEntry<?, ?>) o;
            return Objects.equals(key, myEntry.key) &&
                    Objects.equals(value, myEntry.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }
    }

    @Override
    public int size() {
        return mapSize;
    }

    @Override
    public boolean isEmpty() {
        for(MyEntry<K,V> entry: buckets){
            if(entry != null)
                return false;
        }
        return true;
    }

    @Override
    public boolean containsValue(Object value) {
        for(MyEntry<K,V> entry: buckets){
            MyEntry<K, V> curEntry = entry;

            while(curEntry != null) {
                if(curEntry.getValue().equals(value))
                    return true;

                curEntry = curEntry.getNext();
            }
        }
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        MyEntry<K, V> curEntry = buckets[hash((K)key)];
        while(curEntry != null) {
            if(curEntry.getKey().equals(key))
                return true;

            curEntry = curEntry.getNext();
        }
        return false;
    }

    @Override
    public V get(Object key) {
        MyEntry<K, V> entry = getByKey((K) key);
        if(entry!= null && entry.getKey().equals(key))
            return entry.getValue();
        return null;
    }

    @Override
    public V put(K key, V value) {
        MyEntry<K, V> curEntry = buckets[hash((K) key)];
        if(curEntry == null){
            buckets[hash((K) key)] = new MyEntry<>(key, value);
            mapSize++;
        } else {
            curEntry = getByKey((K) key);
            if(curEntry != null && curEntry.getKey().equals(key)){
                V oldValue = curEntry.getValue();
                curEntry.setValue(value);
                return oldValue;
            } else {
                mapSize++;
                MyEntry<K, V> newEntry = new MyEntry<>(key, value);
                curEntry.setNext(newEntry);
                newEntry.setPrev(curEntry);
                return value;
            }
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        MyEntry<K, V> entry = getByKey((K) key);
        if(entry != null && entry.getKey().equals(key)){
            mapSize--;
            if(entry.getPrev() == null && entry.getNext() == null){
                buckets[hash((K) key)] = null;
            } else {
                MyEntry<K, V> prev = entry.getPrev();
                MyEntry<K, V> next = entry.getNext();

                if(next != null)
                    next.setPrev(prev);
            }
            return entry.getValue();
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for(Entry<? extends K, ? extends V> entry: m.entrySet()){
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        mapSize = 0;
        buckets = new MyEntry[bucketSize];
    }

    @Override
    public Set<K> keySet() {
        return this.keys;
    }

    @Override
    public Collection<V> values() {
        return this.values;
    }

    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> res = new HashSet<>();
        for(MyEntry bucetElem: buckets){
            MyEntry<K, V> entry = bucetElem;
            while(entry != null){
                res.add(entry);
                entry = entry.getNext();
            }
        }
        return res;
    }

    public V getOrDefault(Object key, V defaultValue) {
        MyEntry<K, V> entry = getByKey((K) key);
        if(entry != null && entry.getKey().equals(key))
            return entry.getValue();
        return defaultValue;
    }

    public V putIfAbsent(K key, V value) {
        MyEntry<K, V> entry = getByKey((K) key);
        if(entry == null){
            buckets[hash((K) key)] = new MyEntry(key, value);
        } else if(! entry.getKey().equals(key)) {
            MyEntry<K, V> newEntry = new MyEntry<>(key, value);
            newEntry.setPrev(entry);
            entry.setNext(newEntry);
        }

        return value;
    }

    public V replace(K key, V value) {
        MyEntry<K, V> entry = getByKey((K)key);
        if(entry != null && entry.getKey().equals(key)){
            V oldValue = entry.getValue();
            entry.setValue(value);
            return oldValue;
        }
        return null;
    }

    private int hash(K key) {
        return Math.abs(key.hashCode()) % buckets.length;
    }

    private MyEntry getByKey(K key){
        MyEntry<K, V> entry = buckets[hash((K) key)];
        while(entry != null){
            if(entry.getKey().equals(key)){
                return entry;
            }
            entry = entry.getNext();
        }
        return null;
    }

    public boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    public boolean replace(K key, V oldValue, V newValue) {
        throw new UnsupportedOperationException();
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        throw new UnsupportedOperationException();
    }

    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        throw new UnsupportedOperationException();
    }

    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        throw new UnsupportedOperationException();
    }

    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException();
    }
}