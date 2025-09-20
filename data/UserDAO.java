package data;

import models.User;
import java.io.IOException;
import java.util.*;

/**
 * Data Access Object for User model
 * Handles CSV file operations for users
 */
public class UserDAO {
    private static final String CSV_FILE = "users.csv";
    private static final String[] HEADERS = { "userId", "username", "email", "password" };

    /**
     * Load all users from CSV file
     */
    public List<User> loadUsers() throws IOException {
        List<User> users = new ArrayList<>();
        List<String[]> records = CSVUtil.readCSV(CSV_FILE);

        // Skip header row if exists
        for (int i = (records.size() > 0 && isHeader(records.get(0)) ? 1 : 0); i < records.size(); i++) {
            String[] record = records.get(i);
            if (record.length >= 4) {
                User user = new User();
                user.setUserId(record[0]);
                user.setUsername(record[1]);
                user.setEmail(record[2]);
                user.setPassword(record[3]);
                users.add(user);
            }
        }

        return users;
    }

    /**
     * Save all users to CSV file
     */
    public void saveUsers(List<User> users) throws IOException {
        List<String[]> records = new ArrayList<>();

        // Add header
        records.add(HEADERS);

        // Add user data
        for (User user : users) {
            String[] record = {
                    user.getUserId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPassword()
            };
            records.add(record);
        }

        CSVUtil.writeCSV(CSV_FILE, records);
    }

    /**
     * Find user by username
     */
    public User findByUsername(String username) throws IOException {
        List<User> users = loadUsers();
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * Find user by ID
     */
    public User findById(String userId) throws IOException {
        List<User> users = loadUsers();
        return users.stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Add new user
     */
    public void addUser(User user) throws IOException {
        List<User> users = loadUsers();
        users.add(user);
        saveUsers(users);
    }

    /**
     * Authenticate user
     */
    public User authenticate(String username, String password) throws IOException {
        User user = findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    private boolean isHeader(String[] record) {
        return record.length >= 4 && record[0].equals("userId");
    }
}