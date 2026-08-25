package dbms;

import java.io.Serializable;

public class MyDate implements Serializable {
    private int day;
    private int month;
    private int year;

    public MyDate() {
        // Default to a standard date
        this.day = 1;
        this.month = 1;
        this.year = 2026;
    }

    public MyDate(int day, int month, int year) {
        if (!isValidDate(day, month, year)) {
            throw new IllegalArgumentException("Invalid date: " + year + "-" + month + "-" + day);
        }
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        if (!isValidDate(day, this.month, this.year)) {
            throw new IllegalArgumentException("Invalid day for current month/year");
        }
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        if (!isValidDate(this.day, month, this.year)) {
            throw new IllegalArgumentException("Invalid month for current day/year");
        }
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        if (!isValidDate(this.day, this.month, year)) {
            throw new IllegalArgumentException("Invalid year for current day/month");
        }
        this.year = year;
    }

    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    public static int getDaysInMonth(int month, int year) {
        switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                return 31;
            case 4: case 6: case 9: case 11:
                return 30;
            case 2:
                return isLeapYear(year) ? 29 : 28;
            default:
                return 0;
        }
    }

    public static boolean isValidDate(int day, int month, int year) {
        if (year < 1 || month < 1 || month > 12) {
            return false;
        }
        int daysInMonth = getDaysInMonth(month, year);
        return day >= 1 && day <= daysInMonth;
    }

    // Parse date from string formatted as YYYY-MM-DD
    public static MyDate parse(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        String[] parts = str.split("-");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Date must be in format YYYY-MM-DD");
        }
        int year = Integer.parseInt(parts[0].trim());
        int month = Integer.parseInt(parts[1].trim());
        int day = Integer.parseInt(parts[2].trim());
        return new MyDate(day, month, year);
    }

    // Format to YYYY-MM-DD for database persistence
    public String toDbString() {
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    // Format for user-friendly display: DD-MM-YYYY
    @Override
    public String toString() {
        return String.format("%02d-%02d-%04d", day, month, year);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MyDate other = (MyDate) obj;
        return this.day == other.day && this.month == other.month && this.year == other.year;
    }

    @Override
    public int hashCode() {
        return 31 * (31 * day + month) + year;
    }
}
