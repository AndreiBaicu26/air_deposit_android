package com.example.airdeposit;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.airdeposit.callbacks.CallBackProduct;
import com.example.airdeposit.callbacks.CallbackAddStorage;
import com.example.airdeposit.callbacks.CallbackEmployee;
import com.example.airdeposit.callbacks.CallbackGetStorages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Firebase  {

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void deleteStorageSpace(StorageSpace s){

        db.collection("storageSpaces").document(s.getStorageID()).delete();
    }

    public static void getProduct(String productBarCode, final CallBackProduct callBackProduct) {

        db.collection("products").document(productBarCode).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                            DocumentSnapshot data = task.getResult();

                            Product product = new Product(data.getString("documentId"),data.getString("name"),
                                    Integer.parseInt(data.getString("boh")),
                                    Integer.parseInt(data.getString("foh")),
                                    Integer.parseInt(data.getString("noOfPlayers")),
                                    data.getString("size"));
                            callBackProduct.onCallbackProduct(product);

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
        Query query = collectionReference.whereEqualTo("id",employeeID );
        final Employee e = new Employee();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("aaa", document.getData().toString());
                            e.setEmployeeID(document.getLong("id").intValue());
                            e.setFirstName(document.getString("firstName"));
                            e.setLastName(document.getString("lastName"));

                            callback.onCallback(e);
                        }
                    }else
                    {
                        callback.onCallback(e);
                    }
                } else {
                    Log.d("iii", "onComplete: failed to search for employee ");

                }
            }
        });


    }

    public static void getAllStorages(final CallbackGetStorages callback){

        db.collection("storageSpaces").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<StorageSpace> storageSpaces = new ArrayList<>();
                    if(task.getResult()!= null) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            StorageSpace s = document.toObject(StorageSpace.class);
                            storageSpaces.add(s);
                        }
                    }else{
                        storageSpaces.add(null);

                    }
                    callback.getAllStorages(storageSpaces);
                }else{
                    Log.e("firestore", "Error while getting storages");
                }
            }
        });

    }

    public static void addStorageSpace(String storageID, final CallbackAddStorage callbackAddStorage){

      CollectionReference storageRef = db.collection("storageSpaces");

        storageRef.document(storageID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                       callbackAddStorage.onCallbackAddStorage(null);
                    }else{

                       StorageSpace space = new StorageSpace(storageID,null);
                        storageRef.document(storageID).set(space).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    callbackAddStorage.onCallbackAddStorage(space);

                                }else{
                                    Log.e("firestore", "error while adding space");
                                }
                            }
                        });
                    }
                }else{
                    Log.e("firestore", "error while searching for ID");
                }
            }
        });
    }



}
