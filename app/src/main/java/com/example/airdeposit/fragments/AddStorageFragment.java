package com.example.airdeposit.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.airdeposit.Firebase;
import com.example.airdeposit.R;
import com.example.airdeposit.StorageSpace;
import com.example.airdeposit.callbacks.CallbackAddStorage;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddStorageFragment extends Fragment {

    public AddStorageFragment() {
        // Required empty public constructor
    }

    EditText storageIDInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_add_storage, container, false);
        storageIDInput = v.findViewById(R.id.storageIdInput);

        v.findViewById(R.id.btnAddNewStorage).setOnClickListener(v1 -> {

            String storageID = storageIDInput.getText().toString();

            if(storageID.length() == 0 || storageID.contains(" ")){
                Toast.makeText(getContext(),"Please insert a new ID", Toast.LENGTH_SHORT).show();
            }else{

                Firebase.addStorageSpace(storageID, new CallbackAddStorage() {
                    @Override
                    public void onCallbackAddStorage(StorageSpace storageSpace) {
                        if(storageSpace == null){

                            Toast.makeText(getContext(),"Storage with this ID already exists", Toast.LENGTH_SHORT).show();
                        }else{
                            Snackbar.make(requireView(),"Storage added", BaseTransientBottomBar.LENGTH_SHORT).show();

                            Navigation.findNavController(getView()).navigate(R.id.action_addStorageFragment2_to_addStorageFragment);
                        }
                    }
                });

            }


        });

        return v;
    }


}
