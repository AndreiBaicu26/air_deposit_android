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
import com.example.airdeposit.callbacks.CallBackProduct;
import com.example.airdeposit.fragments.RefillFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


import java.util.Calendar;
import java.util.GregorianCalendar;

public class SaleAdapter extends FirestoreRecyclerAdapter<Sale, SaleAdapter.SaleViewHolder> {


    private  CallBackProduct getProduct;
    public SaleAdapter(@NonNull FirestoreRecyclerOptions options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull SaleViewHolder holder, int position, @NonNull Sale model) {



                Product product = model.getProduct();
                StringBuilder sb = new StringBuilder();

                for (String key : product.getPlacesDeposited().keySet()) {

                    sb.append(key).append(" ");
                }
                String placesDeposited = "Storages: " + sb.toString();
                holder.placesDeposited.setText(placesDeposited);
                holder.nameOfProduct.setText(product.getName());
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(model.getDateCreated());
                String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
                String minute = String.valueOf(calendar.get(Calendar.MINUTE));
               // model.setProduct(product);
                if (minute.length() == 1) {
                    holder.time.setText(hour + ":0" + minute);
                } else {
                    holder.time.setText(hour + ":" + minute);
                }

    }



    public void updateSale(int position,Product p){
        getSnapshots().getSnapshot(position).getReference().update("refilled", true, "product",p);
    }


    @NonNull
    @Override
    public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_refill_item, parent, false);
        return new SaleViewHolder(view);
    }


    @Override
    public void onDataChanged() {
        super.onDataChanged();
        RefillFragment.checkIfRVEmpty(getItemCount());
    }

    class SaleViewHolder extends RecyclerView.ViewHolder {

        TextView nameOfProduct;
        TextView placesDeposited;
        TextView time;

        public SaleViewHolder(@NonNull View itemView) {
            super(itemView);
            nameOfProduct = itemView.findViewById(R.id.tvNameOfProduct);
            placesDeposited = itemView.findViewById(R.id.tvPlacesDeposited);
            time = itemView.findViewById(R.id.tvTime);
        }
    }
}
