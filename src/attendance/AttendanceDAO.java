package attendance;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import util.DBConnection;

public class AttendanceDAO {

    public boolean markAttendance(Attendance attendance) {
        if (attendanceExists(attendance.getStudentId(), attendance.getCourseId(), attendance.getDate())) {
            return false; 
        }
        String sql = "INSERT INTO attendance (student_id, course_id, date, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attendance.getStudentId());
            pstmt.setInt(2, attendance.getCourseId());
            pstmt.setDate(3, Date.valueOf(attendance.getDate()));
            pstmt.setString(4, attendance.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean attendanceExists(int studentId, int courseId, LocalDate date) {
        String sql = "SELECT 1 FROM attendance WHERE student_id=? AND course_id=? AND date=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setDate(3, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Attendance> getAttendanceByStudent(int studentId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE student_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Attendance(
                    rs.getInt("attendance_id"),
                    rs.getInt("student_id"),
                    rs.getInt("course_id"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Attendance> getAttendanceByCourse(int courseId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE course_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Attendance(
                    rs.getInt("attendance_id"),
                    rs.getInt("student_id"),
                    rs.getInt("course_id"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public double calculateAttendancePercentage(int studentId, int courseId) {
        String sqlTotal = "SELECT COUNT(*) FROM attendance WHERE student_id=? AND course_id=?";
        String sqlPresent = "SELECT COUNT(*) FROM attendance WHERE student_id=? AND course_id=? AND status='Present'";
        
        try (Connection conn = DBConnection.getConnection()) {
            int total = 0, present = 0;
            
            try (PreparedStatement pstmt = conn.prepareStatement(sqlTotal)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, courseId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) total = rs.getInt(1);
            }

            if (total == 0) return 0.0;

            try (PreparedStatement pstmt = conn.prepareStatement(sqlPresent)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, courseId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) present = rs.getInt(1);
            }

            return ((double) present / total) * 100;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}