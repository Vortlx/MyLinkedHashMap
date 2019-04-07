package com.solanteq.mylinkedhashmap;

import org.junit.Before;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MyLinkedHashMapTest {

    Map<Integer, String> map;

    @BeforeEach
    void initAll(){
         map = new MyLinkedHashMap<>();
    }

    @Test
    void size() {
        assertEquals(0, map.size());

        map.put(1, "Test");
        assertEquals(1, map.size());

        map.put(1, "Test23");
        assertEquals(1, map.size());

        map.put(2, "Other test");
        assertEquals(2, map.size());

        map.remove(2);
        assertEquals(1, map.size());

        map.remove(1);
        assertEquals(0, map.size());
    }

    @Test
    void isEmpty() {
        assertTrue(map.isEmpty());

        map.put(1, "1");
        assertFalse(map.isEmpty());

        map.put(2, "1");
        assertFalse(map.isEmpty());

        map.remove(2);
        assertFalse(map.isEmpty());

        map.remove(1);
        assertTrue(map.isEmpty());
    }

    @Test
    void containsValue() {
        String value = "test";
        assertFalse(map.containsValue(value));

        map.put(1, value);
        assertTrue(map.containsValue(value));

        map.remove(1);
        assertFalse(map.containsValue(value));
    }

    @Test
    void containsKey() {
        int key = 1;
        assertFalse(map.containsKey(key));

        map.put(key, "Test");
        assertTrue(map.containsKey(key));

        map.remove(key);
        assertFalse(map.containsKey(key));
    }

    @Test
    void get() {
        String expVal = "Test val";
        map.put(1, expVal);
        assertEquals(expVal, map.get(1));

        map.put(2, expVal);
        assertEquals(expVal, map.get(2));

        String expVal2 = "222";
        map.put(3, expVal2);
        assertEquals(expVal2, map.get(3));

        map.put(3, expVal2);
        assertEquals(expVal2, map.get(3));

        map.put(4, expVal2);
        assertEquals(expVal2, map.get(4));
    }

    @Test
    @Disabled("What are best practices for testing this kind of methods?")
    void put(){
    }

    @Test
    void remove() {
        String testVal = "testVal";

        assertFalse(map.containsValue(testVal));
        assertFalse(map.containsKey(1));
        map.put(1, testVal);

        assertTrue(map.containsValue(testVal));
        assertTrue(map.containsKey(1));

        map.remove(1);
        assertFalse(map.containsValue(testVal));
        assertFalse(map.containsKey(1));
    }

    @Test
    void putAll() {
        int[] keys = {1, 2, 3, 4, 5};
        String[] vals = {"1", "2", "3", "4", "5"};
        Map<Integer, String> someMap = new HashMap<>();

        for(int i = 0; i < keys.length; i++){
            someMap.put(keys[i], vals[i]);
        }

        map.putAll(someMap);

        for(int i = 0; i < keys.length; i++){
            assertTrue(map.containsKey(keys[i]));
            assertTrue(map.containsValue(vals[i]));
            assertEquals(vals[i], map.get(keys[i]));
        }
    }

    @Test
    void clear() {
        assertTrue(map.isEmpty());
        map.put(1, "Test1");
        map.put(1, "Test2");
        map.put(3, "Test3");
        map.put(4, "Test4");
        assertFalse(map.isEmpty());

        map.clear();
        assertTrue(map.isEmpty());
    }

    @Test
    void keySet(){
        String[] vals = {"1", "2", "3", "4", "5", "6", "7", "1", "2", "3"};
        int keyGap = 7;
        for(int i = 0; i < vals.length; i+=keyGap){
            map.put(i, vals[i]);
        }

        Set<Integer> keys = map.keySet();
        int keyIdx = 0;
        for(Integer key: keys){
            assertEquals(keyIdx, key);
            keyIdx += keyGap;
        }
    }

    @Test
    void values(){
        String[] vals = {"1", "2", "3", "4", "5", "6", "7", "1", "2", "3"};
        for(int i = 0; i < vals.length; i++){
            map.put(i + 1, vals[i]);
        }

        Iterator<String> mapValsIterator = map.values().iterator();
        int valsIdx = 0;
        while(mapValsIterator.hasNext()){
            assertEquals(vals[valsIdx], mapValsIterator.next());
            valsIdx++;
        }
    }

    @Test
    void getOrDefault() {
        String defaultVal = "defaultValue";
        assertEquals(defaultVal, map.getOrDefault(1, defaultVal));

        map.put(1, "33");
        assertNotEquals(defaultVal, map.getOrDefault(1, defaultVal));

        map.remove(1);
        assertEquals(defaultVal, map.getOrDefault(1, defaultVal));
    }

    @Test
    void putIfAbsent() {
        assertFalse(map.containsKey(1));

        map.putIfAbsent(1, "1");
        assertEquals("1", map.get(1));

        map.putIfAbsent(1, "2");
        assertEquals("1", map.get(1));
    }

    @Test
    void replace() {
        map.put(1, "1");
        assertEquals("1", map.get(1));

        map.replace(1, "2");
        assertEquals("2", map.get(1));
    }

    @Test
    void remove1() {
        assertThrows(UnsupportedOperationException.class, () -> map.remove(1, "Value"));
    }

    @Test
    void replace1() {
        assertThrows(UnsupportedOperationException.class, () -> map.replace(1, "OldValue", "NewValue"));
    }

    @Test
    void forEach() {
        assertThrows(UnsupportedOperationException.class, () -> map.forEach(null));
    }

    @Test
    void replaceAll() {
        assertThrows(UnsupportedOperationException.class, () -> map.replaceAll(null));
    }

    @Test
    void computeIfAbsent() {
        assertThrows(UnsupportedOperationException.class, () -> map.computeIfAbsent(1, null));
    }

    @Test
    void computeIfPresent() {
        assertThrows(UnsupportedOperationException.class, () -> map.computeIfPresent(1, null));
    }

    @Test
    void compute() {
        assertThrows(UnsupportedOperationException.class, () -> map.compute(1, null));
    }

    @Test()
    void merge() {
        assertThrows(UnsupportedOperationException.class, () -> map.merge(1, "1", null));
    }
}