package com.example.airdeposit.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.airdeposit.Product;
import com.example.airdeposit.R;
import com.example.airdeposit.databinding.FragmentProductDetailsBinding;


public class ProductDetailsFragment extends Fragment {

    private FragmentProductDetailsBinding binding;
    Product product;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProductDetailsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getActivity().findViewById(R.id.custom_toolbar).findViewById(R.id.inputProductId).setVisibility(View.GONE);
        getActivity().findViewById(R.id.custom_toolbar).findViewById(R.id.imgSearch).setVisibility(View.GONE);
        if(getArguments() != null){
          product  = getArguments().getParcelable("product");
        }

        setTexts();



        return  view;
    }

    private void setTexts() {

        binding.tvNameOfProduct.setText(product.getName());
        binding.tvbarCode.setText("Code: " + product.getDocumentID());
        binding.tvBOH.setText("BOH: " + product.getBoh());
        binding.tvFOH.setText("FOH: " + product.getFoh());
        binding.tvSize.setText("Size: " + product.getSize());
        StringBuilder builder = new StringBuilder();
        builder.append("Places deposited: ");
        for(String key : product.getPlacesDeposited().keySet()){
            builder.append(key).append(" ");
        }

        String placesDep = builder.toString();
        binding.tvPlacesDep.setText(placesDep);
        binding.tvPrice.setText("Price: " + String.valueOf(product.getPrice()));
    }


}
