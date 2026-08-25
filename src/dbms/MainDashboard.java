package dbms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class MainDashboard extends JFrame {
    // Database Manager Instance
    // All static methods in DatabaseManager are used.

    // --- GUI Components ---
    private JTabbedPane tabbedPane;

    // Student Tab Components
    private JTextField txtStudentId;
    private JTextField txtStudentName;
    private JTextField txtStudentEmail;
    private JRadioButton rbMale, rbFemale, rbOther;
    private ButtonGroup genderGroup;
    private JTextField txtEnrollmentDate;
    private JButton btnSelectDate;
    private JTable tblStudents;
    private DefaultTableModel modelStudents;
    private MyDate selectedEnrollmentDate;

    // Unit Tab Components
    private JTextField txtUnitCode;
    private JTextField txtUnitTitle;
    private JSpinner spinCredits;
    private JTextField txtDepartment;
    private JTable tblUnits;
    private DefaultTableModel modelUnits;

    // Timetable Tab Components
    private JTextField txtTimetableId;
    private JComboBox<Student> cbStudents;
    private JComboBox<Unit> cbUnits;
    private JComboBox<DayOfWeek> cbClassDay;
    private JTextField txtClassTime;
    private JTable tblTimetable;
    private DefaultTableModel modelTimetable;

    public MainDashboard() {
        super("Student Enrollment and Timetable Management System");
        
        // Initialize SQLite Database
        DatabaseManager.initializeDatabase();

        // Use System Look and Feel for cleaner UI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185)); // Sleek Blue
        headerPanel.setPreferredSize(new Dimension(1000, 70));
        JLabel titleLabel = new JLabel("University Timetable & Enrollment Portal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane Setup
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        createStudentTab();
        createUnitTab();
        createTimetableTab();

        add(tabbedPane, BorderLayout.CENTER);

        // Load Initial Data
        refreshStudentsTable();
        refreshUnitsTable();
        refreshTimetableTable();
        refreshTimetableComboBoxes();
    }

    // ==========================================
    // STUDENT MANAGEMENT TAB
    // ==========================================
    private void createStudentTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Form Panel (Left)
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
                "Student Details", 0, 0, 
                new Font("Segoe UI", Font.BOLD, 14), new Color(41, 128, 185)
        ));
        formPanel.setPreferredSize(new Dimension(380, 500));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        txtStudentId = new JTextField();
        txtStudentId.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtStudentId, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        txtStudentName = new JTextField();
        txtStudentName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtStudentName, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Email Address:"), gbc);
        gbc.gridx = 1;
        txtStudentEmail = new JTextField();
        txtStudentEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtStudentEmail, gbc);

        // Gender (Enum - Radio Buttons)
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        rbMale = new JRadioButton("Male");
        rbFemale = new JRadioButton("Female");
        rbOther = new JRadioButton("Other");
        genderGroup = new ButtonGroup();
        genderGroup.add(rbMale);
        genderGroup.add(rbFemale);
        genderGroup.add(rbOther);
        genderPanel.add(rbMale);
        genderPanel.add(rbFemale);
        genderPanel.add(rbOther);
        formPanel.add(genderPanel, gbc);

        // Enrollment Date (Custom Date class with Date Picker)
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Enrollment Date:"), gbc);
        gbc.gridx = 1;
        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        txtEnrollmentDate = new JTextField();
        txtEnrollmentDate.setEditable(false);
        txtEnrollmentDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnSelectDate = new JButton("Select...");
        btnSelectDate.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnSelectDate.addActionListener(e -> {
            DatePickerDialog dp = new DatePickerDialog(this, selectedEnrollmentDate);
            dp.setVisible(true);
            MyDate date = dp.getSelectedDate();
            if (date != null) {
                selectedEnrollmentDate = date;
                txtEnrollmentDate.setText(selectedEnrollmentDate.toString());
            }
        });
        datePanel.add(txtEnrollmentDate, BorderLayout.CENTER);
        datePanel.add(btnSelectDate, BorderLayout.EAST);
        formPanel.add(datePanel, gbc);

        // Buttons Panel
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NORTH;
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        formPanel.add(btnPanel, gbc);

        panel.add(formPanel, BorderLayout.WEST);

        // 2. Table Panel (Right)
        String[] columns = {"ID", "Name", "Email", "Gender", "Enrollment Date"};
        modelStudents = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only cells
            }
        };
        tblStudents = new JTable(modelStudents);
        tblStudents.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblStudents.setRowHeight(22);
        tblStudents.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblStudents.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblStudents.getSelectedRow();
                if (row != -1) {
                    txtStudentId.setText(modelStudents.getValueAt(row, 0).toString());
                    txtStudentId.setEditable(false); // ID key is unique, cannot change on update
                    txtStudentName.setText(modelStudents.getValueAt(row, 1).toString());
                    txtStudentEmail.setText(modelStudents.getValueAt(row, 2).toString());
                    
                    String genderStr = modelStudents.getValueAt(row, 3).toString();
                    if (genderStr.equalsIgnoreCase("Male")) rbMale.setSelected(true);
                    else if (genderStr.equalsIgnoreCase("Female")) rbFemale.setSelected(true);
                    else rbOther.setSelected(true);

                    String dateStr = modelStudents.getValueAt(row, 4).toString();
                    // Date display format is DD-MM-YYYY, but DB string is YYYY-MM-DD
                    // Let's parse DD-MM-YYYY to MyDate
                    String[] dateParts = dateStr.split("-");
                    int day = Integer.parseInt(dateParts[0]);
                    int month = Integer.parseInt(dateParts[1]);
                    int year = Integer.parseInt(dateParts[2]);
                    selectedEnrollmentDate = new MyDate(day, month, year);
                    txtEnrollmentDate.setText(selectedEnrollmentDate.toString());
                }
            }
        });
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblSearch = new JLabel("Search Students:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchPanel.add(lblSearch);
        JTextField txtSearchStudents = new JTextField(20);
        txtSearchStudents.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchPanel.add(txtSearchStudents);
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(tblStudents), BorderLayout.CENTER);

        TableRowSorter<DefaultTableModel> sorterStudents = new TableRowSorter<>(modelStudents);
        tblStudents.setRowSorter(sorterStudents);
        txtSearchStudents.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearchStudents.getText().trim();
                if (text.isEmpty()) {
                    sorterStudents.setRowFilter(null);
                } else {
                    sorterStudents.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        panel.add(tablePanel, BorderLayout.CENTER);


        // 3. Button Action Listeners
        btnAdd.addActionListener(e -> {
            try {
                if (validateStudentInput()) {
                    int id = Integer.parseInt(txtStudentId.getText().trim());
                    if (DatabaseManager.studentExists(id)) {
                        JOptionPane.showMessageDialog(this, "Student ID " + id + " already exists. Please choose a different ID.", "Duplicate ID", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    String name = txtStudentName.getText().trim();
                    String email = txtStudentEmail.getText().trim();
                    Gender gender = rbMale.isSelected() ? Gender.MALE : (rbFemale.isSelected() ? Gender.FEMALE : Gender.OTHER);
                    Student s = new Student(id, name, email, gender, selectedEnrollmentDate);
                    DatabaseManager.insertStudent(s);
                    JOptionPane.showMessageDialog(this, "Student added successfully!");
                    clearStudentFields();
                    refreshStudentsTable();
                    refreshTimetableComboBoxes();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Student ID must be a numeric integer.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding student: Student ID may already exist.\n" + ex.getMessage());
            }
        });


        btnUpdate.addActionListener(e -> {
            try {
                if (validateStudentInput()) {
                    int id = Integer.parseInt(txtStudentId.getText().trim());
                    String name = txtStudentName.getText().trim();
                    String email = txtStudentEmail.getText().trim();
                    Gender gender = rbMale.isSelected() ? Gender.MALE : (rbFemale.isSelected() ? Gender.FEMALE : Gender.OTHER);
                    Student s = new Student(id, name, email, gender, selectedEnrollmentDate);
                    DatabaseManager.updateStudent(s);
                    JOptionPane.showMessageDialog(this, "Student updated successfully!");
                    clearStudentFields();
                    refreshStudentsTable();
                    refreshTimetableTable(); // Cascade update display
                    refreshTimetableComboBoxes();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error updating student: " + ex.getMessage());
            }
        });

        btnDelete.addActionListener(e -> {
            String idStr = txtStudentId.getText().trim();
            if (idStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select or enter a Student ID to delete.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this student? All linked timetable sessions will be removed.", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int id = Integer.parseInt(idStr);
                    DatabaseManager.deleteStudent(id);
                    JOptionPane.showMessageDialog(this, "Student deleted successfully!");
                    clearStudentFields();
                    refreshStudentsTable();
                    refreshTimetableTable(); // Cascade delete check
                    refreshTimetableComboBoxes();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Student ID must be a numeric integer.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting student: " + ex.getMessage());
                }
            }
        });

        btnClear.addActionListener(e -> clearStudentFields());

        tabbedPane.addTab("Manage Students", panel);
    }

    private boolean validateStudentInput() {
        String idStr = txtStudentId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID is required.");
            return false;
        }
        try {
            int id = Integer.parseInt(idStr);
            if (id <= 0) {
                JOptionPane.showMessageDialog(this, "Student ID must be a positive integer.");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Student ID must be a numeric integer.");
            return false;
        }

        if (txtStudentName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student Name is required.");
            return false;
        }

        String email = txtStudentEmail.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email is required.");
            return false;
        }
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (!email.matches(emailRegex)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address (e.g. user@domain.com).");
            return false;
        }

        if (!rbMale.isSelected() && !rbFemale.isSelected() && !rbOther.isSelected()) {
            JOptionPane.showMessageDialog(this, "Gender must be selected.");
            return false;
        }
        if (selectedEnrollmentDate == null) {
            JOptionPane.showMessageDialog(this, "Enrollment Date must be selected.");
            return false;
        }
        return true;
    }


    private void clearStudentFields() {
        txtStudentId.setText("");
        txtStudentId.setEditable(true);
        txtStudentName.setText("");
        txtStudentEmail.setText("");
        genderGroup.clearSelection();
        txtEnrollmentDate.setText("");
        selectedEnrollmentDate = null;
        tblStudents.clearSelection();
    }

    private void refreshStudentsTable() {
        modelStudents.setRowCount(0);
        List<Student> students = DatabaseManager.getAllStudents();
        for (Student s : students) {
            modelStudents.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    s.getEmail(),
                    s.getGender().toString(),
                    s.getEnrollmentDate().toString()
            });
        }
    }


    // ==========================================
    // UNIT MANAGEMENT TAB
    // ==========================================
    private void createUnitTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Form Panel (Left)
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
                "Unit Details", 0, 0, 
                new Font("Segoe UI", Font.BOLD, 14), new Color(41, 128, 185)
        ));
        formPanel.setPreferredSize(new Dimension(380, 500));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Unit Code
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Unit Code:"), gbc);
        gbc.gridx = 1;
        txtUnitCode = new JTextField();
        txtUnitCode.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtUnitCode, gbc);

        // Title
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Unit Title:"), gbc);
        gbc.gridx = 1;
        txtUnitTitle = new JTextField();
        txtUnitTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtUnitTitle, gbc);

        // Credits (Spinner)
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Credits:"), gbc);
        gbc.gridx = 1;
        spinCredits = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        spinCredits.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(spinCredits, gbc);

        // Department
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        txtDepartment = new JTextField();
        txtDepartment.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtDepartment, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NORTH;
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        formPanel.add(btnPanel, gbc);

        panel.add(formPanel, BorderLayout.WEST);

        // 2. Table Panel (Right)
        String[] columns = {"Unit Code", "Title", "Credits", "Department"};
        modelUnits = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblUnits = new JTable(modelUnits);
        tblUnits.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblUnits.setRowHeight(22);
        tblUnits.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblUnits.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblUnits.getSelectedRow();
                if (row != -1) {
                    txtUnitCode.setText(modelUnits.getValueAt(row, 0).toString());
                    txtUnitCode.setEditable(false);
                    txtUnitTitle.setText(modelUnits.getValueAt(row, 1).toString());
                    spinCredits.setValue(Integer.parseInt(modelUnits.getValueAt(row, 2).toString()));
                    txtDepartment.setText(modelUnits.getValueAt(row, 3).toString());
                }
            }
        });
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblSearch = new JLabel("Search Units:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchPanel.add(lblSearch);
        JTextField txtSearchUnits = new JTextField(20);
        txtSearchUnits.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchPanel.add(txtSearchUnits);
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(tblUnits), BorderLayout.CENTER);

        TableRowSorter<DefaultTableModel> sorterUnits = new TableRowSorter<>(modelUnits);
        tblUnits.setRowSorter(sorterUnits);
        txtSearchUnits.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearchUnits.getText().trim();
                if (text.isEmpty()) {
                    sorterUnits.setRowFilter(null);
                } else {
                    sorterUnits.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        panel.add(tablePanel, BorderLayout.CENTER);


        // 3. Actions
        btnAdd.addActionListener(e -> {
            if (validateUnitInput()) {
                String code = txtUnitCode.getText().trim().toUpperCase();
                if (DatabaseManager.unitExists(code)) {
                    JOptionPane.showMessageDialog(this, "Unit Code " + code + " already exists. Please choose a different Code.", "Duplicate Code", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String title = txtUnitTitle.getText().trim();
                int credits = (Integer) spinCredits.getValue();
                String dept = txtDepartment.getText().trim();
                Unit u = new Unit(code, title, credits, dept);
                try {
                    DatabaseManager.insertUnit(u);
                    JOptionPane.showMessageDialog(this, "Unit added successfully!");
                    clearUnitFields();
                    refreshUnitsTable();
                    refreshTimetableComboBoxes();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error adding unit: Unit Code may already exist.\n" + ex.getMessage());
                }
            }
        });


        btnUpdate.addActionListener(e -> {
            if (validateUnitInput()) {
                String code = txtUnitCode.getText().trim().toUpperCase();
                String title = txtUnitTitle.getText().trim();
                int credits = (Integer) spinCredits.getValue();
                String dept = txtDepartment.getText().trim();
                Unit u = new Unit(code, title, credits, dept);
                try {
                    DatabaseManager.updateUnit(u);
                    JOptionPane.showMessageDialog(this, "Unit updated successfully!");
                    clearUnitFields();
                    refreshUnitsTable();
                    refreshTimetableTable(); // Cascade display
                    refreshTimetableComboBoxes();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error updating unit: " + ex.getMessage());
                }
            }
        });

        btnDelete.addActionListener(e -> {
            String code = txtUnitCode.getText().trim().toUpperCase();
            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select or enter a Unit Code to delete.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this unit? All linked timetable records will be deleted.", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    DatabaseManager.deleteUnit(code);
                    JOptionPane.showMessageDialog(this, "Unit deleted successfully!");
                    clearUnitFields();
                    refreshUnitsTable();
                    refreshTimetableTable(); // Cascade update
                    refreshTimetableComboBoxes();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting unit: " + ex.getMessage());
                }
            }
        });

        btnClear.addActionListener(e -> clearUnitFields());

        tabbedPane.addTab("Manage Units", panel);
    }

    private boolean validateUnitInput() {
        if (txtUnitCode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Unit Code is required.");
            return false;
        }
        if (txtUnitTitle.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Unit Title is required.");
            return false;
        }
        if (txtDepartment.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Department is required.");
            return false;
        }
        return true;
    }

    private void clearUnitFields() {
        txtUnitCode.setText("");
        txtUnitCode.setEditable(true);
        txtUnitTitle.setText("");
        spinCredits.setValue(3);
        txtDepartment.setText("");
        tblUnits.clearSelection();
    }

    private void refreshUnitsTable() {
        modelUnits.setRowCount(0);
        List<Unit> units = DatabaseManager.getAllUnits();
        for (Unit u : units) {
            modelUnits.addRow(new Object[]{
                    u.getUnitCode(),
                    u.getTitle(),
                    u.getCredits(),
                    u.getDepartment()
            });
        }
    }


    // ==========================================
    // TIMETABLE MANAGEMENT TAB (Link Entity)
    // ==========================================
    private void createTimetableTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Form Panel (Left)
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
                "Schedule Details (Link Entity)", 0, 0, 
                new Font("Segoe UI", Font.BOLD, 14), new Color(41, 128, 185)
        ));
        formPanel.setPreferredSize(new Dimension(380, 500));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Timetable ID (Read Only)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Schedule ID:"), gbc);
        gbc.gridx = 1;
        txtTimetableId = new JTextField();
        txtTimetableId.setEditable(false);
        txtTimetableId.setText("(Auto)");
        txtTimetableId.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtTimetableId, gbc);

        // Student ComboBox
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Select Student:"), gbc);
        gbc.gridx = 1;
        cbStudents = new JComboBox<>();
        cbStudents.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cbStudents, gbc);

        // Unit ComboBox
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Select Unit:"), gbc);
        gbc.gridx = 1;
        cbUnits = new JComboBox<>();
        cbUnits.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cbUnits, gbc);

        // Class Day (Enum ComboBox)
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Class Day:"), gbc);
        gbc.gridx = 1;
        cbClassDay = new JComboBox<>(DayOfWeek.values());
        cbClassDay.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cbClassDay, gbc);

        // Class Time (e.g., dropdown list or text field)
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Class Time:"), gbc);
        gbc.gridx = 1;
        txtClassTime = new JTextField("09:00 AM - 10:30 AM");
        txtClassTime.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtClassTime, gbc);

        // Buttons Panel
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NORTH;
        JPanel btnPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        JButton btnAdd = new JButton("Schedule Class");
        JButton btnUpdate = new JButton("Reschedule");
        JButton btnDelete = new JButton("Remove");
        JButton btnClear = new JButton("Clear");
        JButton btnExport = new JButton("Export Report");
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExport.setBackground(new Color(41, 128, 185));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFocusPainted(false);
        btnExport.setContentAreaFilled(false);
        btnExport.setOpaque(true);

        
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        btnPanel.add(btnExport);
        btnPanel.add(new JLabel("")); // Spacer
        formPanel.add(btnPanel, gbc);

        panel.add(formPanel, BorderLayout.WEST);


        // 2. Table Panel (Right)
        String[] columns = {"ID", "Student ID", "Student Name", "Unit Code", "Unit Title", "Class Day", "Class Time"};
        modelTimetable = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblTimetable = new JTable(modelTimetable);
        tblTimetable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblTimetable.setRowHeight(22);
        tblTimetable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblTimetable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblTimetable.getSelectedRow();
                if (row != -1) {
                    txtTimetableId.setText(modelTimetable.getValueAt(row, 0).toString());
                    
                    int studentId = (Integer) modelTimetable.getValueAt(row, 1);
                    String unitCode = modelTimetable.getValueAt(row, 3).toString();
                    DayOfWeek day = (DayOfWeek) modelTimetable.getValueAt(row, 5);
                    String time = modelTimetable.getValueAt(row, 6).toString();

                    // Set Student in JComboBox
                    for (int i = 0; i < cbStudents.getItemCount(); i++) {
                        Student s = cbStudents.getItemAt(i);
                        if (s.getId() == studentId) {
                            cbStudents.setSelectedIndex(i);
                            break;
                        }
                    }

                    // Set Unit in JComboBox
                    for (int i = 0; i < cbUnits.getItemCount(); i++) {
                        Unit u = cbUnits.getItemAt(i);
                        if (u.getUnitCode().equals(unitCode)) {
                            cbUnits.setSelectedIndex(i);
                            break;
                        }
                    }

                    cbClassDay.setSelectedItem(day);
                    txtClassTime.setText(time);
                }
            }
        });
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblSearch = new JLabel("Search Schedules:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchPanel.add(lblSearch);
        JTextField txtSearchTimetable = new JTextField(20);
        txtSearchTimetable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchPanel.add(txtSearchTimetable);
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(tblTimetable), BorderLayout.CENTER);

        TableRowSorter<DefaultTableModel> sorterTimetable = new TableRowSorter<>(modelTimetable);
        tblTimetable.setRowSorter(sorterTimetable);
        txtSearchTimetable.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearchTimetable.getText().trim();
                if (text.isEmpty()) {
                    sorterTimetable.setRowFilter(null);
                } else {
                    sorterTimetable.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        panel.add(tablePanel, BorderLayout.CENTER);


        // 3. Actions
        btnAdd.addActionListener(e -> {
            Student s = (Student) cbStudents.getSelectedItem();
            Unit u = (Unit) cbUnits.getSelectedItem();
            if (s == null || u == null) {
                JOptionPane.showMessageDialog(this, "A student and unit must be selected to schedule.");
                return;
            }
            DayOfWeek day = (DayOfWeek) cbClassDay.getSelectedItem();
            String time = txtClassTime.getText().trim();
            if (time.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Class time is required.");
                return;
            }

            if (DatabaseManager.checkScheduleConflict(s.getId(), day.name(), time, 0)) {
                JOptionPane.showMessageDialog(this, "Schedule Conflict: This student already has a class scheduled on " + day + " at " + time, "Conflict Detected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Timetable tt = new Timetable(0, s.getId(), u.getUnitCode(), day, time);
            try {
                DatabaseManager.insertTimetable(tt);
                JOptionPane.showMessageDialog(this, "Timetable entry scheduled successfully!");
                clearTimetableFields();
                refreshTimetableTable();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error scheduling: " + ex.getMessage());
            }
        });

        btnUpdate.addActionListener(e -> {
            String ttIdStr = txtTimetableId.getText().trim();
            if (ttIdStr.isEmpty() || ttIdStr.equals("(Auto)")) {
                JOptionPane.showMessageDialog(this, "Please select a timetable record from the table to update.");
                return;
            }

            int ttId = Integer.parseInt(ttIdStr);
            Student s = (Student) cbStudents.getSelectedItem();
            Unit u = (Unit) cbUnits.getSelectedItem();
            if (s == null || u == null) {
                JOptionPane.showMessageDialog(this, "A student and unit must be selected.");
                return;
            }
            DayOfWeek day = (DayOfWeek) cbClassDay.getSelectedItem();
            String time = txtClassTime.getText().trim();
            if (time.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Class time is required.");
                return;
            }

            if (DatabaseManager.checkScheduleConflict(s.getId(), day.name(), time, ttId)) {
                JOptionPane.showMessageDialog(this, "Schedule Conflict: This student already has a class scheduled on " + day + " at " + time, "Conflict Detected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Timetable tt = new Timetable(ttId, s.getId(), u.getUnitCode(), day, time);
            try {
                DatabaseManager.updateTimetable(tt);
                JOptionPane.showMessageDialog(this, "Timetable entry updated successfully!");
                clearTimetableFields();
                refreshTimetableTable();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error updating: " + ex.getMessage());
            }
        });


        btnDelete.addActionListener(e -> {
            String ttIdStr = txtTimetableId.getText().trim();
            if (ttIdStr.isEmpty() || ttIdStr.equals("(Auto)")) {
                JOptionPane.showMessageDialog(this, "Please select a timetable record from the table to remove.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this schedule?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int ttId = Integer.parseInt(ttIdStr);
                    DatabaseManager.deleteTimetable(ttId);
                    JOptionPane.showMessageDialog(this, "Timetable entry deleted successfully!");
                    clearTimetableFields();
                    refreshTimetableTable();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting timetable record: " + ex.getMessage());
                }
            }
        });

        btnClear.addActionListener(e -> clearTimetableFields());

        btnExport.addActionListener(e -> {
            Student selectedStudent = (Student) cbStudents.getSelectedItem();
            if (selectedStudent == null) {
                JOptionPane.showMessageDialog(this, "Please select a student to export their timetable.", "Export Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Let's query schedules for this specific student from the database
            List<Object[]> timetableData = DatabaseManager.getTimetableDisplayData();
            StringBuilder sb = new StringBuilder();
            sb.append("==================================================\n");
            sb.append("         TIMETABLE SCHEDULE REPORT                \n");
            sb.append("==================================================\n\n");
            sb.append("Student ID: ").append(selectedStudent.getId()).append("\n");
            sb.append("Name      : ").append(selectedStudent.getName()).append("\n");
            sb.append("Email     : ").append(selectedStudent.getEmail()).append("\n");
            sb.append("Gender    : ").append(selectedStudent.getGender()).append("\n\n");
            sb.append("--------------------------------------------------\n");
            sb.append(String.format("%-12s | %-30s | %-20s\n", "Day", "Course Unit", "Class Time"));
            sb.append("--------------------------------------------------\n");

            boolean hasSchedules = false;
            for (Object[] row : timetableData) {
                int sId = (Integer) row[1];
                if (sId == selectedStudent.getId()) {
                    String unitCode = (String) row[3];
                    String unitTitle = (String) row[4];
                    String day = row[5].toString();
                    String time = (String) row[6];
                    sb.append(String.format("%-12s | %-30s | %-20s\n", day, unitCode + " - " + unitTitle, time));
                    hasSchedules = true;
                }
            }

            if (!hasSchedules) {
                sb.append("No classes scheduled for this student.\n");
            }
            sb.append("--------------------------------------------------\n\n");
            sb.append("Generated on: ").append(new java.util.Date().toString()).append("\n");
            sb.append("==================================================\n");

            // Open file chooser to save
            JFileChooser fileChooser;
            int userSelection = JFileChooser.CANCEL_OPTION;
            try {
                String systemLAF = UIManager.getSystemLookAndFeelClassName();
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Timetable Report");
                fileChooser.setSelectedFile(new java.io.File("Student_" + selectedStudent.getId() + "_Timetable.txt"));
                userSelection = fileChooser.showSaveDialog(this);
                UIManager.setLookAndFeel(systemLAF);
            } catch (Exception ex) {
                fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Timetable Report");
                fileChooser.setSelectedFile(new java.io.File("Student_" + selectedStudent.getId() + "_Timetable.txt"));
                userSelection = fileChooser.showSaveDialog(this);
            }

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                try (java.io.FileWriter writer = new java.io.FileWriter(fileToSave)) {
                    writer.write(sb.toString());
                    JOptionPane.showMessageDialog(this, "Timetable exported successfully to:\n" + fileToSave.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (java.io.IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "File Save Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        tabbedPane.addTab("Manage Schedules", panel);

    }

    private void clearTimetableFields() {
        txtTimetableId.setText("(Auto)");
        if (cbStudents.getItemCount() > 0) cbStudents.setSelectedIndex(0);
        if (cbUnits.getItemCount() > 0) cbUnits.setSelectedIndex(0);
        cbClassDay.setSelectedIndex(0);
        txtClassTime.setText("09:00 AM - 10:30 AM");
        tblTimetable.clearSelection();
    }

    private void refreshTimetableTable() {
        modelTimetable.setRowCount(0);
        List<Object[]> rows = DatabaseManager.getTimetableDisplayData();
        for (Object[] row : rows) {
            modelTimetable.addRow(row);
        }
    }

    private void refreshTimetableComboBoxes() {
        // Refresh Students ComboBox
        cbStudents.removeAllItems();
        List<Student> students = DatabaseManager.getAllStudents();
        for (Student s : students) {
            cbStudents.addItem(s);
        }

        // Refresh Units ComboBox
        cbUnits.removeAllItems();
        List<Unit> units = DatabaseManager.getAllUnits();
        for (Unit u : units) {
            cbUnits.addItem(u);
        }
    }

    // ==========================================
    // MAIN RUNNER METHOD
    // ==========================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginDialog login = new LoginDialog(null);
            login.setVisible(true);
            if (login.isSucceeded()) {
                MainDashboard mainFrame = new MainDashboard();
                mainFrame.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }

}
