package com.example.androidthings.myproject;

class DeviceMessage {

    private String mBuilding;
    private Room mRoom;

    public String getBuilding() {
        return mBuilding;
    }

    public void setBuilding(String building) {
        this.mBuilding = building;
    }

    public Room getRoom() {
        return mRoom;
    }

    public void setRoom(Room room) {
        this.mRoom = room;
    }
}

class Room {

    private int mCurrentOccupancy;
    private int mMaximumOccupancy;
    private Student mStudent;

    public int getCurrentOccupancy() {
        return mCurrentOccupancy;
    }

    public void setCurrentOccupancy(int currentOccupancy) {
        this.mCurrentOccupancy = currentOccupancy;
    }

    public int getMaximumOccupancy() {
        return mMaximumOccupancy;
    }

    public void setMaximumOccupancy(int maximumOccupancy) {
        this.mMaximumOccupancy = maximumOccupancy;
    }

    public Student getStudent() {
        return mStudent;
    }

    public void setStudents(Student student) {
        this.mStudent = student;
    }
}

class Student {

    private int mTimestampIn;
    private int mTimestampOut;

    public int getTimestampIn() {
        return mTimestampIn;
    }

    public void setTimestampIn(int timestampIn) {
        this.mTimestampIn = timestampIn;
    }

    public int getTimestampOut() {
        return mTimestampOut;
    }

    public void setTimestampOut(int timestampOut) {
        this.mTimestampOut = timestampOut;
    }
}