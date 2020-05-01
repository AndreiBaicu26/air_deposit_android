package com.example.airdeposit;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class Product implements Parcelable {

    private String productID;
    private String nameOfProduct;
    private int boh;
    private int foh;
    private int maxPlayers;
    private String size;
    private List<DocumentReference> listOfPlacesDeposited;

    public Product(String productID, String nameOfProduct, int boh, int foh, int maxPlayers, String size) {
        this.productID = productID;
        this.nameOfProduct = nameOfProduct;
        this.boh = boh;
        this.foh = foh;
        this.maxPlayers = maxPlayers;
        this.size = size;
    }


    protected Product(Parcel in) {
        productID = in.readString();
        nameOfProduct = in.readString();
        boh = in.readInt();
        foh = in.readInt();
        maxPlayers = in.readInt();
        size = in.readString();
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
        dest.writeString(productID);
        dest.writeString(nameOfProduct);
        dest.writeInt(boh);
        dest.writeInt(foh);
        dest.writeInt(maxPlayers);
        dest.writeString(size);
    }

    public String getProductID() {
        return productID;
    }

    public String getNameOfProduct() {
        return nameOfProduct;
    }

    public int getBoh() {
        return boh;
    }

    public int getFoh() {
        return foh;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getSize() {
        return size;
    }

    public List<DocumentReference> getListOfPlacesDeposited() {
        return listOfPlacesDeposited;
    }
}
