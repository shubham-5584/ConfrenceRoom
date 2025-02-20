package com.conferenceroom.com;



import java.time.LocalDate;
import java.time.LocalTime;

public class Booking {
    private int bookingId;
    private int userId;
    private int roomId;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;

    public Booking() {}

    public Booking(int bookingId, int userId, int roomId, LocalDate bookingDate, LocalTime startTime, LocalTime endTime, String status) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.roomId = roomId;
        this.bookingDate = bookingDate;
        setStartTime(startTime);
        setEndTime(endTime);
        setStatus(status);
    }

    public int getBookingId() {
        return bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public int getRoomId() {
        return roomId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStartTime(LocalTime startTime) {
        if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null.");
        }
        if (endTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        if (endTime == null) {
            throw new IllegalArgumentException("End time cannot be null.");
        }
        if (startTime != null && endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time must be after start time.");
        }
        this.endTime = endTime;
    }

    public void setStatus(String status) {
        if (!status.equals("CONFIRMED") && !status.equals("CANCELLED")) {
            throw new IllegalArgumentException("Invalid status. Status must be either 'CONFIRMED' or 'CANCELLED'.");
        }
        this.status = status;
    }
}