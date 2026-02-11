package com.mycompany.library_system.dao;

import com.mycompany.library_system.entity.User;
import com.mycompany.library_system.repo.UserRepository;

import java.io.*;

public class UserDAO {

    private static final String FILE_PATH = "database/users.csv";

    public UserDAO() {
        loadAllUser();
    }

    public boolean exists(String userId) {
        return UserRepository.getUserMap().containsKey(userId);
    }

    public void save(User user) {
        UserRepository.getUserMap().put(user.getUserId(), user);
        writeToFile();
    }

    public void delete(String userId) {
        UserRepository.getUserMap().remove(userId);
        writeToFile();
    }

    public User findById(String userId) {
        return UserRepository.getUserMap().get(userId);
    }

    private void loadAllUser() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) { // skip header
                    isHeader = false;
                    continue;
                }
                User user = User.fromCSV(line);
                UserRepository.getUserMap().put(user.getUserId(), user);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load users from CSV", e);
        }
    }

    private void writeToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {

            // CSV header
            bw.write("userId,name,email,role");
            bw.newLine();

            for (User user : UserRepository.getUserMap().values()) {
                bw.write(user.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write users to CSV", e);
        }
    }
}
