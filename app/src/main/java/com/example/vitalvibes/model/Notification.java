package com.example.vitalvibes.model;

public class Notification {
    private String id;
    private String title;
    private String message;
    private long timeStamp;
    private boolean isRead;

    public Notification() {
    }

    public Notification(String id, String title, String message, long timeStamp, boolean isRead) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timeStamp = timeStamp;
        this.isRead = isRead;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
