package com.example.airdeposit;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.security.auth.callback.Callback;

public class Firebase  {

    static FirebaseFirestore db = FirebaseFirestore.getInstance();


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
