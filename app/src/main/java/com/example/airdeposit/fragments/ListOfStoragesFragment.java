package com.example.airdeposit.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.airdeposit.Firebase;
import com.example.airdeposit.R;
import com.example.airdeposit.adapters.StorageAdapter;
import com.example.airdeposit.StorageSpace;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ListOfStoragesFragment extends Fragment {
    private  RecyclerView recyclerView;
    private StorageAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button btnAddStorage;
    EditText input;

    private ArrayList<StorageSpace> list;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         view =  inflater.inflate(R.layout.fragment_list_of_storages, container, false);
         btnAddStorage = view.findViewById(R.id.btnAddStorage);
        getActivity().findViewById(R.id.custom_toolbar).findViewById(R.id.inputProductId).setVisibility(View.GONE);
        getActivity().findViewById(R.id.custom_toolbar).findViewById(R.id.imgSearch).setVisibility(View.GONE);
         btnAddStorage.setOnClickListener(v1 -> {

           Navigation.findNavController(getView()).navigate(R.id.action_addStorageFragment_to_addStorageFragment2);

       });

        setUpRecyclerView();

        return view;
    }

    private void removeStorage(StorageSpace storageSpace, int position){

        if(storageSpace.getStoredProducts().size() > 0 ){
            Snackbar.make(getView(),"Can't delete storage space while having products stored in it", BaseTransientBottomBar.LENGTH_SHORT).show();
        }else{

           Firebase.deleteStorageSpace(storageSpace);
            Snackbar.make(getView(), R.string.storage_deleted, BaseTransientBottomBar.LENGTH_SHORT).show();
        }


    }

    private void createAlertForRemovingStorage(StorageSpace storage, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are you sure you want to delete " + storage.getStorageID());
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                     removeStorage(storage,position);
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
        FirestoreRecyclerOptions<StorageSpace> options = new FirestoreRecyclerOptions.Builder<StorageSpace>()
                .setQuery(Firebase.queryForStoragesRecyclerView(),StorageSpace.class)
                .build();
        adapter = new StorageAdapter(options,true);
        recyclerView = view.findViewById(R.id.rvOfStorage);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

       adapter.setOnItemClickListener(new StorageAdapter.OnItemClickListener() {
           @Override
           public void onItemClick(int position, TextView boxImg) {
                    Bundle b = new Bundle();
                    b.putParcelable("storage",adapter.getItem(position));


                    FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                            .addSharedElement(boxImg,adapter.getItem(position).getStorageID())
                            .build();

                    Navigation.findNavController(getView()).navigate(R.id.action_listOfStoragesFragment_to_storageDetailsFragment,b,null,extras);
           }

           @Override
           public void onDeleteItemClick(int position) {
               createAlertForRemovingStorage(adapter.getItem(position), position);

           }
       });

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
