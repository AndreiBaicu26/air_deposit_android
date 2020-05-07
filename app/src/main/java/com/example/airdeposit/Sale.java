package com.example.airdeposit;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Calendar;
import java.util.Date;


public class Sale {

    private Product product;
    private Date dateCreated;
    private boolean isRefilled;

    public Sale(Product product) {
     this.product = product;
        this.dateCreated =  Calendar.getInstance().getTime();;
        this.isRefilled = false;
    }

    public Sale() {

    }


    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isRefilled() {
        return isRefilled;
    }

    public void setRefilled(boolean refilled) {
        isRefilled = refilled;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "product=" + product +
                ", dateCreated=" + dateCreated +
                ", isRefilled=" + isRefilled +
                '}';
    }
}
