import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class DatabaseApp {
    // Database connection parameters
    private static final String URL = "jdbc:postgresql://localhost:5432/Assignment_03";
    private static final String USER = "postgres";
    private static final String PASSWORD = "001125";
    private static final Scanner scanner = new Scanner(System.in);
    // Formatter to parse dates in the ISO_LOCAL_DATE format
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    public static void main(String[] args) {
        DatabaseApp app = new DatabaseApp();
        // Main loop for user interaction
        while (true) {
            // Menu options
            System.out.println("1. Add Student");
            System.out.println("2. Delete Student");
            System.out.println("3. List All Students");
            System.out.println("4. Update Student Email");
            System.out.println("5. Exit\n");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Handling user's choice
            switch (choice) {
                case 1:
                    app.addStudent();
                    break;
                case 2:
                    app.deleteStudent();
                    break;
                case 3:
                    app.getAllStudents();
                    break;
                case 4:
                    app.updateStudentEmail();
                    break;
                case 5:
                    System.exit(0); // Exit the program
                default:
                    System.out.println("---Invalid option. Please choose again---");
            }
            System.out.println("\n------------------------------------------------\n");
        }
    }

    // Retrieves and displays all students from the database
    private void getAllStudents() {
        String query = "SELECT * FROM students;";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                // Display each student's details
                System.out.println(rs.getInt("student_id") + ": " +
                        rs.getString("first_name") + " " +
                        rs.getString("last_name") + ", " +
                        rs.getString("email") + ", " +
                        rs.getDate("enrollment_date"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Adds a new student to the database
    private void addStudent() {
        // User input for new student details
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        LocalDate enrollmentDate = null;
        // Validate and parse the enrollment date
        while (enrollmentDate == null) {
            System.out.print("Enter enrollment date (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine();
            enrollmentDate = validateDate(dateInput);
            if (enrollmentDate == null) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }

        // SQL query to insert the new student
        String query = "INSERT INTO students (first_name, last_name, email, enrollment_date) VALUES (?, ?, ?, ?);";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            // Setting query parameters
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setDate(4, Date.valueOf(enrollmentDate));
            // Execute the update
            pstmt.executeUpdate();
            System.out.println("Student added successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Validates the date input by the user
    private LocalDate validateDate(String dateInput) {
        try {
            return LocalDate.parse(dateInput, dateFormatter);
        } catch (DateTimeParseException e) {
            return null; // Return null if the date is invalid
        }
    }

    // Deletes a student from the database based on student ID
    private void deleteStudent() {
        System.out.print("Enter student ID to delete: ");
        int studentId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // SQL query to delete the student
        String query = "DELETE FROM students WHERE student_id = ?;";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            int affectedRows = pstmt.executeUpdate();
            // Check if the student was deleted
            if (affectedRows > 0) {
                System.out.println("Student deleted successfully.");
            } else {
                System.out.println("Student with ID " + studentId + " not found.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Updates the email of a student based on student ID
    private void updateStudentEmail() {
        System.out.print("Enter student ID to update: ");
        int studentId = scanner.nextInt();
        scanner.nextLine(); // Consume newline left after reading integer

        System.out.print("Enter new email: ");
        String newEmail = scanner.nextLine();

        // SQL query to update the student's email
        String query = "UPDATE students SET email = ? WHERE student_id = ?;";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newEmail);
            pstmt.setInt(2, studentId);
            int affectedRows = pstmt.executeUpdate();
            // Check if the email was updated
            if (affectedRows > 0) {
                System.out.println("Email updated successfully.");
            } else {
                System.out.println("Student with ID " + studentId + " not found.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

