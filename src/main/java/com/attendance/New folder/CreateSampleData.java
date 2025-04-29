package com.attendance;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class CreateSampleData {
    public static void main(String[] args) {
        // Create a list to hold student data
        List<Student> students = new ArrayList<>();
        
        // Generate 50 students with IDs S001 to S050 and generic names
        for (int i = 1; i <= 50; i++) {
            String studentId = String.format("S%03d", i);
            String studentName = "Student " + i;
            students.add(new Student(studentId, studentName));
        }

        // Create an attendance system instance
        AttendanceSystem attendanceSystem = new AttendanceSystem();

        // Add students to the system
        for (Student student : students) {
            attendanceSystem.addStudent(student.getStudentId(), student.getName());
        }

        // Simulate marking attendance from 2023 to 2025-04-21
        LocalDate startDate = LocalDate.of(2023, 1, 1); // Start from 2023-01-01
        LocalDate endDate = LocalDate.of(2025, 4, 21); // Until 2025-04-21

        // Randomly mark attendance for each student for every day in the range
        Random random = new Random();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            for (Student student : students) {
                if (random.nextBoolean()) { // Randomly mark attendance as present or absent
                    attendanceSystem.markAttendance(student.getStudentId(), date);
                }
            }
        }

        // Serialize the attendance system to a .ser file
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("attendance_system_data.ser"))) {
            oos.writeObject(attendanceSystem);
            System.out.println("Attendance data for 50 students serialized successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
