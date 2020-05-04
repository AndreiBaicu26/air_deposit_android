package com.example.airdeposit.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.airdeposit.Firebase;
import com.example.airdeposit.Product;
import com.example.airdeposit.ProductAdapter;
import com.example.airdeposit.R;
import com.example.airdeposit.StorageAdapter;
import com.example.airdeposit.StorageSpace;
import com.example.airdeposit.databinding.FragmentStorageDetailsBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.common.collect.HashMultimap;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class StorageDetailsFragment extends Fragment {

    FragmentStorageDetailsBinding binder;
    StorageSpace currentStorage;
    HashMap<String, Integer> list;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    View view;

    public StorageDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binder = FragmentStorageDetailsBinding.inflate(inflater, container, false);
        if(getArguments()!=null){
            currentStorage = getArguments().getParcelable("storage");
        }

        setViews();
        setRecyclerView();

        binder.button.setOnClickListener(v->{
            emptyStorage();
        });

        view = binder.getRoot();
        return view;
    }

    private void emptyStorage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Empty this storage?");
        builder.setPositiveButton("Yes",(v,l)->{

        });
        builder.setNegativeButton("No",(v2,l2)->{});

        builder.create().show();
    }


    private void setRecyclerView() {

        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(Firebase.queryForProductInStorageRecyclerView(currentStorage.getStorageID()),Product.class)
                .build();

        adapter = new ProductAdapter(options,currentStorage);
        recyclerView = binder.rvItemsInStorage;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setViews() {
        binder.tvIdStorage.setText(currentStorage.getStorageID());
        String filled = "Filled: " + currentStorage.getFilledPercentage() + " %";
        binder.tvFilledStorage.setText(filled);


    }


}
