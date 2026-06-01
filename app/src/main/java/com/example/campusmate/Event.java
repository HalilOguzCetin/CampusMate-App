package com.example.campusmate;

public class Event {

    private String title;
    private String date;
    private String location;
    private String description;

    private String status;


    public Event(String title, String date, String location, String description, String status) {
        this.title = title;
        this.date = date;
        this.location = location;
        this.description = description;
        this.status = status;
    }

    public Event(String title, String date, String location, String description) {
        this.title = title;
        this.date = date;
        this.location = location;
        this.description = description;
        this.status = "pending";
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}