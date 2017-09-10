package com.example.androidthings.myproject;

import java.util.List;

class Room {

    private int currentOccupancy;
    private int maximumOccupancy;
    List<Student> students;

    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public void setCurrentOccupancy(int currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }

    public int getMaximumOccupancy() {
        return maximumOccupancy;
    }

    public void setMaximumOccupancy(int maximumOccupancy) {
        this.maximumOccupancy = maximumOccupancy;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

}

class Student {

    private String name;
    private int timestampIn;
    private int timestampOut;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimestampIn() {
        return timestampIn;
    }

    public void setTimestampIn(int timestampIn) {
        this.timestampIn = timestampIn;
    }

    public int getTimestampOut() {
        return timestampOut;
    }

    public void setTimestampOut(int timestampOut) {
        this.timestampOut = timestampOut;
    }

}