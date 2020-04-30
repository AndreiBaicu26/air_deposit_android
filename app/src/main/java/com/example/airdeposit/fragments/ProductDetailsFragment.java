package com.example.airdeposit.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.airdeposit.Product;
import com.example.airdeposit.R;
import com.example.airdeposit.databinding.FragmentHomeBinding;
import com.example.airdeposit.databinding.FragmentProductDetailsBinding;


public class ProductDetailsFragment extends Fragment {

    private FragmentProductDetailsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProductDetailsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Product product = getArguments().getParcelable("product");

        binding.tvNameOfProduct.setText(product.getNameOfProduct());
        binding.tvbarCode.setText("Code: " + product.getProductID());
        binding.tvBOH.setText("BOH: " + product.getBoh());
        binding.tvFOH.setText("FOH: " + product.getFoh());
        binding.tvSize.setText("Size: " + product.getSize());

        return  view;
    }




}
