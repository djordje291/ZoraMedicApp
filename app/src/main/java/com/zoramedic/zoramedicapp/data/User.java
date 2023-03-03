package com.zoramedic.zoramedicapp.data;

public class User {

    private String firstName;
    private String lastName;
    private String userFirebaseID;
    private String fcmToken;
    private boolean availability;
    private int clearanceLvl;

    public User() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserFirebaseID() {
        return userFirebaseID;
    }

    public void setUserFirebaseID(String userFirebaseID) {
        this.userFirebaseID = userFirebaseID;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public boolean getAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public int getClearanceLvl() {
        return clearanceLvl;
    }

    public void setClearanceLvl(int clearanceLvl) {
        this.clearanceLvl = clearanceLvl;
    }
}
