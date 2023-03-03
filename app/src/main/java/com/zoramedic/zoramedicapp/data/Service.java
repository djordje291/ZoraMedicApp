package com.zoramedic.zoramedicapp.data;

import com.zoramedic.zoramedicapp.view.util.Constants;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Service implements Serializable {
    private Bill bill;
    private List<Pharmacy> pharmacyList;
    private String description;
    private String creatorID;
    private Date timestamp;

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public List<Pharmacy> getPharmacyList() {
        return pharmacyList;
    }

    public String getPharmacyListFormatted() {
        StringBuilder sb = new StringBuilder();
        if (pharmacyList != null && pharmacyList.size() > 0) {
            for (Pharmacy p : pharmacyList) {
                sb.append(p.getName());
                sb.append(" x ");
                sb.append(p.getUsed());
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public void setPharmacyList(List<Pharmacy> pharmacyList) {
        this.pharmacyList = pharmacyList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String dateAndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_AND_TIME_FORMAT);
        return sdf.format(getTimestamp());
    }

    @Override
    public String toString() {
        return "Service{" +
                "bill=" + bill +
                ", pharmacyList=" + pharmacyList +
                ", description='" + description + '\'' +
                ", creatorID='" + creatorID + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
