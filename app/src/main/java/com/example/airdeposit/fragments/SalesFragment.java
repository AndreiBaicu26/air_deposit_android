package com.example.airdeposit.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.airdeposit.Firebase;
import com.example.airdeposit.R;
import com.example.airdeposit.Sale;

import java.sql.Time;
import java.util.Date;

public class SalesFragment extends Fragment {

    public SalesFragment() {

    }

    Button btnScan;
    EditText input;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sales, container, false);
        getActivity().findViewById(R.id.custom_toolbar).findViewById(R.id.inputProductId).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.custom_toolbar).findViewById(R.id.imgSearch).setVisibility(View.VISIBLE);
        btnScan = v.findViewById(R.id.btn_scan_sale);
        btnScan.setOnClickListener(view -> {
            Bundle b = new Bundle();
            b.putString("from", "sale");
            Navigation.findNavController(v).navigate(R.id.action_salesFragment_to_cameraScanFragment, b);
        });


        input = getActivity().findViewById(R.id.inputProductId);
        ImageView i = getActivity().findViewById(R.id.custom_toolbar).findViewById(R.id.imgSearch);
        i.setOnClickListener(a -> {
            imgPressSearchProduct(getView());
        });

        return v;
    }

    private void imgPressSearchProduct(View view) {
        if (input.getText().toString().length() > 0) {
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
                    if (product.getFoh() == 0) {
                        builder.setTitle("No more FOH products");
                        builder.create().show();

                    } else {
                        product.setFoh(product.getFoh() - 1);

                        Sale s = new Sale(product);

                        Firebase.saleProduct(product);
                        Firebase.createSale(s);
                        builder.setTitle("Product sold");
                        builder.create().show();
                    }

                }
            });
        }
    }
}
