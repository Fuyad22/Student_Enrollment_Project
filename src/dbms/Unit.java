package dbms;

public class Unit {
    private String unitCode;
    private String title;
    private int credits;
    private String department;

    public Unit(String unitCode, String title, int credits, String department) {
        this.unitCode = unitCode;
        this.title = title;
        this.credits = credits;
        this.department = department;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return unitCode + " - " + title;
    }
}
