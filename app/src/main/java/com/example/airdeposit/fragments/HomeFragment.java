package com.example.airdeposit.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.airdeposit.Firebase;
import com.example.airdeposit.MainActivity;
import com.example.airdeposit.Product;
import com.example.airdeposit.R;
import com.example.airdeposit.callbacks.CallBackProduct;
import com.example.airdeposit.databinding.FragmentHomeBinding;

import static android.Manifest.permission.CAMERA;

public class HomeFragment extends Fragment {
    TextView input;
    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getActivity().findViewById(R.id.custom_toolbar).findViewById(R.id.inputProductId).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.custom_toolbar).findViewById(R.id.imgSearch).setVisibility(View.VISIBLE);;
        binding.btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("from", "home");
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_cameraScanFragment,b);
            }
        });

        input = getActivity().findViewById(R.id.inputProductId);
        ImageView i = getActivity().findViewById(R.id.custom_toolbar).findViewById(R.id.imgSearch);
        i.setOnClickListener(a->{imgPressSearchProduct(getView());});
        return view;
    }

    private void imgPressSearchProduct(View view) {
        if(input.getText().toString().length() > 0) {
            String productCode = input.getText().toString();
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                }});


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
                    Navigation.findNavController(getView()).navigate(R.id.action_homeFragment_to_productDetailsFragment,bundle);
                }
            });

        }
    }


    //
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
