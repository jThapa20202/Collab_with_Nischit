package attendance;

import java.time.LocalDate;

public class AttendanceService {
    private AttendanceDAO attendanceDAO = new AttendanceDAO();

    public boolean markPresent(int studentId, int courseId, LocalDate date) {
        Attendance att = new Attendance();
        att.setStudentId(studentId);
        att.setCourseId(courseId);
        att.setDate(date);
        att.setStatus("Present");
        return attendanceDAO.markAttendance(att);
    }

    public boolean markAbsent(int studentId, int courseId, LocalDate date) {
        Attendance att = new Attendance();
        att.setStudentId(studentId);
        att.setCourseId(courseId);
        att.setDate(date);
        att.setStatus("Absent");
        return attendanceDAO.markAttendance(att);
    }
}