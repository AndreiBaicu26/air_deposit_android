package com.example.airdeposit.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.airdeposit.Product;
import com.example.airdeposit.R;
import com.example.airdeposit.Sale;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class SaleAdapter extends FirestoreRecyclerAdapter<Sale, SaleAdapter.SaleViewHolder> {

    public SaleAdapter(@NonNull FirestoreRecyclerOptions options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull SaleViewHolder holder, int position, @NonNull Sale model) {

        model.getProductRef().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Product product = task.getResult().toObject(Product.class);
                StringBuilder sb = new StringBuilder();
                Log.d("keys",product.getPlacesDeposited().toString());
                for(String key: product.getPlacesDeposited().keySet()){
                    Log.d("keys",key);
                    sb.append(key).append(" ");
                }
                String placesDeposited = "Storages: "  + sb.toString();
                holder.placesDeposited.setText(placesDeposited);
                holder.nameOfProduct.setText(product.getName());
                Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                  calendar.setTime(model.getDateCreated());   // assigns calendar to given date
               String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
               String minute = String.valueOf(calendar.get(Calendar.MINUTE));
               if(minute.length() == 1){
                   holder.time.setText(hour + ":0" + minute);
               }else{
                   holder.time.setText(hour + ":"+minute);
               }

            }
        });




    }

    @NonNull
    @Override
    public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_refill_item, parent, false);
        return new SaleViewHolder(view);
    }


    class SaleViewHolder extends RecyclerView.ViewHolder{

        TextView nameOfProduct;
        TextView placesDeposited;
        TextView time;
        public SaleViewHolder(@NonNull View itemView) {
            super(itemView);
            nameOfProduct = itemView.findViewById(R.id.tvNameOfProduct);
            placesDeposited= itemView.findViewById(R.id.tvPlacesDeposited);
            time = itemView.findViewById(R.id.tvTime);
        }
    }
}
