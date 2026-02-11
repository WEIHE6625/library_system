package com.mycompany.library_system.utility;

import com.mycompany.library_system.repo.UserRepository;

public class IdGenerator {
    private static int userCounter = 0;
    private static int bookCounter = 0;

    public static void initUserCounter() {
        int max = 0;
        for (String id : UserRepository.getUserMap().keySet()) {
            if (id.startsWith("U")) {
                try {
                    int num = Integer.parseInt(id.substring(1)); // remove 'U'
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {
                }
            }
        }
        userCounter = max; // latest user ID number
    }

    public static String generateUserId() {
        userCounter++;
        return String.format("U%04d", userCounter);
    }

    public static String generateBookId() {
        bookCounter++;
        return String.format("B%04d", bookCounter);
    }
}
