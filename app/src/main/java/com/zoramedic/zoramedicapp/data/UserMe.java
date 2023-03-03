package com.zoramedic.zoramedicapp.data;

import android.app.Application;

public class UserMe extends Application {
    private String firstName;
    private String lastName;

    private String userFirebaseID;

    private int clearanceLvl;

    private String docRef;
    private String fcmToken;

    private static UserMe instance;

    public static UserMe getInstance() {
        if (instance == null) {
            instance = new UserMe();
        }
        return instance;
    }

    //object for firestore has to have an empty constructor
    public UserMe() {}

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

    public int getClearanceLvl() {
        return clearanceLvl;
    }

    public void setClearanceLvl(int clearanceLvl) {
        this.clearanceLvl = clearanceLvl;
    }

    public String getDocRef() {
        return docRef;
    }

    public void setDocRef(String docRef) {
        this.docRef = docRef;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
