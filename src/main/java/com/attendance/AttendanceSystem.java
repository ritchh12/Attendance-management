package com.attendance;

import java.time.LocalDate;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceSystem implements Serializable{
    private static final long serialVersionUID = 1L;
    private Map<String, Student> students;
    private Map<String, Map<LocalDate, Boolean>> attendance;

    public AttendanceSystem() {
        students = new HashMap<>();
        attendance = new HashMap<>();
    }

    public void addStudent(String studentId, String studentName) {
        Student student = new Student(studentId, studentName);
        students.put(studentId, student);
        attendance.put(studentId, new HashMap<>());
    }

    public void deleteStudent(String studentId) {
        students.remove(studentId);
        attendance.remove(studentId);
    }

    public void markAttendance(String studentId, LocalDate date) {
        if (students.containsKey(studentId)) {
            attendance.get(studentId).put(date, true);
        }
    }

    public void markAbsent(String studentId, LocalDate date) {
        if (students.containsKey(studentId)) {
            attendance.get(studentId).put(date, false);
        }
    }

    public List<Student> getStudents() {
        return new ArrayList<>(students.values());
    }

    public boolean isStudentPresent(String studentId, LocalDate date) {
        Map<LocalDate, Boolean> studentAttendance = attendance.get(studentId);
        return studentAttendance != null && studentAttendance.getOrDefault(date, false);
    }

    public double getAttendancePercentage(String studentId, LocalDate date) {
        Map<LocalDate, Boolean> studentAttendance = attendance.get(studentId);
        if (studentAttendance == null) {
            return 0;
        }

        long totalDays = studentAttendance.size();
        long presentDays = studentAttendance.entrySet().stream()
                .filter(entry -> entry.getValue())
                .count();

        return totalDays > 0 ? (double) presentDays / totalDays * 100 : 0;
    }

    public List<Student> getStudentsBelowAttendanceThreshold(int thresholdPercentage, LocalDate date) {
        List<Student> filteredStudents = new ArrayList<>();
        for (Student student : students.values()) {
            double attendancePercentage = getAttendancePercentage(student.getStudentId(), date);
            if (attendancePercentage < thresholdPercentage) {
                filteredStudents.add(student);
            }
        }
        return filteredStudents;
    }

    public void loadAttendanceForDate(LocalDate date) {
        // This method is left blank for now because attendance for a specific date
        // will be managed by the methods markAttendance and markAbsent.
    }
}
