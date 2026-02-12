package com.mycompany.library_system.control;

import com.mycompany.library_system.entity.User;
import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.dao.UserDAO;
import com.mycompany.library_system.repo.UserRepository;
import com.mycompany.library_system.utility.IdGenerator;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public void addUser(String name, String email, String role) {
        MapInterface<String, User> userList = UserRepository.getUserMap();
        String userId = IdGenerator.generateId(userList, "U", 4);

        if (userList.containsKey(userId)) {
            throw new IllegalArgumentException("User ID already exists");
        }

        User user = new User(userId, name, email, role);
        userDAO.save(user);
    }

    public void deleteUser(String userId) {
        if (!UserRepository.getUserMap().containsKey(userId)) {
            throw new IllegalArgumentException("User not found");
        }

        userDAO.delete(userId);
    }

    public void updateUser(User user) {
        String userId = user.getUserId();

        if (!UserRepository.getUserMap().containsKey(userId)) {
            throw new IllegalArgumentException("User not found");
        }

        userDAO.save(user);
    }

    public User getUser(String userId) {
        return userDAO.findById(userId);
    }
}
