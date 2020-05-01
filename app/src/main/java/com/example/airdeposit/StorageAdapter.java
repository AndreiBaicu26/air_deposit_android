package com.example.airdeposit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.internal.Storage;

import java.util.ArrayList;

public class StorageAdapter extends RecyclerView.Adapter <StorageAdapter.StorageViewHolder>{

    private ArrayList<StorageSpace> sorageList;

    public static class StorageViewHolder extends RecyclerView.ViewHolder{
        public TextView tvStorageId;
        public TextView tvStorageFilled;

        public StorageViewHolder(@NonNull View itemView) {
            super(itemView);

            tvStorageId = itemView.findViewById(R.id.tvIdStorage);
            tvStorageFilled = itemView.findViewById(R.id.tvFilledStorage);
        }
    }

    public StorageAdapter(ArrayList<StorageSpace> storageList){
        this.sorageList = storageList;
    }

    @NonNull
    @Override
    public StorageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        StorageViewHolder storageViewHolder = new StorageViewHolder(view);
        return storageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StorageViewHolder holder, int position) {
        StorageSpace storage = this.sorageList.get(position);
        holder.tvStorageId.setText(storage.getStorageID());
        holder.tvStorageFilled.setText("Filled: " + storage.getFilledPercentage()+"%");
    }



    @Override
    public int getItemCount() {
        return sorageList.size();
    }
}
