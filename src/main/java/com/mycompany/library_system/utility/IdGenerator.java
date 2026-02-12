package com.mycompany.library_system.utility;

import com.mycompany.library_system.adt.MapInterface;

public class IdGenerator {
    private static int getMaxId(MapInterface<String, ?> map, String prefix) {
        int max = 0;
        for (String id : map.keys()) {
            if (id.startsWith(prefix)) {
                try {
                    int num = Integer.parseInt(id.substring(prefix.length())); // remove prefix
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return max; // latest ID number
    }

    public static String generateId(MapInterface<String, ?> map, String prefix, int numberlength) {
        int lastId = getMaxId(map, prefix);
        lastId++;
        return String.format(prefix + "%0" + numberlength + "d", lastId);
    }
}
