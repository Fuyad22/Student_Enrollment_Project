package dbms;

public class Timetable {
    private int timetableId;
    private int studentId;
    private String unitCode;
    private DayOfWeek classDay;
    private String classTime;

    public Timetable(int timetableId, int studentId, String unitCode, DayOfWeek classDay, String classTime) {
        this.timetableId = timetableId;
        this.studentId = studentId;
        this.unitCode = unitCode;
        this.classDay = classDay;
        this.classTime = classTime;
    }

    public int getTimetableId() {
        return timetableId;
    }

    public void setTimetableId(int timetableId) {
        this.timetableId = timetableId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public DayOfWeek getClassDay() {
        return classDay;
    }

    public void setClassDay(DayOfWeek classDay) {
        this.classDay = classDay;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }
}
