package reports;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.DBConnection;
import student.Student;
import course.Course;

public class ReportDAO {

    public StudentReport generateStudentReport(int studentId) {
        List<String> courses = new ArrayList<>();
        List<Double> marks = new ArrayList<>();
        
        // Fetch Courses and Marks for the student
        String sql = "SELECT c.course_name, m.marks FROM enrollments e " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "LEFT JOIN marks m ON e.student_id = m.student_id AND e.course_id = m.course_id " +
                     "WHERE e.student_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                courses.add(rs.getString("course_name"));
                // Handle null marks if not assigned yet
                marks.add(rs.getObject("marks") != null ? rs.getDouble("marks") : 0.0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Calculate Average Attendance (Simplified aggregation across all courses)
        double totalPercentage = 0;
        int count = 0;
        // In a real scenario, this would rely on AttendanceDAO logic or a complex query
        // Here we stub the calculation query
        String avgAttSql = "SELECT AVG(CASE WHEN status='Present' THEN 1 ELSE 0 END) * 100 " +
                           "FROM attendance WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(avgAttSql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                totalPercentage = rs.getDouble(1);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return new StudentReport(studentId, courses, marks, totalPercentage);
    }

    public Map<Course, Double> generateCourseAttendanceReport(int courseId) {
        Map<Course, Double> report = new HashMap<>();
        // This method requires calculating attendance % for every student in a specific course
        String sql = "SELECT s.student_id, s.name, " +
                     "(SELECT count(*) FROM attendance a WHERE a.student_id = s.student_id AND a.course_id = ? AND a.status = 'Present') * 100.0 / " +
                     "(SELECT count(*) FROM attendance a WHERE a.student_id = s.student_id AND a.course_id = ?) as pct " +
                     "FROM students s " +
                     "JOIN enrollments e ON s.student_id = e.student_id " +
                     "WHERE e.course_id = ?";
        
        // Note: Map key is Course, but logic suggests mapping Student -> % for a specific course.
        // However, sticking to the PDF signature: Map<Course, Double>. 
        // This signature implies One Course -> One Double. This might be the *Average Attendance of the Course*.
        
        String avgCourseSql = "SELECT c.course_id, c.course_name, c.credits, " +
                              "(SELECT count(*) FROM attendance a WHERE a.course_id = c.course_id AND a.status = 'Present') * 100.0 / " +
                              "(NULLIF((SELECT count(*) FROM attendance a WHERE a.course_id = c.course_id), 0)) as pct " +
                              "FROM courses c WHERE c.course_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(avgCourseSql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Course c = new Course(rs.getInt("course_id"), rs.getString("course_name"), rs.getInt("credits"));
                Double pct = rs.getDouble("pct");
                report.put(c, pct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }

    public List<Student> getDefaulterList(double minAttendance) {
        List<Student> defaulters = new ArrayList<>();
        // Query to find students whose average attendance across enrolled courses is less than minAttendance
        String sql = "SELECT s.student_id, s.name, s.roll_no, s.email, " +
                     "(SELECT count(*) FROM attendance a WHERE a.student_id = s.student_id AND a.status = 'Present') * 100.0 / " +
                     "NULLIF((SELECT count(*) FROM attendance a WHERE a.student_id = s.student_id), 0) as total_pct " +
                     "FROM students s " +
                     "HAVING total_pct < ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, minAttendance);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                defaulters.add(new Student(
                    rs.getInt("student_id"),
                    rs.getString("name"),
                    rs.getString("roll_no"),
                    rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return defaulters;
    }
}