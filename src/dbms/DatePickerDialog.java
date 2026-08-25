package dbms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DatePickerDialog extends JDialog {
    private MyDate selectedDate;
    private int currentMonth;
    private int currentYear;
    private JLabel monthYearLabel;
    private JPanel dayGridPanel;
    private JButton[] dayButtons = new JButton[42];
    
    private final String[] MONTH_NAMES = {
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };

    public DatePickerDialog(Frame parent, MyDate initialDate) {
        super(parent, "Select Date", true);
        
        if (initialDate != null) {
            this.selectedDate = initialDate;
            this.currentMonth = initialDate.getMonth() - 1; // Calendar is 0-indexed for month
            this.currentYear = initialDate.getYear();
        } else {
            GregorianCalendar now = new GregorianCalendar();
            this.currentMonth = now.get(Calendar.MONTH);
            this.currentYear = now.get(Calendar.YEAR);
            this.selectedDate = new MyDate(now.get(Calendar.DAY_OF_MONTH), currentMonth + 1, currentYear);
        }

        setSize(370, 290);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout(5, 5));

        // Header Panel (Month & Year navigation)
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JButton prevBtn = new JButton("<<");
        JButton nextBtn = new JButton(">>");
        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        headerPanel.add(prevBtn, BorderLayout.WEST);
        headerPanel.add(monthYearLabel, BorderLayout.CENTER);
        headerPanel.add(nextBtn, BorderLayout.EAST);
        
        prevBtn.addActionListener(e -> {
            currentMonth--;
            if (currentMonth < 0) {
                currentMonth = 11;
                currentYear--;
            }
            updateCalendar();
        });
        
        nextBtn.addActionListener(e -> {
            currentMonth++;
            if (currentMonth > 11) {
                currentMonth = 0;
                currentYear++;
            }
            updateCalendar();
        });

        add(headerPanel, BorderLayout.NORTH);

        // Calendar Panel
        JPanel calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Days of week header
        JPanel daysOfWeekHeader = new JPanel(new GridLayout(1, 7));
        String[] days = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
        for (String day : days) {
            JLabel lbl = new JLabel(day, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            daysOfWeekHeader.add(lbl);
        }
        calendarPanel.add(daysOfWeekHeader, BorderLayout.NORTH);

        // Day buttons grid
        dayGridPanel = new JPanel(new GridLayout(6, 7, 2, 2));
        for (int i = 0; i < 42; i++) {
            final int index = i;
            dayButtons[i] = new JButton();
            dayButtons[i].setFocusPainted(false);
            dayButtons[i].setMargin(new Insets(0, 0, 0, 0));
            dayButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String text = dayButtons[index].getText();
                    if (!text.isEmpty()) {
                        int dayNum = Integer.parseInt(text);
                        selectedDate = new MyDate(dayNum, currentMonth + 1, currentYear);
                        dispose();
                    }
                }
            });
            dayGridPanel.add(dayButtons[i]);
        }
        calendarPanel.add(dayGridPanel, BorderLayout.CENTER);
        add(calendarPanel, BorderLayout.CENTER);

        // Bottom panel with Cancel and Today buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton todayBtn = new JButton("Today");
        JButton cancelBtn = new JButton("Cancel");
        
        todayBtn.addActionListener(e -> {
            GregorianCalendar now = new GregorianCalendar();
            currentMonth = now.get(Calendar.MONTH);
            currentYear = now.get(Calendar.YEAR);
            selectedDate = new MyDate(now.get(Calendar.DAY_OF_MONTH), currentMonth + 1, currentYear);
            dispose();
        });
        
        cancelBtn.addActionListener(e -> {
            selectedDate = null;
            dispose();
        });
        
        bottomPanel.add(todayBtn);
        bottomPanel.add(cancelBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        updateCalendar();
    }

    private void updateCalendar() {
        monthYearLabel.setText(MONTH_NAMES[currentMonth] + " " + currentYear);

        // Find starting day of the month
        GregorianCalendar cal = new GregorianCalendar(currentYear, currentMonth, 1);
        int startDay = cal.get(Calendar.DAY_OF_WEEK); // 1 = Sunday, 2 = Monday, etc.
        int daysInMonth = MyDate.getDaysInMonth(currentMonth + 1, currentYear);

        // Clear all buttons
        for (int i = 0; i < 42; i++) {
            dayButtons[i].setText("");
            dayButtons[i].setEnabled(false);
            dayButtons[i].setBackground(null);
        }

        // Populate days
        int dayNum = 1;
        for (int i = startDay - 1; dayNum <= daysInMonth; i++) {
            dayButtons[i].setText(String.valueOf(dayNum));
            dayButtons[i].setEnabled(true);
            
            // Highlight selected date
            if (selectedDate != null && selectedDate.getDay() == dayNum 
                && selectedDate.getMonth() == (currentMonth + 1) 
                && selectedDate.getYear() == currentYear) {
                dayButtons[i].setBackground(new Color(160, 200, 240));
            } else {
                dayButtons[i].setBackground(Color.WHITE);
            }
            dayNum++;
        }
    }

    public MyDate getSelectedDate() {
        return selectedDate;
    }
}
