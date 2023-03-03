package com.zoramedic.zoramedicapp.data;

import com.google.firebase.Timestamp;
import com.zoramedic.zoramedicapp.view.util.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Message {
    private String message;
    private String creatorID;
    private String creatorName;
    private Timestamp dateAndTime;
    private String docRef;

    public Message () {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Timestamp getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(Timestamp dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getDocRef() {
        return docRef;
    }

    public void setDocRef(String docRef) {
        this.docRef = docRef;
    }

    public String convertTime() {
        Date date = new Date(getDateAndTime().toDate().getTime());
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIME_FORMAT);
        return sdf.format(date);
    }

    public String convertDate() {
        Date date = new Date(getDateAndTime().toDate().getTime());
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FROMAT);
        return sdf.format(date);
    }
}
