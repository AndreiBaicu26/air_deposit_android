package com.example.airdeposit;

import android.system.ErrnoException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.security.auth.callback.Callback;

public class Firebase  {

    static FirebaseFirestore db = FirebaseFirestore.getInstance();


    public static void getProduct(String productBarCode, final CallBackProduct callBackProduct) {


        db.collection("products").document(productBarCode).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                            DocumentSnapshot data = task.getResult();

                            Product product = new Product(data.getString("documentId"),data.getString("name"),
                                   Integer.valueOf(data.getString("boh")),
                                    Integer.valueOf(data.getString("foh")),
                                    Integer.valueOf(data.getString("noOfPlayers")),
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





}
