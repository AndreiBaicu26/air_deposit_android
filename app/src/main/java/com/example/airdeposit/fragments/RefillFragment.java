package com.example.airdeposit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.airdeposit.Firebase;
import com.example.airdeposit.R;
import com.example.airdeposit.Sale;
import com.example.airdeposit.StorageSpace;
import com.example.airdeposit.adapters.SaleAdapter;
import com.example.airdeposit.adapters.StorageAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RefillFragment extends Fragment {

    private RecyclerView recyclerView;
    private SaleAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_refill, container, false);

        setUpRecyclerView();
        return view;
    }

    private void setUpRecyclerView() {
        FirestoreRecyclerOptions<Sale> options = new FirestoreRecyclerOptions.Builder<Sale>()
                .setQuery(Firebase.queryForRefill(),Sale.class)
                .build();
        adapter = new SaleAdapter(options);
        recyclerView = view.findViewById(R.id.rvRefill);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());


        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
