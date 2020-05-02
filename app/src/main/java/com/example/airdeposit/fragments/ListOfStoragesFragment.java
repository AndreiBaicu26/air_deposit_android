package com.example.airdeposit.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.airdeposit.R;
import com.example.airdeposit.StorageAdapter;
import com.example.airdeposit.StorageSpace;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.api.LogDescriptor;

import java.util.ArrayList;

public class ListOfStoragesFragment extends Fragment {
    private  RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    Button btnAddStorage;
    EditText input;
    String selectedId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v  =  inflater.inflate(R.layout.fragment_list_of_storages, container, false);
        btnAddStorage = v.findViewById(R.id.btnAddStorage);

       btnAddStorage.setOnClickListener(v1 -> {

           Navigation.findNavController(getView()).navigate(R.id.action_addStorageFragment_to_addStorageFragment2);

       });

        ArrayList<StorageSpace> list = new ArrayList<>();
        list.add(new StorageSpace("A20",null));
        recyclerView = v.findViewById(R.id.rvOfStorage);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new StorageAdapter(list);


        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return v;
    }

    public LinearLayout createEditText(){
        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(50, 5, 50, 5);

        input = new EditText(getContext());
        input.setHint("ex: A40");
        input.setLayoutParams(lp);

        container.addView(input);
        return container;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }
}
