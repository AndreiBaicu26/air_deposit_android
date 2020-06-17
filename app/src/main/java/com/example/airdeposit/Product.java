package com.example.airdeposit;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;

public class Product implements Parcelable,Cloneable {

    private double price;
    private String documentId;
    private String name;
    private int boh;
    private int foh;
    private int noOfPlayers;
    private String size;
    private HashMap<String, Integer> placesDeposited;


    private int productsInProcessing;

    public Product(){ }

    public void setDocumentID(String documentID) {
        this.documentId = documentID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBoh(int boh) {
        this.boh = boh;
    }

    public void setFoh(int foh) {
        this.foh = foh;
    }

    public void setNoOfPlayers(int noOfPlayers) {
        this.noOfPlayers = noOfPlayers;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Product(String documentID, String nameOfProduct, int boh, int foh, int noOfPlayers, String size) {
        this.documentId = documentID;
        this.name = nameOfProduct;
        this.boh = boh;
        this.foh = foh;
        this.noOfPlayers = noOfPlayers;
        this.size = size;
    }

    protected Product(Parcel in) {
        documentId = in.readString();
        name = in.readString();
        boh = in.readInt();
        foh = in.readInt();
        noOfPlayers = in.readInt();
        size = in.readString();
    }

    public int getProductsInProcessing() {
        if(this.placesDeposited.containsKey("Processing")){
            return this.placesDeposited.get("Processing");
        }else{
            return 0;
        }

    }


    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentId);
        dest.writeString(name);
        dest.writeInt(boh);
        dest.writeInt(foh);
        dest.writeInt(noOfPlayers);
        dest.writeString(size);
    }

    public String getDocumentID() {
        return documentId;
    }

    public String getName() {
        return name;
    }

    public int getBoh() {
        return boh;
    }

    public int getFoh() {
        return foh;
    }

    public int getNoOfPlayers() {
        return noOfPlayers;
    }

    public String getSize() {
        return size;
    }

    public HashMap<String, Integer> getPlacesDeposited() {
        return placesDeposited;
    }

    public void removeProductFromStorage(String storageID){
        this.placesDeposited.remove(storageID);

    }



    public void addProductToStorage(StorageSpace storage) throws Exception {
        String storageID = storage.getStorageID();

        if(this.placesDeposited == null){
            this.placesDeposited = new HashMap<>();
        }

        int productsDeposited = this.boh-this.getUnStoredProducts();
//        for(String key : this.placesDeposited.keySet()){
//            if(!key.equals("Processing")) {
//                productsDeposited += this.placesDeposited.get(key);
//            }
//        }

        if(this.boh == productsDeposited) throw new Exception("No more "+ this.getName() + " products to store");

        if(this.placesDeposited.containsKey(storageID)){
            int currentNumberInStorage = this.placesDeposited.get(storageID);
            currentNumberInStorage++;
            this.placesDeposited.put(storageID, currentNumberInStorage);
        }else{
            this.placesDeposited.put(storageID,1);
        }
    }

    public void addToProcessing(){
        String storageID = "Processing";

        if(this.placesDeposited == null){
            this.placesDeposited = new HashMap<>();
        }


        if(this.placesDeposited.containsKey(storageID)){
            int currentNumberInStorage = this.placesDeposited.get(storageID);
            currentNumberInStorage++;
            this.placesDeposited.put(storageID, currentNumberInStorage);
        }else{
            this.placesDeposited.put(storageID,1);
        }
    }

    public void setPlacesDeposited(HashMap<String, Integer> placesDeposited) {
        this.placesDeposited = placesDeposited;
    }

    public  int getUnStoredProducts(){

        if(this.placesDeposited.get("Processing") == null ){
            return 0;
        }else{
            return this.placesDeposited.get("Processing");
        }

    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {

        Product product = (Product) super.clone();
        HashMap<String, Integer> clonedPlacesDeposited = new HashMap<>();
        clonedPlacesDeposited.putAll(this.placesDeposited);
        product.setPlacesDeposited(clonedPlacesDeposited);
        return product;

    }
}
