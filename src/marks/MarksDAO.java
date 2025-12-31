package marks;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.DBConnection;

public class MarksDAO {

    public boolean addMarks(Marks marks) {
        String sql = "INSERT INTO marks (student_id, course_id, marks, grade) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, marks.getStudentId());
            pstmt.setInt(2, marks.getCourseId());
            pstmt.setDouble(3, marks.getMarks());
            pstmt.setString(4, marks.getGrade());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMarks(Marks marks) {
        String sql = "UPDATE marks SET marks=?, grade=? WHERE student_id=? AND course_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, marks.getMarks());
            pstmt.setString(2, marks.getGrade());
            pstmt.setInt(3, marks.getStudentId());
            pstmt.setInt(4, marks.getCourseId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Marks getMarks(int studentId, int courseId) {
        String sql = "SELECT * FROM marks WHERE student_id=? AND course_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Marks(
                    rs.getInt("marks_id"),
                    rs.getInt("student_id"),
                    rs.getInt("course_id"),
                    rs.getDouble("marks"),
                    rs.getString("grade")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Marks> getMarksByStudent(int studentId) {
        List<Marks> list = new ArrayList<>();
        String sql = "SELECT * FROM marks WHERE student_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Marks(
                    rs.getInt("marks_id"),
                    rs.getInt("student_id"),
                    rs.getInt("course_id"),
                    rs.getDouble("marks"),
                    rs.getString("grade")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}