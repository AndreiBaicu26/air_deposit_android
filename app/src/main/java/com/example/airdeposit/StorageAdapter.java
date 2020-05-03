package com.example.airdeposit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.internal.Storage;

import java.util.ArrayList;

public class StorageAdapter extends RecyclerView.Adapter <StorageAdapter.StorageViewHolder>{

    private ArrayList<StorageSpace> storageList;
    private  OnItemClickListener aListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDeleteItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        aListener = listener;
    }

    public StorageAdapter(ArrayList<StorageSpace> storageList){
        this.storageList = storageList;
    }

    @NonNull
    @Override
    public StorageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        StorageViewHolder storageViewHolder = new StorageViewHolder(view, aListener);
        return storageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StorageViewHolder holder, int position) {
        StorageSpace storage = this.storageList.get(position);
        holder.tvStorageId.setText(storage.getStorageID());
        holder.tvStorageFilled.setText("Filled: " + storage.getFilledPercentage()+"%");
    }

    @Override
    public int getItemCount() {
        return storageList.size();
    }

    public static class StorageViewHolder extends RecyclerView.ViewHolder{
        public TextView tvStorageId;
        public TextView tvStorageFilled;
        public ImageView deleteImage;

        public StorageViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            tvStorageId = itemView.findViewById(R.id.tvIdStorage);
            tvStorageFilled = itemView.findViewById(R.id.tvFilledStorage);
            deleteImage = itemView.findViewById(R.id.imgRemove);

            itemView.setOnClickListener(view -> {
                if(listener !=null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                    }
                }
            });

            deleteImage.setOnClickListener(view->{
                if(listener !=null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onDeleteItemClick(position);
                    }
                }
            });

        }
    }
}
