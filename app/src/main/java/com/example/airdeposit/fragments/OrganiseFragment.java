package com.example.airdeposit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.airdeposit.R;

public class OrganiseFragment extends Fragment {

    Button btnScan;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organise_item, container, false);

        btnScan = view.findViewById(R.id.btn_scan_organise);
        btnScan.setOnClickListener(v ->{
            Bundle b = new Bundle();
            b.putString("from","organise");
            Navigation.findNavController(getView()).navigate(R.id.cameraScanFragment,b);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


}
