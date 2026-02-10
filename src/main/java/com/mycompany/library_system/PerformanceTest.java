package com.mycompany.library_system;

import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;

import com.mycompany.library_system.adt.HashMap;
import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.utility.SampleEntity;

/**
 * Benchmarking HashMap vs. TreeMap vs. ArrayList
 * Data Size: 1,000,000 records
 */
public class PerformanceTest {

    // Configuration
    private static final int DATA_SIZE = 1_000_000;
    private static final String TARGET_KEY = "ID" + String.format("%06d", DATA_SIZE / 2);

    public static void main(String[] args) {
        System.out.println("Generating " + DATA_SIZE + " random data records...");
        
        // 1. Prepare Data Arrays
        SampleEntity[] values = new SampleEntity[DATA_SIZE];
        Random rand = new Random();
        
        for (int i = 0; i < DATA_SIZE; i++) {
            // Generate padded ID: "ID000000", "ID000001", ... "ID999999"
            String id = String.format("ID%06d", i);
            values[i] = new SampleEntity(id, "Name" + i, rand.nextInt(100));
        }

        System.out.println("Data generation complete. Starting Benchmarks...\n");
        System.out.printf("%-15s %-15s %-15s %-15s\n", "Structure", "Add (ms)", "Search (ns)", "Delete (ms)");
        System.out.println("-------------------------------------------------------------");

        testHashMap(values);
        testTreeMap(values);
        testArrayList(values);
        
        System.out.println("-------------------------------------------------------------");
        System.out.println("\nAnalysis:");
        System.out.println("1. HashMap: Fastest search O(1). Value type (Entity) adds minimal overhead.");
        System.out.println("2. TreeMap: Insertion O(log n). Value type (Entity) adds minimal overhead.");
        System.out.println("3. ArrayList: Extremely slow delete O(n). Testing Keys only to show structural difference.");

        // --- Functional Sorting Tests ---
        
        System.out.println("\n======================================");
        System.out.println("      Functional Sorting Tests        ");
        System.out.println("======================================");

        HashMap<String, SampleEntity> hashMap = new HashMap<>();
        TreeMap<String, SampleEntity> treeMap = new TreeMap<>();
        
        // Load data for sorting tests
        System.out.println("Reloading maps for sorting tests...");
        for(int i=0; i<DATA_SIZE; i++) {
             hashMap.put(values[i].getId(), values[i]);
             treeMap.put(values[i].getId(), values[i]);
        }
        System.out.println("Reload complete.\n");

        // Test HashMap Sorting
        testSortKeyAscending(hashMap, "HashMap");
        testSortKeyDescending(hashMap, "HashMap");
        testSortTopScores(hashMap, "HashMap");

        // Test TreeMap Sorting
        testSortKeyAscending(treeMap, "TreeMap");
        testSortKeyDescending(treeMap, "TreeMap");
        testSortTopScores(treeMap, "TreeMap");
    }

    // ... (Existing testHashMap, testTreeMap, testArrayList methods remain unchanged) ...
    private static void testHashMap(SampleEntity[] values) {
        HashMap<String, SampleEntity> map = new HashMap<>();
        long start = System.nanoTime();
        for (int i = 0; i < DATA_SIZE; i++) { map.put(values[i].getId(), values[i]); }
        long durationAdd = (System.nanoTime() - start) / 1_000_000;

        start = System.nanoTime();
        map.get(TARGET_KEY);
        long durationSearch = (System.nanoTime() - start);

        start = System.nanoTime();
        map.remove(TARGET_KEY);
        long durationDelete = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("%-15s %-15d %-15d %-15d\n", "HashMap", durationAdd, durationSearch, durationDelete);
    }

    private static void testTreeMap(SampleEntity[] values) {
        TreeMap<String, SampleEntity> map = new TreeMap<>();
        long start = System.nanoTime();
        for (int i = 0; i < DATA_SIZE; i++) { map.put(values[i].getId(), values[i]); }
        long durationAdd = (System.nanoTime() - start) / 1_000_000;

        start = System.nanoTime();
        map.get(TARGET_KEY);
        long durationSearch = (System.nanoTime() - start);

        start = System.nanoTime();
        map.remove(TARGET_KEY);
        long durationDelete = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("%-15s %-15d %-15d %-15d\n", "TreeMap", durationAdd, durationSearch, durationDelete);
    }

    private static void testArrayList(SampleEntity[] values) {
        ArrayList<String> list = new ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < DATA_SIZE; i++) { list.add(values[i].getId()); }
        long durationAdd = (System.nanoTime() - start) / 1_000_000;

        start = System.nanoTime();
        boolean found = list.contains(TARGET_KEY); 
        long durationSearch = (System.nanoTime() - start);

        start = System.nanoTime();
        list.remove(TARGET_KEY);
        long durationDelete = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("%-15s %-15d %-15d %-15d\n", "ArrayList", durationAdd, durationSearch, durationDelete);
    }


    // --- Modified Sorting Methods with Timer ---

    private static void testSortKeyAscending(MapInterface<String, SampleEntity> map, String mapName) {
        // Start Timer
        long start = System.nanoTime();

        // 1. Get all entries
        MapInterface.Entry<String, SampleEntity>[] entries = map.entrySet();
        
        // 2. Sort by Key Ascending
        Arrays.sort(entries, (e1, e2) -> e1.getKey().compareTo(e2.getKey()));
        
        // Stop Timer
        long duration = (System.nanoTime() - start) / 1_000_000; // ms

        System.out.println("\n--- " + mapName + ": Top 10 Ascending by ID (Time: " + duration + " ms) ---");
        
        // 3. Display first 10
        int limit = Math.min(10, entries.length);
        for (int i = 0; i < limit; i++) {
            System.out.println("ID: " + entries[i].getKey() + " | " + entries[i].getValue().toString());
        }
    }

    private static void testSortKeyDescending(MapInterface<String, SampleEntity> map, String mapName) {
        // Start Timer
        long start = System.nanoTime();

        MapInterface.Entry<String, SampleEntity>[] entries = map.entrySet();
        
        // Sort by Key Descending
        Arrays.sort(entries, (e1, e2) -> e2.getKey().compareTo(e1.getKey()));
        
        // Stop Timer
        long duration = (System.nanoTime() - start) / 1_000_000; // ms

        System.out.println("\n--- " + mapName + ": Top 10 Descending by ID (Time: " + duration + " ms) ---");
        
        int limit = Math.min(10, entries.length);
        for (int i = 0; i < limit; i++) {
            System.out.println("ID: " + entries[i].getKey() + " | " + entries[i].getValue().toString());
        }
    }

    private static void testSortTopScores(MapInterface<String, SampleEntity> map, String mapName) {
        // Start Timer
        long start = System.nanoTime();

        MapInterface.Entry<String, SampleEntity>[] entries = map.entrySet();
        
        // Sort by Value (SampleEntity.score) Descending
        Arrays.sort(entries, (e1, e2) -> {
            int score1 = e1.getValue().getScore();
            int score2 = e2.getValue().getScore();
            return Integer.compare(score2, score1); 
        });
        
        // Stop Timer
        long duration = (System.nanoTime() - start) / 1_000_000; // ms

        System.out.println("\n--- " + mapName + ": Top 10 Highest Scores (Time: " + duration + " ms) ---");
        
        int limit = Math.min(10, entries.length);
        for (int i = 0; i < limit; i++) {
            System.out.println("Score: " + entries[i].getValue().getScore() + 
                               " | ID: " + entries[i].getKey() + 
                               " | " + entries[i].getValue().toString());
        }
    }
}