package com.zoramedic.zoramedicapp.data;

import com.google.firebase.Timestamp;
import com.zoramedic.zoramedicapp.view.util.Constants;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BloodPressure implements Serializable {
    private int systolic;
    private int diastolic;
    private int pulse;
    private Date timestamp;
    private String creatorID;

    public int getSystolic() {
        return systolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
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

    public String dateAndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_AND_TIME_FORMAT);
        return sdf.format(getTimestamp());
    }

    @Override
    public String toString() {
        return "BloodPressure{" +
                "systolic=" + systolic +
                ", diastolic=" + diastolic +
                ", pulse=" + pulse +
                ", timestamp=" + timestamp +
                ", creatorID='" + creatorID + '\'' +
                '}';
    }
}
