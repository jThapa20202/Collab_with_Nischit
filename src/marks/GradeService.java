package marks;

public class GradeService {
    private MarksDAO marksDAO = new MarksDAO();

    public String calculateGrade(double marks) {
        if (marks >= 90) return "A";
        else if (marks >= 80) return "B";
        else if (marks >= 70) return "C";
        else if (marks >= 60) return "D";
        else return "F";
    }

    public boolean assignGrade(Marks marks) {
        String grade = calculateGrade(marks.getMarks());
        marks.setGrade(grade);
        
        // Check if marks already exist, then update, else add
        if (marksDAO.getMarks(marks.getStudentId(), marks.getCourseId()) != null) {
            return marksDAO.updateMarks(marks);
        } else {
            return marksDAO.addMarks(marks);
        }
    }
}
