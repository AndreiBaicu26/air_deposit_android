package com.example.airdeposit.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.airdeposit.Product;
import com.example.airdeposit.R;
import com.example.airdeposit.StorageSpace;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class ProductAdapter extends FirestoreRecyclerAdapter<Product, ProductAdapter.ProductViewHolder> {
    StorageSpace currentStorage;

    public ProductAdapter(@NonNull FirestoreRecyclerOptions<Product> options, StorageSpace s) {
        super(options);
        this.currentStorage = s;
    }



    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item,parent,false);

        return new ProductViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {
        Integer productQuantity = model.getPlacesDeposited().get(currentStorage.getStorageID());
        String productName = model.getName();
        String name = "Product name: " + productName;
        String quantity = "Quantity: " + productQuantity;
        holder.productName.setText(name);
        holder.productQuantity.setText(quantity);
    }



    class ProductViewHolder extends RecyclerView.ViewHolder{

        TextView productName;
        TextView productQuantity;

        public ProductViewHolder(@NonNull View itemView) {

            super(itemView);
            productName = itemView.findViewById(R.id.tvNameOfProduct);
            productQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }

}
