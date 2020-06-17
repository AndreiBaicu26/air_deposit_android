package com.example.airdeposit;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.airdeposit.callbacks.CallBackProduct;
import com.example.airdeposit.callbacks.CallbackAddStorage;
import com.example.airdeposit.callbacks.CallbackEmployee;
import com.example.airdeposit.callbacks.CallbackGetAllStorageSpaces;
import com.example.airdeposit.callbacks.CallbackGetStorage;
import com.example.airdeposit.callbacks.CallbackProductAddedToStorage;
import com.example.airdeposit.callbacks.CallbackSuccessMessage;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Firebase  {

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void deleteStorageSpace(StorageSpace s){

        db.collection("storageSpaces").document(s.getStorageID()).delete();
        db.collection("products").whereGreaterThan("placesDeposited."+s.getStorageID(),0).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult() != null){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product p = document.toObject(Product.class);
                            p.getPlacesDeposited().remove(s.getStorageID());
                            document.getReference().update("placesDeposited",p.getPlacesDeposited());
                            Firebase.updateAllSales(p);
                        }
                        }
                    }
                }
            });
    }


    public static void getProduct(String productBarCode, final CallBackProduct callBackProduct) {

        db.collection("products").document(productBarCode).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                            DocumentSnapshot data = task.getResult();

                            Product p = data.toObject(Product.class);


                        callBackProduct.onCallbackProduct(p);

                    }else{
                        callBackProduct.onCallbackProduct(null);
                    }
                }else{
                    throw new Error("Error while fetching data");
                }
            }
        });
    }

    public static void getEmployee(int employeeID, final CallbackEmployee callback){
        CollectionReference collectionReference = db.collection("employees");

        collectionReference.whereEqualTo("id",employeeID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){

                        if(!task.getResult().isEmpty()){

                            for(DocumentSnapshot data: task.getResult()){
                                callback.onCallback(data.toObject(Employee.class));
                            }

                        }else{
                            callback.onCallback(null);
                        }

                    }else{

                    }
            }
        });


    }

    public static void emptyStorage(StorageSpace storage, final CallbackSuccessMessage callbackSuccessMessage) {
        db.collection("storageSpaces").document(storage.getStorageID())
                .update("storedProducts", storage.getStoredProducts(), "maxBig", storage.getMaxBig(),
                        "maxMedium", storage.getMaxMedium(),
                        "maxSmall", storage.getMaxSmall())
                .addOnSuccessListener(aVoid -> callbackSuccessMessage.onSuccessMessage("Storage is now empty"));

    }

    public static void removeProductFromStorage(Product product){
            Firebase.updateAllSales(product);
            db.collection("products").document(product.getDocumentID()).update("foh",product.getFoh(),"boh",product.getBoh() ,"placesDeposited", product.getPlacesDeposited());

    }

    public static void updateAllSales(Product p ){
        db.collection("sales").whereEqualTo("product.name",p.getName()).whereEqualTo("refilled",false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult() != null){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                           db.collection("sales").document(document.getId()).update("product",p);
                        }
                    }
                }
            }

        });


    }

    public static void removeFromProcessing(String prodName, int quantity) {
        db.collection("storageSpaces").document("Processing").update("storedProducts."+prodName, quantity);
    }

    public static void saleProduct(Product product){
        Map<String, Object> data = new HashMap<>();
        Timestamp t = new Timestamp(new Date());
        data.put("exitDate", t );
        Firebase.updateAllSales(product);
        db.collection("products").document(product.getDocumentID()).collection("exits").add(data);
        db.collection("products").document(product.getDocumentID()).update("foh", product.getFoh());
    }

    public  static void updateStorage(StorageSpace storage){

        db.collection("storageSpaces").document(storage.getStorageID())
                .update("storedProducts", storage.getStoredProducts(), "maxBig", storage.getMaxBig(),
                        "maxMedium", storage.getMaxMedium(),
                        "maxSmall", storage.getMaxSmall());
    }

    public static void addStorageSpace(String storageID, final CallbackAddStorage callbackAddStorage){

      CollectionReference storageRef = db.collection("storageSpaces");

        storageRef.document(storageID).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(task.getResult().exists()){
                   callbackAddStorage.onCallbackAddStorage(null);
                }else{

                   StorageSpace space = new StorageSpace(storageID,null);
                    storageRef.document(storageID).set(space).addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            callbackAddStorage.onCallbackAddStorage(space);

                        }else{
                            Log.e("firestore", "error while adding space");
                        }
                    });
                }
            }else{
                Log.e("firestore", "error while searching for ID");
            }
        });
    }


    public static void addProductToStorage(StorageSpace storage, Product product, final CallbackProductAddedToStorage callback){
        Firebase.updateAllSales(product);
        db.collection("storageSpaces").document(storage.getStorageID())
                .update("storedProducts",storage.getStoredProducts(),"maxBig",storage.getMaxBig(),
                        "maxMedium", storage.getMaxMedium(),
                        "maxSmall", storage.getMaxSmall())
                .addOnSuccessListener(aVoid -> {
                callback.onProductAddedToStorage("Product stored successfully");
        }).addOnFailureListener(e -> {
            callback.onProductAddedToStorage("Error while storing product");
        });

        db.collection("products").document(product.getDocumentID()).update("placesDeposited", product.getPlacesDeposited())
                .addOnFailureListener(aVoid->{
                   Log.e("Firestore", "Could not add Product to storage");
                });
    }

    public static void receivedNewProducts(String productID, int quantity, int entryQuantity){
       db.collection("products").document(productID).update("boh",quantity );
        Map<String, Object> data = new HashMap<>();
        Timestamp t = new Timestamp(new Date());
        data.put("entryDate", t );
        data.put("prductsEntered", entryQuantity);
        db.collection("products").document(productID).collection("entries").add(data);
    }

    public static Query queryForStoragesRecyclerView(){
        Query query = db.collection("storageSpaces").orderBy("storageID", Query.Direction.ASCENDING);

        return query;
    }

    public static Query queryForProductInStorageRecyclerView(String storageID){
        String path = "placesDeposited."+storageID;
        Query query = db.collection("products").whereGreaterThan( "placesDeposited."+storageID,0);
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    if(!task.getResult().isEmpty()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Log.d("firreeeeeeeee got", document.getData().toString());
//
//                        }
//                    }else
//                    {
//                        Log.d("firreeeeeeeee empty", task.getResult().toString());
//
//                    }
//                } else {
//                    Log.d("firreeeeeeeee", task.getException().toString());
//
//                }
//            }
//        });

        return query;

    }

    public static Query queryForRefill(){
        Query query = db.collection("sales").whereEqualTo("refilled",false);


        return query;
    }

    public static void createSale(Sale s){

        CollectionReference salesRef = db.collection("sales");
        salesRef.add(s);
    }

    public static void  getStorage(String storageID, final CallbackGetStorage callbackGetStorage){
        db.collection("storageSpaces").document(storageID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult() != null) {

                        StorageSpace s = task.getResult().toObject(StorageSpace.class);

                        callbackGetStorage.onCallbackGetStorage(s);
                    }else{
                        Log.d("storageeee", task.getResult().toString());
                    }
                }
            }
        });
    }


    public static void getAllStorages(final CallbackGetAllStorageSpaces callback){

        db.collection("storageSpaces").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<StorageSpace> storageSpaces = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        storageSpaces.add(document.toObject(StorageSpace.class));
                    }
                    callback.callbackGetAllStorageSpaces(storageSpaces);
                } else {
                    Log.d("error", "Error getting documents: ", task.getException());
                }
            }
        });
    }


}
