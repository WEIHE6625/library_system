/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.library_system;

import com.mycompany.library_system.adt.HashMap;
import com.mycompany.library_system.adt.TreeMap;

/**
 *
 * @author wh
 */
public class Library_system {

    public static void main(String[] args) {
        // Run HashMap Unit Tests
        // test_hashmap();
        
        // Run TreeMap Unit Tests
        test_treemap();
    }

    public static void test_hashmap() { 

        System.out.println("========== Starting HashMap Unit Tests ==========\n");

        // 1. Initialization
        System.out.println("--- Test 1: Initialization & isEmpty ---");
        HashMap<String, Integer> map = new HashMap<>();
        test(map.isEmpty(), "Map should be empty initially");
        test(map.size() == 0, "Size should be 0");
        System.out.println();

        // 2. Basic Put & Get
        System.out.println("--- Test 2: Basic Put & Get ---");
        map.put("Alice", 100);
        map.put("Bob", 200);
        map.put("Charlie", 300);

        test(map.size() == 3, "Size should be 3");
        test(map.get("Alice") == 100, "Get Alice should be 100");
        test(map.get("Bob") == 200, "Get Bob should be 200");
        test(map.get("Dave") == null, "Get non-existent key should return null");
        System.out.println();

        // 3. Update (Collision Handling & Overwriting)
        System.out.println("--- Test 3: Updating Values ---");
        Integer oldValue = map.put("Alice", 999);
        test(oldValue == 100, "Put should return old value (100)");
        test(map.get("Alice") == 999, "Get Alice should now be 999");
        test(map.size() == 3, "Size should remain 3 after update");
        System.out.println();

        // 4. Null Key Support
        System.out.println("--- Test 4: Null Key Handling ---");
        map.put(null, 500);
        test(map.get(null) == 500, "Should support null key");
        test(map.containsKey(null), "containsKey(null) should be true");
        System.out.println();

        // 5. ContainsKey & ContainsValue
        System.out.println("--- Test 5: Contains Checks ---");
        test(map.containsKey("Bob"), "Should contain key 'Bob'");
        test(!map.containsKey("Zack"), "Should not contain key 'Zack'");
        test(map.containsValue(300), "Should contain value 300");
        test(!map.containsValue(9999), "Should not contain value 9999");
        System.out.println();

        // 6. Remove
        System.out.println("--- Test 6: Remove ---");
        Integer removedVal = map.remove("Bob");
        test(removedVal == 200, "Remove should return value (200)");
        test(map.get("Bob") == null, "Get 'Bob' should now be null");
        test(!map.containsKey("Bob"), "containsKey('Bob') should be false");
        test(map.size() == 3, "Size should be 3 (Alice, Charlie, null)");
        System.out.println();

        // 7. Resizing (Rehashing)
        System.out.println("--- Test 7: Resize/Rehash Stress Test ---");
        // Current capacity 16. Load factor 0.75. Threshold = 12.
        // We already have 3 items. Let's add 20 more to force a resize.
        System.out.println("Adding 20 items to trigger resize...");
        for (int i = 0; i < 1000000; i++) {
            map.put("User" + i, i);
        }
        test(map.size() == 1000002, "Size should be 1000002 (3 old + 1000000 new)");
        test(map.get("User499999") == 499999, "Should retrieve post-resize data correctly");
        test(map.get("Alice") == 999, "Old data should still exist after resize");
        System.out.println();

        // 8. PutAll
        System.out.println("--- Test 8: PutAll ---");
        HashMap<String, Integer> map2 = new HashMap<>();
        map2.put("X", 1);
        map2.put("Y", 2);
        
        map2.putAll(map); // Copy massive map into map2
        
        test(map2.containsKey("Alice"), "Map2 should now have Alice");
        test(map2.containsKey("User19"), "Map2 should now have User19");
        test(map2.size() == 25, "Map2 size check (2 existing + 23 copied)");
        System.out.println();

        // 9. Clear
        System.out.println("--- Test 9: Clear ---");
        map.clear();
        test(map.isEmpty(), "Map should be empty after clear");
        test(map.size() == 0, "Size should be 0 after clear");
        test(map.get("Alice") == null, "Alice should be gone");
        System.out.println();
        
        System.out.println("========== All Tests Completed ==========");
    }

    // --- Unit Test Main Method ---
    
