package com.mycompany.library_system.utility;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import com.mycompany.library_system.adt.MapInterface;

/**
 *
 * @author all
 */
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

    // Return a keyId for different TreeMap
    public static <K> String getKeyId(K key, String Id) {
        if (key == null) return null;
        String keyId = "";
        if(key instanceof Double){
            keyId = String.format("%06.2f-%s", key, Id);
        }else if (key instanceof Integer) {
            keyId = String.format("%03d-%s", key, Id);
        }else if (key instanceof String) {
            keyId = (String) key;
            keyId = String.format("%s-%s", keyId.toLowerCase().replaceAll("\\s+", ""), Id);
        }else if (key instanceof Enum<?>) { // ADD THIS BRANCH
            // Get the Enum name (e.g., "BACHELOR"), lowercase it and remove spaces
            String enumStr = ((Enum<?>) key).name().toLowerCase().replaceAll("_", "");
            keyId = String.format("%s-%s", enumStr, Id);
        }else if (key instanceof YearMonth) {
            YearMonth yearMonth = (YearMonth) key;
            keyId = String.format("%s-%s", yearMonth.format(DateTimeFormatter.ofPattern("MM-yyyy")).toString(), Id);
        }
        return keyId;
    }
}
