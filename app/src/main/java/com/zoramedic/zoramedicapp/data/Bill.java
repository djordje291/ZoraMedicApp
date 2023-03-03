package com.zoramedic.zoramedicapp.data;

import java.io.Serializable;

public class Bill implements Serializable {
    private String title;
    private int price;
    private String docRef;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDocRef() {
        return docRef;
    }

    public void setDocRef(String docRef) {
        this.docRef = docRef;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "title='" + title + '\'' +
                ", price='" + price + '\'' +
                ", docRef='" + docRef + '\'' +
                '}';
    }
}
