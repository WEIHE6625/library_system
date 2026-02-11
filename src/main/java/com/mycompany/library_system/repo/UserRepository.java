package com.mycompany.library_system.repo;

import com.mycompany.library_system.entity.User;
import com.mycompany.library_system.adt.TreeMap;

public class UserRepository {
    private static final TreeMap<String, User> userMap = new TreeMap<>();

    public static TreeMap<String, User> getUserMap() {
        return userMap;
    }

    public User get(String id) {
        return userMap.get(id);
    }

    public void put(User user) {
        userMap.put(user.getUserId(), user);
    }

    public void remove(String id) {
        userMap.remove(id);
    }

    public boolean contains(String id) {
        return userMap.containsKey(id);
    }
}
