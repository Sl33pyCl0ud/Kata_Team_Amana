package info.dmerej.contacts;

import java.io.File;
import java.sql.*;
import java.util.stream.Stream;

public class Database {
    private Connection connection;
    private int insertedCount = 0;

    public Database(File databaseFile) {
        String databasePath = databaseFile.getPath();
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
        } catch (SQLException e) {
            throw new RuntimeException("Could not create connection: " + e.toString());
        }
    }

    public void migrate() {
        System.out.println("Migrating database ...");
        try {
            Statement statement = connection.createStatement();
            statement.execute("""
                    CREATE TABLE contacts(
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    email TEXT NOT NULL
                    )
                    """
            );
        } catch (SQLException e) {
            throw new RuntimeException("Could not migrate db: " + e.toString());
        }
        System.out.println("Done migrating database");
    }

    public void insertContacts(Stream<Contact> contacts) {
        String insertQuery = "INSERT INTO contacts (name, email) VALUES (?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
    
            contacts.forEach(contact -> {
                try {
                    preparedStatement.setString(1, contact.getName());
                    preparedStatement.setString(2, contact.getEmail());
                    preparedStatement.executeUpdate();
                    insertedCount++;
                } catch (SQLException e) {
                    throw new RuntimeException("Error when inserting contact into db: " + e.toString());
                }
            });
    
            preparedStatement.close();
    
        } catch (SQLException e) {
            throw new RuntimeException("Error when preparing insert statement: " + e.toString());
        }
        System.out.println("Inserted " + insertedCount + " contacts into the database.");
    }
    

    public String getContactNameFromEmail(String email) {
        String query = "SELECT name FROM contacts WHERE email = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString(1);
            } else {
                throw new RuntimeException("No match in the db for email: " + email);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error when looking up contacts from db: " + e.toString());
        }
    }

    public void close() {
        if (connection == null) {
            return;
        }

        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Could not close db: " + e.toString());
        }
    }

}
