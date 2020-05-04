package com.example.airdeposit;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class StorageSpace implements Parcelable {

    private String storageID;
    private HashMap<String, Integer> storedProducts;
    private int maxBig;
    private int maxMedium;
    private int maxSmall;


    public StorageSpace(){

    }

    public StorageSpace(String storageID, HashMap<String, Integer> storedProducts) {
        this.storageID = storageID;
        this.storedProducts = storedProducts;
        this.maxBig= 10;
        this.maxMedium = 20;
        this.maxSmall = 40;
    }

    protected StorageSpace(Parcel in) {
        storageID = in.readString();
        maxBig = in.readInt();
        maxMedium = in.readInt();
        maxSmall = in.readInt();
    }

    public static final Creator<StorageSpace> CREATOR = new Creator<StorageSpace>() {
        @Override
        public StorageSpace createFromParcel(Parcel in) {
            return new StorageSpace(in);
        }

        @Override
        public StorageSpace[] newArray(int size) {
            return new StorageSpace[size];
        }
    };

    public String getStorageID() {
        return storageID;
    }

    public void setStorageID(String storageID) {
        this.storageID = storageID;
    }

    public HashMap<String, Integer> getStoredProducts() {
        return storedProducts;
    }

    public void setStoredProducts(HashMap<String, Integer> storedProducts) {
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

    public String getFilledPercentage(){
        float totalProducts = this.maxBig+ this.maxSmall + this.maxMedium;
        float calc = 100 - ((totalProducts * 100) / 70);
        String strDouble = String.format("%.2f", calc);
        return strDouble;
    }

    public void addBigProduct(){

        this.maxBig -=1;
        this.maxMedium -=2;
        this.maxSmall -= 4;

    }

    public void addMediumProduct(){
        if(this.maxMedium % 2 == 0) {
            this.maxBig -= 1;
        }
        this.maxMedium -=1;
        this.maxSmall -=2;


    }

    public void addSmallProduct(){
        if(this.maxSmall % 4 == 0) {
            this.maxBig -= 1;
            this.maxMedium -=1;
            this.maxSmall -=1;
        }else if(this.maxSmall % 2 ==0){
            this.maxMedium -=1;
            this.maxSmall -=1;
        }else{
            this.maxSmall -=1;
        }


    }

    //TODO -- maybe error when searching for p
   public void storeProduct(Product p) throws Exception {
        if(productExists(p)){
            int itemsStored = this.storedProducts.get(p.getNameOfProduct());
            ++itemsStored;
            this.storedProducts.put(p.getNameOfProduct(), itemsStored);
        }else {
            this.storedProducts.put(p.getNameOfProduct(),1);
        }

        if(p.getSize().equals("small")){
            if(this.maxSmall == 0) throw new Exception("No more space for small sized games");
            addSmallProduct();
        }else if(p.getSize().equals("medium")){
            if(this.maxMedium == 0) throw new Exception("No more space for medium sized games");
            addMediumProduct();
        }else{
            if(this.maxBig == 0) throw new Exception("No more space for big sized games");
            addBigProduct();
        }
    }

    private boolean productExists(Product p){
        if(this.storedProducts == null){
            this.storedProducts = new HashMap<String, Integer>();
        }
        if(this.storedProducts.containsKey(p.getNameOfProduct())){
            return true;
        }else{
            return false;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(storageID);
        parcel.writeInt(maxBig);
        parcel.writeInt(maxMedium);
        parcel.writeInt(maxSmall);
    }
}
