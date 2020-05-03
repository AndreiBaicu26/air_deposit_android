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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.airdeposit.Firebase;
import com.example.airdeposit.R;
import com.example.airdeposit.StorageAdapter;
import com.example.airdeposit.StorageSpace;
import com.example.airdeposit.callbacks.CallbackGetStorages;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.LogDescriptor;

import java.util.ArrayList;
import java.util.List;

public class ListOfStoragesFragment extends Fragment {
    private  RecyclerView recyclerView;
    private StorageAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button btnAddStorage;
    EditText input;
    String selectedId;
    private ArrayList<StorageSpace> list;
    ImageView imageDelete;
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         view =  inflater.inflate(R.layout.fragment_list_of_storages, container, false);
         btnAddStorage = view.findViewById(R.id.btnAddStorage);
         btnAddStorage.setOnClickListener(v1 -> {

           Navigation.findNavController(getView()).navigate(R.id.action_addStorageFragment_to_addStorageFragment2);

       });

        createListToShow();

        return view;
    }

    private void removeStorage(int position){

        Firebase.deleteStorageSpace(list.get(position));
        list.remove(list.get(position));
        Snackbar.make(getView(), R.string.storage_deleted, BaseTransientBottomBar.LENGTH_SHORT).show();
        adapter.notifyItemRemoved(position);
    }

    private void createAlertForRemovingStorage(StorageSpace storage, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are you sure you want to delete " + storage.getStorageID());
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                     removeStorage(position);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.create().show();
    }

    private void setUpRecyclerView() {
        recyclerView = view.findViewById(R.id.rvOfStorage);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new StorageAdapter(list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

       adapter.setOnItemClickListener(new StorageAdapter.OnItemClickListener() {
           @Override
           public void onItemClick(int position) {

           }

           @Override
           public void onDeleteItemClick(int position) {
                createAlertForRemovingStorage(list.get(position), position);

           }
       });
    }

    private void createListToShow(){


        Firebase.getAllStorages(new CallbackGetStorages() {
            @Override
            public void getAllStorages(List<StorageSpace> storageSpaces) {
                list = new ArrayList<StorageSpace>(storageSpaces);
                setUpRecyclerView();
            }

        });
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
