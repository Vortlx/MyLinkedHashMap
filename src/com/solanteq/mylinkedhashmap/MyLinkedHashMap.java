package com.solanteq.mylinkedhashmap;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MyLinkedHashMap<K, V> extends AbstractMap<K, V> {
    private MyEntry[] buckets;
    private int bucketSize;
    private int mapSize;

    private MyEntry<K, V> head;
    private MyEntry<K, V> tail;

    public MyLinkedHashMap(){
        bucketSize = 16;
        mapSize = 0;
        buckets = new MyEntry[bucketSize];
    }

    private class MyEntry<K, V> implements Map.Entry<K, V>{
        private final K key;
        private V value;
        private MyEntry<K, V> nextInBucket;
        private MyEntry<K, V> prevInBucket;

        private MyEntry<K, V> globalNext;
        private MyEntry<K, V> globalPrev;

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

        public MyEntry<K, V> getNextInBucket() {
            return nextInBucket;
        }

        public void setNextInBucket(MyEntry<K, V> nextInBucket) {
            this.nextInBucket = nextInBucket;
        }

        public MyEntry<K, V> getPrevInBucket() {
            return prevInBucket;
        }

        public void setPrevInBucket(MyEntry<K, V> prevInBucket) {
            this.prevInBucket = prevInBucket;
        }

        public MyEntry<K, V> getGlobalNext() {
            return globalNext;
        }

        public void setGlobalNext(MyEntry<K, V> globalNext) {
            this.globalNext = globalNext;
        }

        public MyEntry<K, V> getGlobalPrev() {
            return globalPrev;
        }

        public void setGlobalPrev(MyEntry<K, V> globalPrev) {
            this.globalPrev = globalPrev;
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
        return head == null;
    }

    @Override
    public boolean containsValue(Object value) {
        MyEntry<K, V> curEntry = head;

        while(curEntry != null) {
            if(curEntry.getValue().equals(value))
                return true;

            curEntry = curEntry.getGlobalNext();
        }
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        MyEntry<K, V> curEntry = buckets[hash((K)key)];
        while(curEntry != null) {
            if(curEntry.getKey().equals(key))
                return true;

            curEntry = curEntry.getNextInBucket();
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
            MyEntry<K, V> newEntry = new MyEntry<>(key, value);
            buckets[hash((K) key)] = newEntry;
            mapSize++;

            if(isEmpty()){
                head = newEntry;
                tail = head;
            } else {
                newEntry.setGlobalPrev(tail);
                tail.setGlobalNext(newEntry);
                tail = newEntry;
            }
            return value;
        } else {
            curEntry = getByKey((K) key);
            if(curEntry.getKey().equals(key)){
                V oldValue = curEntry.getValue();
                curEntry.setValue(value);
                return oldValue;
            } else {
                mapSize++;
                MyEntry<K, V> newEntry = new MyEntry<>(key, value);
                curEntry.setNextInBucket(newEntry);
                newEntry.setPrevInBucket(curEntry);

                newEntry.setGlobalPrev(tail);
                tail.setGlobalNext(newEntry);
                tail = newEntry;

                return value;
            }
        }
    }

    @Override
    public V remove(Object key) {
        MyEntry<K, V> entry = getByKey((K) key);
        if(entry != null && entry.getKey().equals(key)){
            mapSize--;

//            Remove in specific bucket
            if(entry.getPrevInBucket() == null && entry.getNextInBucket() == null){
                buckets[hash((K) key)] = null;
            } else {
                MyEntry<K, V> prev = entry.getPrevInBucket();
                MyEntry<K, V> next = entry.getNextInBucket();

                prev.setNextInBucket(next);
                if(next != null)
                    next.setPrevInBucket(prev);
            }

//            Remove in global list
            if(entry.getGlobalPrev() == null && entry.getGlobalNext() == null){
                head = tail = null;
            } else {
                MyEntry<K, V> prev = entry.getGlobalPrev();
                MyEntry<K, V> next = entry.getGlobalNext();

                prev.setGlobalNext(next);
                if(next != null)
                    next.setGlobalPrev(prev);
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
        head = tail = null;
        buckets = new MyEntry[bucketSize];
    }

    @Override
    public Set<K> keySet() {
        // ToDo Write own impl for LinkedHashSet
        Set<K> keys = new LinkedHashSet<>();
        MyEntry<K, V> elemPoint = head;
        while(elemPoint != null){
            keys.add(elemPoint.getKey());
            elemPoint = elemPoint.getGlobalNext();
        }
        return keys;
    }

    @Override
    public Collection<V> values() {
        Collection<V> values = new ArrayList<>();
        MyEntry<K, V> elemPoint = head;
        while(elemPoint != null){
            values.add(elemPoint.getValue());
            elemPoint = elemPoint.getGlobalNext();
        }
        return values;
    }

    public Set<Map.Entry<K, V>> entrySet() {
        // ToDo Write own impl for LinkedHashSet
        Set<Map.Entry<K, V>> res = new LinkedHashSet<>();
        for(MyEntry bucetElem: buckets){
            MyEntry<K, V> entry = bucetElem;
            while(entry != null){
                res.add(entry);
                entry = entry.getGlobalNext();
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
            MyEntry<K, V> newEntry = new MyEntry<>(key, value);
            buckets[hash((K) key)] = new MyEntry(key, value);

            if(head == null){
                head = tail = newEntry;
            }
        } else if(! entry.getKey().equals(key)) {
            MyEntry<K, V> newEntry = new MyEntry<>(key, value);
            entry.setNextInBucket(newEntry);
            newEntry.setPrevInBucket(entry);


            newEntry.setGlobalPrev(tail);
            tail.setGlobalNext(newEntry);
            tail = newEntry;
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
            entry = entry.getNextInBucket();
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