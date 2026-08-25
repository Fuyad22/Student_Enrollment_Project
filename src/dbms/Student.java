package dbms;

public class Student {
    private int id;
    private String name;
    private String email;
    private Gender gender;
    private MyDate enrollmentDate;

    public Student(int id, String name, String email, Gender gender, MyDate enrollmentDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.enrollmentDate = enrollmentDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public MyDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(MyDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }
}
