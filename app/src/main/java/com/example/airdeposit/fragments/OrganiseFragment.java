package com.example.airdeposit.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.airdeposit.Firebase;
import com.example.airdeposit.Product;
import com.example.airdeposit.R;
import com.example.airdeposit.callbacks.CallBackProduct;

public class OrganiseFragment extends Fragment {
    TextView input;
    Button btnScan;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organise, container, false);
        getActivity().findViewById(R.id.custom_toolbar).findViewById(R.id.inputProductId).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.custom_toolbar).findViewById(R.id.imgSearch).setVisibility(View.VISIBLE);
        btnScan = view.findViewById(R.id.btn_scan_organise);
        btnScan.setOnClickListener(v ->{
            Bundle b = new Bundle();
            b.putString("from","organise");
            Navigation.findNavController(getView()).navigate(R.id.action_organiseFragment_to_cameraScanFragment,b);
        });

        input = getActivity().findViewById(R.id.inputProductId);
        ImageView i = getActivity().findViewById(R.id.custom_toolbar).findViewById(R.id.imgSearch);
        i.setOnClickListener(a->{imgPressSearchProduct(getView());});
        return view;
    }


    public void imgPressSearchProduct(final View view) {

        if(input.getText().toString().length() > 0) {
            String productCode = input.getText().toString();
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setPositiveButton("Ok", (dialog, which) -> {


            });


            Firebase.getProduct(productCode, product -> {
                if (product == null) {
                    builder.setTitle("Could not detect product");
                    builder.setMessage("Speak to a manager");

                    AlertDialog alert = builder.create();
                    alert.show();

                } else {
                    input.setText("");
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("product",product);
                    Navigation.findNavController(getView()).navigate(R.id.action_organiseFragment_to_organiseItemFragment,bundle);
                }
            });

        }


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


}
