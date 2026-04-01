package com.mycompany.library_system.dao;

import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.entity.User;
import com.mycompany.library_system.utility.DataProcessor;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import me.tongfei.progressbar.ProgressBar;

import java.io.*;

public class UserDAO {
    public static TreeMap<String, User> readFile(String filePath) {

        TreeMap<String, User> userList = new TreeMap<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {

            // Skip header
            reader.readNext();

            int totalLines = DataProcessor.lineCounter(filePath);
            String[] data;

            try (ProgressBar pb = new ProgressBar("Loading Users : ", totalLines)) {

                while ((data = reader.readNext()) != null) {
                    pb.step();

                    if (data.length >= 8) {
                        try {
                            String userId = data[0].trim();
                            String name = data[1].trim();
                            String email = data[2].trim();
                            User.Gender gender = User.Gender.valueOf(data[3].trim().toUpperCase());
                            int age = 0;
                            try { age = Integer.parseInt(data[4]); } catch (NumberFormatException e) {}
                            User.Education education = User.Education.valueOf(data[5].trim().toUpperCase());
                            User.UserRole role = User.UserRole.valueOf(data[6].trim().toUpperCase());

                            String createdAt = data[7].trim();
                            int numOfBooksBorrowing = 0;

                            try {
                                numOfBooksBorrowing = Integer.parseInt(data[8]);
                            } catch (NumberFormatException e) {}

                            User user = new User(
                                    userId,
                                    name,
                                    email,
                                    gender,
                                    age,
                                    education,
                                    role,
                                    createdAt,
                                    numOfBooksBorrowing
                            );
                            userList.put(userId, user);

                        } catch (Exception e) {
                            System.out.println("Skipping invalid row: " + e.getMessage());
                        }
                    }
                }
            }

        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return userList;
    }


    public static void writeFile(String filePath, TreeMap<String, User> userList) {

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {

            // Write header
            String[] header = {
                    "UserID",
                    "Name",
                    "Email",
                    "Gender",
                    "Age",
                    "Education",
                    "Role",
                    "CreatedAt",
                    "NumOfBooksBorrowing",
            };

            writer.writeNext(header, false);

            // Write data
            for (User user : userList.values()) {

                String[] data = {
                        user.getUserId(),
                        user.getName(),
                        user.getEmail(),
                        user.getGender().name(),
                        String.valueOf(user.getAge()),
                        user.getEducation().name(),
                        user.getRole().name(),
                        user.getCreatedAt(),
                        String.valueOf(user.getNumOfBooksBorrowing())
                };

                writer.writeNext(data, false);
            }

            System.out.println("Data successfully saved to " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
