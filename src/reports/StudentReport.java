package reports;

import java.util.List;

public class StudentReport {
    private int studentId;
    private List<String> courses; // Using String for course names based on PDF attribute list simplicity
    private List<Double> marks;   // Corresponds to the course list
    private double attendancePercentage;

    public StudentReport(int studentId, List<String> courses, List<Double> marks, double attendancePercentage) {
        this.studentId = studentId;
        this.courses = courses;
        this.marks = marks;
        this.attendancePercentage = attendancePercentage;
    }

    // Getters and Setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public List<String> getCourses() { return courses; }
    public void setCourses(List<String> courses) { this.courses = courses; }
    public List<Double> getMarks() { return marks; }
    public void setMarks(List<Double> marks) { this.marks = marks; }
    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
}