package com.example.clubhub.event;

import java.io.Serializable;

public class Event implements Serializable {
    private String eventId; // eventId (sử dụng documentId của Firestore)
    private String eventName;
    private String eventDate;
    private String eventTime;
    private String eventPlace;
    private String eventImageUrl;
    private String clubName;
    private String eventDescription;
    private int registeredUsers;
    private int totalUsers;

    // Constructors
    public Event() {
    }

    public Event(String eventName, String eventDate, String eventTime, String eventPlace,
                 String eventImageUrl, String clubName, String eventDescription,
                 int registeredUsers, int totalUsers) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventPlace = eventPlace;
        this.eventImageUrl = eventImageUrl;
        this.clubName = clubName;
        this.eventDescription = eventDescription;
        this.registeredUsers = registeredUsers;
        this.totalUsers = totalUsers;
    }

    // Getters and setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventPlace() {
        return eventPlace;
    }

    public void setEventPlace(String eventPlace) {
        this.eventPlace = eventPlace;
    }

    public String getEventImageUrl() {
        return eventImageUrl;
    }

    public void setEventImageUrl(String eventImageUrl) {
        this.eventImageUrl = eventImageUrl;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public int getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(Object registeredUsers) {
        if (registeredUsers instanceof String) {
            try {
                this.registeredUsers = Integer.parseInt((String) registeredUsers);  // Chuyển từ String sang int
            } catch (NumberFormatException e) {
                this.registeredUsers = 0;  // Giá trị mặc định nếu không thể chuyển đổi
            }
        } else if (registeredUsers instanceof Number) {
            this.registeredUsers = ((Number) registeredUsers).intValue();  // Nếu là Number thì gán trực tiếp
        } else {
            this.registeredUsers = 0;  // Giá trị mặc định khi kiểu dữ liệu không hợp lệ
        }
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Object totalUsers) {
        if (totalUsers instanceof String) {
            try {
                this.totalUsers = Integer.parseInt((String) totalUsers);  // Chuyển từ String sang int
            } catch (NumberFormatException e) {
                this.totalUsers = 0;  // Giá trị mặc định nếu không thể chuyển đổi
            }
        } else if (totalUsers instanceof Number) {
            this.totalUsers = ((Number) totalUsers).intValue();  // Nếu là Number thì gán trực tiếp
        } else {
            this.totalUsers = 0;  // Giá trị mặc định khi kiểu dữ liệu không hợp lệ
        }
    }
}