    public static void test_treemap() {
        System.out.println("========== Starting TreeMap Unit Tests ==========\n");

        // 1. Initialization
        System.out.println("--- Test 1: Initialization & isEmpty ---");
        TreeMap<String, Integer> map = new TreeMap<>();
        test(map.isEmpty(), "Map should be empty initially");
        test(map.size() == 0, "Size should be 0");
        System.out.println();

        // 2. Basic Put & Get
        System.out.println("--- Test 2: Basic Put & Get ---");
        map.put("Alice", 100);
        map.put("Bob", 200);
        map.put("Charlie", 300);

        test(map.size() == 3, "Size should be 3");
        test(map.get("Alice") == 100, "Get Alice should be 100");
        test(map.get("Bob") == 200, "Get Bob should be 200");
        test(map.get("Dave") == null, "Get non-existent key should return null");
        System.out.println();

        // 3. Update (Overwriting values)
        System.out.println("--- Test 3: Updating Values ---");
        Integer oldValue = map.put("Alice", 999);
        test(oldValue == 100, "Put should return old value (100)");
        test(map.get("Alice") == 999, "Get Alice should now be 999");
        test(map.size() == 3, "Size should remain 3 after update");
        System.out.println();

        // 4. Null Key Support (TreeMap Specific Behavior)
        // Your current code ignores null keys (returns null immediately).
        System.out.println("--- Test 4: Null Key Handling ---");
        map.put(null, 500); 
        test(map.get(null) == null, "Get(null) should return null (Ignored)");
        test(!map.containsKey(null), "containsKey(null) should be false (Ignored)");
        test(map.size() == 3, "Size should remain 3 (Null was not added)");
        System.out.println();

        // 5. ContainsKey & ContainsValue
        System.out.println("--- Test 5: Contains Checks ---");
        test(map.containsKey("Bob"), "Should contain key 'Bob'");
        test(!map.containsKey("Zack"), "Should not contain key 'Zack'");
        
        // This requires the containsValue implementation provided above!
        test(map.containsValue(300), "Should contain value 300");
        test(!map.containsValue(9999), "Should not contain value 9999");
        System.out.println();

        // 6. Remove
        System.out.println("--- Test 6: Remove ---");
        Integer removedVal = map.remove("Bob");
        test(removedVal == 200, "Remove should return value (200)");
        test(map.get("Bob") == null, "Get 'Bob' should now be null");
        test(!map.containsKey("Bob"), "containsKey('Bob') should be false");
        test(map.size() == 2, "Size should be 2 (Alice, Charlie)");
        System.out.println();

        // 7. Large Data Stress Test (Balancing Check)
        System.out.println("--- Test 7: Tree Balancing Stress Test ---");
        // We add items in strict order (0, 1, 2...). 
        // In a normal BST, this creates a Linked List (slow).
        // In Red-Black Tree, it should balance itself.
        System.out.println("Adding 1000000 items sequentially...");
        for (int i = 0; i < 1000000; i++) {
            map.put("User" + i, i);
        }
        test(map.size() == 1000002, "Size should be 1000002 (2 old + 1000000 new)");
        test(map.get("User499999") == 499999, "Should retrieve middle item correctly");
        test(map.get("Alice") == 999, "Old data should still exist");
        System.out.println();

        // 8. PutAll
        System.out.println("--- Test 8: PutAll ---");
        TreeMap<String, Integer> map2 = new TreeMap<>();
        map2.put("X", 1);
        map2.put("Y", 2);
        
        // This requires the putAll implementation provided above!
        map2.putAll(map); 
        
        test(map2.containsKey("Alice"), "Map2 should now have Alice");
        test(map2.containsKey("User49"), "Map2 should now have User49");
        test(map2.size() == 54, "Map2 size check (2 existing + 52 copied)");
        System.out.println();

        // 9. Clear
        System.out.println("--- Test 9: Clear ---");
        map.clear();
        test(map.isEmpty(), "Map should be empty after clear");
        test(map.size() == 0, "Size should be 0 after clear");
        test(map.get("Alice") == null, "Alice should be gone");
        System.out.println();
        
        System.out.println("========== All Tests Completed ==========");
    }


    // Method to print Pass/Fail
    private static void test(boolean condition, String message) {
        if (condition) {
            System.out.println(" [PASS] " + message);
        } else {
            System.out.println("![FAIL] " + message);
        }
    }
}
