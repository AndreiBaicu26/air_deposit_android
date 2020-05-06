package com.example.airdeposit;


import com.google.firebase.firestore.DocumentReference;

import java.util.Calendar;
import java.util.Date;


public class Sale {

   // private Product product;
    private DocumentReference productRef;
    private Date dateCreated;
    private boolean isRefilled;

    public Sale(DocumentReference product, boolean isRefilled) {
        this.productRef = product;

        this.dateCreated = Calendar.getInstance().getTime();
        this.isRefilled = isRefilled;
//        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
//        calendar.setTime(dateCreated);   // assigns calendar to given date
//        calendar.get(Calendar.HOUR_OF_DAY); // get
    }

    public Sale() {

    }

//    public void setProduct(Product p){
//
//    }

//    public Product getProduct(){
//        return this.product;
//    }

    public DocumentReference getProductRef() {
        return productRef;
    }

    public void setProductRef(DocumentReference product) {
        this.productRef = product;
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

    @Override
    public String toString() {
        return "Sale{" +
                "product="  +
                ", dateCreated=" + dateCreated +
                ", isRefilled=" + isRefilled +
                '}';
    }
}
