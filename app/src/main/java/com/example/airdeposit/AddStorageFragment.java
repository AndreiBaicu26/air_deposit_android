package com.example.airdeposit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddStorageFragment extends Fragment {

    public AddStorageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_add_storage, container, false);

        v.findViewById(R.id.btnAddNewStorage).setOnClickListener(v1 -> {

            //TODO: Firebase Add new storage. Search for id if exists. Return new storage. Send new storage back to storage List.

            Bundle bundle = new Bundle();
           // bundle.putParcelable("product",product);

            Navigation.findNavController(getView()).navigate(R.id.action_addStorageFragment2_to_addStorageFragment);
        });

        return v;
    }
}
