package com.example.campusmate;

public class Event {

    private String title;
    private String date;
    private String location;
    private String description;
    private String createdByName;

    public Event() {
    }

    public Event(String title,
                 String date,
                 String location,
                 String description,
                 String createdByName) {

        this.title = title;
        this.date = date;
        this.location = location;
        this.description = description;
        this.createdByName = createdByName;
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

    public String getCreatedByName() {
        return createdByName;
    }
}