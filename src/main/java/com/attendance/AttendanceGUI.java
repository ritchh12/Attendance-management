package com.attendance;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class AttendanceGUI {
    private JFrame frame;
    private JPanel mainPanel;
    private JTextField studentIdField;
    private JTextField studentNameField;
    private JTable attendanceTable;
    private JSpinner dateSpinner;
    private JSpinner percentageSpinner; // New JSpinner for percentage input
    private AttendanceSystem attendanceSystem;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AttendanceGUI() {
        attendanceSystem = new AttendanceSystem();
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("Attendance System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));  // Updated layout to add percentage input
        inputPanel.add(new JLabel("Student ID:"));
        studentIdField = new JTextField();
        inputPanel.add(studentIdField);
        inputPanel.add(new JLabel("Student Name:"));
        studentNameField = new JTextField();
        inputPanel.add(studentNameField);
        inputPanel.add(new JLabel("Date:"));

        // Date Spinner
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.addChangeListener(e -> loadAttendanceForSelectedDate());
        inputPanel.add(dateSpinner);

        inputPanel.add(new JLabel("Attendance Threshold (%):"));
        percentageSpinner = new JSpinner(new SpinnerNumberModel(75, 0, 100, 1)); // Percentage input
        inputPanel.add(percentageSpinner);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Add Student");
        JButton deleteButton = new JButton("Delete Student");
        JButton markButton = new JButton("Mark Attendance");
        JButton viewButton = new JButton("View Attendance");
        JButton viewByPercentageButton = new JButton("View Below Attendance Percentage");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(markButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(viewByPercentageButton);  // New button to view students below percentage threshold

        // Attendance Table
        String[] columnNames = {"Student ID", "Name", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        attendanceTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(attendanceTable);

        // Add to Main Panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(tableScrollPane, BorderLayout.SOUTH);

        // Button Actions
        addButton.addActionListener(e -> addStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        markButton.addActionListener(e -> markAttendance());
        viewButton.addActionListener(e -> loadAttendanceForSelectedDate());
        viewByPercentageButton.addActionListener(e -> viewByAttendancePercentage());

        frame.add(mainPanel);
        frame.setVisible(true);

        loadAttendanceForSelectedDate(); // Initial load
    }

    private void addStudent() {
        String id = studentIdField.getText();
        String name = studentNameField.getText();
        if (!id.isEmpty() && !name.isEmpty()) {
            attendanceSystem.addStudent(id, name);
            studentIdField.setText("");
            studentNameField.setText("");
            JOptionPane.showMessageDialog(frame, "Student added successfully!");
            loadAttendanceForSelectedDate();
        } else {
            JOptionPane.showMessageDialog(frame, "Please enter both ID and name!");
        }
    }

    private void deleteStudent() {
        String id = studentIdField.getText();
        if (!id.isEmpty()) {
            attendanceSystem.deleteStudent(id);
            studentIdField.setText("");
            studentNameField.setText("");
            JOptionPane.showMessageDialog(frame, "Student deleted successfully!");
            loadAttendanceForSelectedDate();
        } else {
            JOptionPane.showMessageDialog(frame, "Please enter Student ID to delete!");
        }
    }

    private void markAttendance() {
        String id = studentIdField.getText();
        if (!id.isEmpty()) {
            LocalDate date = getSelectedDate();
            attendanceSystem.markAttendance(id, date);
            studentIdField.setText("");
            loadAttendanceForSelectedDate();
        } else {
            JOptionPane.showMessageDialog(frame, "Please enter student ID!");
        }
    }

    private LocalDate getSelectedDate() {
        java.util.Date date = (java.util.Date) dateSpinner.getValue();
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    private void loadAttendanceForSelectedDate() {
        LocalDate date = getSelectedDate();
        attendanceSystem.loadAttendanceForDate(date);
        updateAttendanceTable(date);
    }

    private void updateAttendanceTable(LocalDate date) {
        DefaultTableModel model = (DefaultTableModel) attendanceTable.getModel();
        model.setRowCount(0);

        List<Student> students = attendanceSystem.getStudents();
        for (Student student : students) {
            model.addRow(new Object[]{
                    student.getStudentId(),
                    student.getName(),
                    attendanceSystem.isStudentPresent(student.getStudentId(), date) ? "Present" : "Absent"
            });
        }
    }

    private void viewByAttendancePercentage() {
        int threshold = (Integer) percentageSpinner.getValue();
        LocalDate selectedDate = getSelectedDate();
        List<Student> filteredStudents = attendanceSystem.getStudentsBelowAttendanceThreshold(threshold, selectedDate);
        DefaultTableModel model = (DefaultTableModel) attendanceTable.getModel();
        model.setRowCount(0);

        for (Student student : filteredStudents) {
            model.addRow(new Object[]{
                    student.getStudentId(),
                    student.getName(),
                    "Below " + threshold + "%"  // Display message indicating below threshold
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AttendanceGUI());
    }
}
