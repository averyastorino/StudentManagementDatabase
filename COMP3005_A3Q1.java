import java.sql.*;
import java.util.Scanner;

// Main class for the Student Management System application
// Handles database connection and CRUD operations using JDBC
public class COMP3005_A3Q1 {

    public static void main(String[] args) {
        // Database connection configuration
        // url: JDBC URL for PostgreSQL
        // user/password: credentials for connecting to the database
        String url = "jdbc:postgresql://localhost:5432/StudentManagementDB";
        String user = "postgres";
        String password = "myPassword";

        try {
            // Load the PostgreSQL JDBC driver class
            Class.forName("org.postgresql.Driver");
            //connection to the database
            Connection conn = DriverManager.getConnection(url, user, password);
            // Check if connection is successful
            // If connected, start the interactive menu
            // Otherwise, print an error message
            if (conn != null) {
                System.out.println("Successfully conneted to the database");
                mainMenu(conn); //start the main menu for user interaction
            } else {
                System.out.println("Failed to connect to the database");
            }
            //close the connection
            conn.close();
        // Handle exceptions related to JDBC driver loading or SQL operations
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    //get all students from the database
    public static void getAllStudents(Connection conn) {
        // SQL query to select all columns from the students table
        String query = "SELECT * FROM students"; 
        // Execute the SQL query and store the results in a ResultSet
        try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("--- Student List ---");
            //rs object to iterate through the result set
            while (rs.next()) {
                //Retrieve values and display to the user
                System.out.println("Student ID: " + rs.getInt("student_id"));
                System.out.println("First Name: " + rs.getString("first_name"));
                System.out.println("Last Name: " + rs.getString("last_name"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Enrollment Date: " + rs.getString("enrollment_date"));
                System.out.println("--------------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Insert a new student record into the database
    // Takes first name, last name, email, and enrollment date as parameters
    public static void addStudent(Connection conn, String firstName, String lastName, String email, String enrollmentDate) {
        String query = "INSERT INTO students (first_name, last_name, email, enrollment_date) VALUES (?, ?, ?, ?)";
        //prepared statement to add a new student
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);

            // Parse user input string to java.sql.Date
            Date sqlDate = Date.valueOf(enrollmentDate); // format must be "YYYY-MM-DD"
            // Then set it in the PreparedStatement
            pstmt.setDate(4, sqlDate);

            pstmt.executeUpdate();
            System.out.println("Student added successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //update the email of a student based on the student_id
    public static void updateStudentEmail(Connection conn, int studentId, String newEmail) {
        String query = "UPDATE students SET email = ? WHERE student_id = ?";
        //prepared statement to update the email
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newEmail);
            pstmt.setInt(2, studentId);

            // Execute the update and check if any row was affected
            // Print a success or failure message
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Student email updated successfully");
            } else {
                System.out.println("Failed to update student email");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //delete a student from the database using the student_id
    public static void deleteStudent(Connection conn, int studentId) {
        String query = "DELETE FROM students WHERE student_id = ?";
        //prepared statement to delete a student
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            
            // Execute the delete operation and verify if it affected any row
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Student deleted successfully");
            } else {
                System.out.println("Failed to delete student");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Display an interactive menu for the user to perform CRUD operations
    public static void mainMenu(Connection conn) {
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        do {
            System.out.println("---- Student Management System ----");
            System.out.println("1. View All Students");
            System.out.println("2. Add a New Student");
            System.out.println("3. Update Student Email");
            System.out.println("4. Delete a Student");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: // View all students
                    getAllStudents(conn);
                    break;
                case 2: // Prompt user for student info and add a new student
                    System.out.println("First Name: ");
                    String firstName = scanner.nextLine();
                    System.out.println("Last Name: ");
                    String lastName = scanner.nextLine();
                    System.out.println("Email: ");
                    String email = scanner.nextLine();
                    System.out.println("Enrollment Date (YYYY-MM-DD): ");
                    String enrollmentDate = scanner.nextLine();
                    addStudent(conn, firstName, lastName, email, enrollmentDate);
                    break;
                case 3: // Prompt user for student_id and new email, then update record
                    System.out.println("Student ID to update: ");
                    int studentId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("New Email: ");
                    String newEmail = scanner.nextLine();
                    updateStudentEmail(conn, studentId, newEmail);
                    break;
                case 4: // Prompt user for student_id and delete the corresponding student
                    System.out.println("Student ID to delete: ");
                    studentId = scanner.nextInt();
                    scanner.nextLine();
                    deleteStudent(conn, studentId);
                    break;
                case 5: // Exit the program
                    System.out.println("Exiting the program...Goodbye!");
                    break;
                default: // Handle invalid menu choices
                    System.out.println("Invalid choice. Please try again.\n");
            }

        } while (choice != 5);

        scanner.close();
    }

    
}