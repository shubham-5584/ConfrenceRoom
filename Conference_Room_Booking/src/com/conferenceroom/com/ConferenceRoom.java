package com.conferenceroom.com;



public class ConferenceRoom {
    private int roomId;
    private String roomName;
    private int capacity;

    // Default constructor
    public ConferenceRoom() {}

    public ConferenceRoom(int roomId, String roomName, int capacity) {
        this.roomId = roomId;
        this.roomName = roomName;
        setCapacity(capacity); // Use setter to apply validation
    }

    public int getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setCapacity(int capacity) {
        if (capacity > 0) {
            this.capacity = capacity;
        } else {
            throw new IllegalArgumentException("Capacity must be greater than 0.");
        }
    }
}