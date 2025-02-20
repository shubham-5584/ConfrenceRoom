package com.conferenceroom.com;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class ConferenceRoomBookingApp {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            while (true) {
                System.out.println("\n--- Conference Room Booking System ---");
                System.out.println("1. Add Conference Room");
                System.out.println("2. Remove Conference Room");
                System.out.println("3. Book Conference Room");
                System.out.println("4. Cancel Booking");
                System.out.println("5. Print Daily Timetable");
                System.out.println("6. View Weekly Timetable");
                System.out.println("7. Exit");
                System.out.print("Enter your choice: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> addConferenceRoom();
                    case 2 -> removeConferenceRoom();
                    case 3 -> bookConferenceRoom();
                    case 4 -> cancelBooking();
                    case 5 -> printDailyTimetable();
                    case 6 -> viewWeeklyTimetable();
                    case 7 -> {
                        System.out.println("Exiting the application...");
                        scanner.close();
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice! Please enter a valid option.");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static void addConferenceRoom() {
        System.out.print("Enter room name: ");
        String roomName = scanner.nextLine().trim();
        System.out.print("Enter room capacity: ");
        int capacity = scanner.nextInt();
        scanner.nextLine();

        if (capacity <= 0) {
            System.out.println("Error: Capacity must be greater than 0.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO ConferenceRooms (room_name, capacity) VALUES (?, ?)")) {
            stmt.setString(1, roomName);
            stmt.setInt(2, capacity);
            stmt.executeUpdate();
            System.out.println("Conference room added successfully!");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private static void removeConferenceRoom() {
        System.out.print("Enter room ID to remove: ");
        int roomId = scanner.nextInt();
        scanner.nextLine();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM ConferenceRooms WHERE room_id = ?")) {
            stmt.setInt(1, roomId);
            int rowsDeleted = stmt.executeUpdate();
            System.out.println(rowsDeleted > 0 ? "Conference room removed successfully!" : "Room not found!");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private static void bookConferenceRoom() {
        System.out.print("Enter Employee ID: ");
        int userId = scanner.nextInt();
        System.out.print("Enter room ID: ");
        int roomId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter booking date (YYYY-MM-DD): ");
        LocalDate bookingDate = LocalDate.parse(scanner.nextLine());
        System.out.print("Enter start time (HH:MM): ");
        LocalTime startTime = LocalTime.parse(scanner.nextLine());
        System.out.print("Enter end time (HH:MM): ");
        LocalTime endTime = LocalTime.parse(scanner.nextLine());

        if (endTime.isBefore(startTime)) {
            System.out.println("Error: End time must be after start time.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM Bookings WHERE room_id = ? AND booking_date = ? AND NOT (end_time <= ? OR start_time >= ?)")) {
            checkStmt.setInt(1, roomId);
            checkStmt.setDate(2, Date.valueOf(bookingDate));
            checkStmt.setTime(3, Time.valueOf(startTime));
            checkStmt.setTime(4, Time.valueOf(endTime));
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("Error: Room is already booked for the given time slot!");
                return;
            }

            try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Bookings (employee_id, room_id, booking_date, start_time, end_time, status) VALUES (?, ?, ?, ?, ?, 'CONFIRMED')")) {
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, roomId);
                insertStmt.setDate(3, Date.valueOf(bookingDate));
                insertStmt.setTime(4, Time.valueOf(startTime));
                insertStmt.setTime(5, Time.valueOf(endTime));
                insertStmt.executeUpdate();
                System.out.println("Room booked successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private static void cancelBooking() {
        System.out.print("Enter booking ID to cancel: ");
        int bookingId = scanner.nextInt();
        scanner.nextLine();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Bookings WHERE booking_id = ?")) {
            stmt.setInt(1, bookingId);
            int rowsDeleted = stmt.executeUpdate();
            System.out.println(rowsDeleted > 0 ? "Booking cancelled successfully!" : "Booking not found!");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private static void printDailyTimetable() {
        System.out.print("Enter date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT room_id, employee_id, start_time, end_time, status FROM Bookings WHERE booking_date = ? ORDER BY start_time")) {
            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n--- Daily Timetable for " + date + " ---");
            boolean hasBookings = false;
            while (rs.next()) {
                hasBookings = true;
                System.out.println("Room ID: " + rs.getInt("room_id") +
                        " | Employee ID: " + rs.getInt("employee_id") +
                        " | Time: " + rs.getTime("start_time") + " - " + rs.getTime("end_time") +
                        " | Status: " + rs.getString("status"));
            }
            if (!hasBookings) {
                System.out.println("No bookings found for the selected date.");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private static void viewWeeklyTimetable() {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(7);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT booking_date, room_id, employee_id, start_time, end_time, status FROM Bookings WHERE booking_date BETWEEN ? AND ? ORDER BY booking_date, start_time")) {
            stmt.setDate(1, Date.valueOf(today));
            stmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n--- Weekly Timetable ---");
            boolean hasBookings = false;
            while (rs.next()) {
                hasBookings = true;
                System.out.println("Date: " + rs.getDate("booking_date") +
                        " | Room ID: " + rs.getInt("room_id") +
                        " | Employee ID: " + rs.getInt("employee_id") +
                        " | Time: " + rs.getTime("start_time") + " - " + rs.getTime("end_time") +
                        " | Status: " + rs.getString("status"));
            }
            if (!hasBookings) {
                System.out.println("No bookings found for the next 7 days.");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}