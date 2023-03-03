package com.zoramedic.zoramedicapp.data;

import java.io.Serializable;

public class Pharmacy implements Serializable {

    private String docRef;
    private String name;
    private int quantity;
    private int minimalLimit;
    private boolean hasLow;
    private int price;
    private int used;
    private boolean perOs;


    //need empty for firestore
    public Pharmacy() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getMinimalLimit() {
        return minimalLimit;
    }

    public void setMinimalLimit(int minimalLimit) {
        this.minimalLimit = minimalLimit;
    }

    public boolean isHasLow() {
        return hasLow;
    }

    public void setHasLow(boolean hasLow) {
        this.hasLow = hasLow;
    }

    public String getDocRef() {
        return docRef;
    }

    public void setDocRef(String docRef) {
        this.docRef = docRef;
    }

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isPerOs() {
        return perOs;
    }

    public void setPerOs(boolean perOs) {
        this.perOs = perOs;
    }
}
