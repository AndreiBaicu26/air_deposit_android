package com.example.airdeposit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.internal.Storage;

import java.util.ArrayList;

public class StorageAdapter extends FirestoreRecyclerAdapter<StorageSpace,StorageAdapter.StorageViewHolder> {

    private  OnItemClickListener aListener;
    private boolean isDeletable;


    public StorageAdapter(@NonNull FirestoreRecyclerOptions<StorageSpace> options, boolean isDeletable) {

        super(options);
        this.isDeletable = isDeletable;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDeleteItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        aListener = listener;
    }


    @NonNull
    @Override
    public StorageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new StorageViewHolder(view, aListener);
    }



    @Override
    protected void onBindViewHolder(@NonNull StorageViewHolder holder, int position, @NonNull StorageSpace model) {
        if(this.isDeletable){
            holder.deleteImage.setVisibility(View.VISIBLE);
        }else{
            holder.deleteImage.setVisibility(View.GONE);
        }
        holder.tvStorageId.setText(model.getStorageID());
        String storageFilled = "Filled: " + model.getFilledPercentage()+"%";
        holder.tvStorageFilled.setText(storageFilled);

    }

     class StorageViewHolder extends RecyclerView.ViewHolder{
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
