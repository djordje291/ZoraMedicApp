package com.zoramedic.zoramedicapp.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class Patient implements Serializable {

    private String name;
    private String phoneNumber;
    private String bloodType;
    private String JMBG;
    private String address;
    private int weight;
    private int height;
    private String roomNumber;
    private String wordPath;
    private boolean isMale;
    private String birthday;
    private List<Service> services;
    private List<BloodPressure> bloodPressureHistory;
    private String docRef;
    private Saturation saturation;

    //has to have empty constructor for firestore
    public Patient() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        this.isMale = male;
    }

    public List<Service> getServices() {
        if (services == null) {
            services = new ArrayList<>();
        }return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public String getDocRef() {
        return docRef;
    }

    public void setDocRef(String docRef) {
        this.docRef = docRef;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getJMBG() {
        return JMBG;
    }

    public void setJMBG(String JMBG) {
        this.JMBG = JMBG;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getWordPath() {
        return wordPath;
    }

    public void setWordPath(String wordPath) {
        this.wordPath = wordPath;
    }

    public Saturation getSaturation() {
        return saturation;
    }

    public void setSaturation(Saturation saturation) {
        this.saturation = saturation;
    }

    public List<BloodPressure> getBloodPressureHistory() {
        if (bloodPressureHistory == null ) {
            bloodPressureHistory = new ArrayList<>();
        }
        return bloodPressureHistory;
    }

    public void setBloodPressureHistory(List<BloodPressure> bloodPressureHistory) {
        this.bloodPressureHistory = bloodPressureHistory;
    }

    public String getBirthday() {
        if (birthday == null || birthday.isEmpty()) {
            setBirthday();
        }
        return birthday;
    }

    public void setBirthday() {
        int day = Integer.parseInt(JMBG.substring(0,2));
        int month = Integer.parseInt(JMBG.substring(2,4));
        int year;
        if (Integer.parseInt(JMBG.substring(4,7)) > 100) {
            year = 1000 + Integer.parseInt(JMBG.substring(4,7));
        } else {
            year = 2000 + Integer.parseInt(JMBG.substring(4,7));
        }
        birthday = day + "/" + month + "/" + year;
    }

    public int getAge(){
        int day = Integer.parseInt(JMBG.substring(0,2));
        int month = Integer.parseInt(JMBG.substring(2,4));
        int year;
        if (Integer.parseInt(JMBG.substring(4,7)) > 100) {
            year = 1000 + Integer.parseInt(JMBG.substring(4,7));
        } else {
            year = 2000 + Integer.parseInt(JMBG.substring(4,7));
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate birthDate = LocalDate.of(year, month, day);
            LocalDate now = LocalDate.now();
            return Period.between(birthDate, now).getYears();
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", bloodType='" + bloodType + '\'' +
                ", JMBG='" + JMBG + '\'' +
                ", address='" + address + '\'' +
                ", weight=" + weight +
                ", height=" + height +
                ", roomNumber='" + roomNumber + '\'' +
                ", wordPath='" + wordPath + '\'' +
                ", isMale=" + isMale +
                ", birthday='" + birthday + '\'' +
                ", services=" + services +
                ", bloodPressureHistory=" + bloodPressureHistory +
                ", docRef='" + docRef + '\'' +
                ", saturation=" + saturation +
                '}';
    }
}
