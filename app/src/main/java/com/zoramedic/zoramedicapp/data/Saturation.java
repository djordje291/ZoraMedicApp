package com.zoramedic.zoramedicapp.data;

import com.zoramedic.zoramedicapp.view.util.Constants;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Saturation implements Serializable {
    private Date timestamp;
    private String creatorID;
    private int percentage;

    public Saturation() {
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public String dateAndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_AND_TIME_FORMAT);
        return sdf.format(getTimestamp());
    }

    @Override
    public String toString() {
        return "Saturation{" +
                "timestamp=" + timestamp +
                ", creatorID='" + creatorID + '\'' +
                ", percentage=" + percentage +
                '}';
    }
}
