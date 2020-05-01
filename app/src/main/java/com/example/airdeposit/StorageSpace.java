package com.example.airdeposit;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class StorageSpace {

    private String storageID;
    private List<DocumentReference> storedProducts;
    private int maxBig;
    private int maxMedium;
    private int maxSmall;

    public StorageSpace(String storageID, List<DocumentReference> storedProducts) {
        this.storageID = storageID;
        this.storedProducts = storedProducts;
    }

    public String getStorageID() {
        return storageID;
    }

    public void setStorageID(String storageID) {
        this.storageID = storageID;
    }

    public List<DocumentReference> getStoredProducts() {
        return storedProducts;
    }

    public void setStoredProducts(List<DocumentReference> storedProducts) {
        this.storedProducts = storedProducts;
    }

    public int getMaxBig() {
        return maxBig;
    }

    public void setMaxBig(int maxBig) {
        this.maxBig = maxBig;
    }

    public int getMaxMedium() {
        return maxMedium;
    }

    public void setMaxMedium(int maxMedium) {
        this.maxMedium = maxMedium;
    }

    public int getMaxSmall() {
        return maxSmall;
    }

    public void setMaxSmall(int maxSmall) {
        this.maxSmall = maxSmall;
    }

    public double getFilledPercentage(){
        return 2.4;
    }
}
