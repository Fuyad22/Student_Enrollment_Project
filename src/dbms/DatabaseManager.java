package dbms;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:timetable.db";

    static {
        try {
            // Load SQLite JDBC driver class
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load SQLite JDBC driver.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        // Enable foreign key support in SQLite
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public static void initializeDatabase() {
        String createStudents = "CREATE TABLE IF NOT EXISTS students ("
                + "  id INTEGER PRIMARY KEY,"
                + "  name TEXT NOT NULL,"
                + "  email TEXT NOT NULL,"
                + "  gender TEXT NOT NULL,"
                + "  enrollment_date TEXT NOT NULL"
                + ");";

        String createUnits = "CREATE TABLE IF NOT EXISTS units ("
                + "  unit_code TEXT PRIMARY KEY,"
                + "  title TEXT NOT NULL,"
                + "  credits INTEGER NOT NULL,"
                + "  department TEXT NOT NULL"
                + ");";

        String createTimetable = "CREATE TABLE IF NOT EXISTS timetable ("
                + "  timetable_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "  student_id INTEGER NOT NULL,"
                + "  unit_code TEXT NOT NULL,"
                + "  class_day TEXT NOT NULL,"
                + "  class_time TEXT NOT NULL,"
                + "  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,"
                + "  FOREIGN KEY (unit_code) REFERENCES units(unit_code) ON DELETE CASCADE"
                + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createStudents);
            stmt.execute(createUnits);
            stmt.execute(createTimetable);
            System.out.println("Database tables initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database.");
            e.printStackTrace();
        }
    }

    // --- Student CRUD ---

    public static List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT id, name, email, gender, enrollment_date FROM students ORDER BY id;";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                Gender gender = Gender.valueOf(rs.getString("gender"));
                MyDate date = MyDate.parse(rs.getString("enrollment_date"));
                list.add(new Student(id, name, email, gender, date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void insertStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (id, name, email, gender, enrollment_date) VALUES (?, ?, ?, ?, ?);";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, student.getId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getGender().name());
            pstmt.setString(5, student.getEnrollmentDate().toDbString());
            pstmt.executeUpdate();
        }
    }

    public static void updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET name = ?, email = ?, gender = ?, enrollment_date = ? WHERE id = ?;";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getEmail());
            pstmt.setString(3, student.getGender().name());
            pstmt.setString(4, student.getEnrollmentDate().toDbString());
            pstmt.setInt(5, student.getId());
            pstmt.executeUpdate();
        }
    }

    public static void deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?;";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // --- Unit CRUD ---

    public static List<Unit> getAllUnits() {
        List<Unit> list = new ArrayList<>();
        String sql = "SELECT unit_code, title, credits, department FROM units ORDER BY unit_code;";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String code = rs.getString("unit_code");
                String title = rs.getString("title");
                int credits = rs.getInt("credits");
                String dept = rs.getString("department");
                list.add(new Unit(code, title, credits, dept));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void insertUnit(Unit unit) throws SQLException {
        String sql = "INSERT INTO units (unit_code, title, credits, department) VALUES (?, ?, ?, ?);";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, unit.getUnitCode());
            pstmt.setString(2, unit.getTitle());
            pstmt.setInt(3, unit.getCredits());
            pstmt.setString(4, unit.getDepartment());
            pstmt.executeUpdate();
        }
    }

    public static void updateUnit(Unit unit) throws SQLException {
        String sql = "UPDATE units SET title = ?, credits = ?, department = ? WHERE unit_code = ?;";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, unit.getTitle());
            pstmt.setInt(2, unit.getCredits());
            pstmt.setString(3, unit.getDepartment());
            pstmt.setString(4, unit.getUnitCode());
            pstmt.executeUpdate();
        }
    }

    public static void deleteUnit(String unitCode) throws SQLException {
        String sql = "DELETE FROM units WHERE unit_code = ?;";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, unitCode);
            pstmt.executeUpdate();
        }
    }

    // --- Timetable CRUD ---

    public static List<Timetable> getAllTimetables() {
        List<Timetable> list = new ArrayList<>();
        String sql = "SELECT timetable_id, student_id, unit_code, class_day, class_time FROM timetable;";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("timetable_id");
                int studentId = rs.getInt("student_id");
                String unitCode = rs.getString("unit_code");
                DayOfWeek day = DayOfWeek.valueOf(rs.getString("class_day"));
                String time = rs.getString("class_time");
                list.add(new Timetable(id, studentId, unitCode, day, time));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Object[]> getTimetableDisplayData() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT t.timetable_id, t.student_id, s.name AS student_name, "
                + "t.unit_code, u.title AS unit_title, t.class_day, t.class_time "
                + "FROM timetable t "
                + "JOIN students s ON t.student_id = s.id "
                + "JOIN units u ON t.unit_code = u.unit_code "
                + "ORDER BY t.timetable_id;";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int ttId = rs.getInt("timetable_id");
                int sId = rs.getInt("student_id");
                String sName = rs.getString("student_name");
                String uCode = rs.getString("unit_code");
                String uTitle = rs.getString("unit_title");
                String day = rs.getString("class_day");
                String time = rs.getString("class_time");
                // Capitalize day name for display
                DayOfWeek dayEnum = DayOfWeek.valueOf(day);
                list.add(new Object[]{ttId, sId, sName, uCode, uTitle, dayEnum, time});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void insertTimetable(Timetable timetable) throws SQLException {
        String sql = "INSERT INTO timetable (student_id, unit_code, class_day, class_time) VALUES (?, ?, ?, ?);";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, timetable.getStudentId());
            pstmt.setString(2, timetable.getUnitCode());
            pstmt.setString(3, timetable.getClassDay().name());
            pstmt.setString(4, timetable.getClassTime());
            pstmt.executeUpdate();
        }
    }

    public static void updateTimetable(Timetable timetable) throws SQLException {
        String sql = "UPDATE timetable SET student_id = ?, unit_code = ?, class_day = ?, class_time = ? WHERE timetable_id = ?;";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, timetable.getStudentId());
            pstmt.setString(2, timetable.getUnitCode());
            pstmt.setString(3, timetable.getClassDay().name());
            pstmt.setString(4, timetable.getClassTime());
            pstmt.setInt(5, timetable.getTimetableId());
            pstmt.executeUpdate();
        }
    }

    public static void deleteTimetable(int timetableId) throws SQLException {
        String sql = "DELETE FROM timetable WHERE timetable_id = ?;";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, timetableId);
            pstmt.executeUpdate();
        }
    }

    public static boolean studentExists(int id) {
        String sql = "SELECT COUNT(*) FROM students WHERE id = ?;";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean unitExists(String unitCode) {
        String sql = "SELECT COUNT(*) FROM units WHERE unit_code = ?;";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, unitCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkScheduleConflict(int studentId, String classDay, String classTime, int excludeId) {
        String sql = "SELECT COUNT(*) FROM timetable WHERE student_id = ? AND class_day = ? AND class_time = ? AND timetable_id != ?;";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setString(2, classDay);
            pstmt.setString(3, classTime);
            pstmt.setInt(4, excludeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

