package com.example.airdeposit.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.airdeposit.Sale;
import com.example.airdeposit.callbacks.CallBackProduct;
import com.example.airdeposit.Firebase;
import com.example.airdeposit.Product;
import com.example.airdeposit.R;
import com.google.zxing.Result;

import java.sql.Time;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class CameraScanFragment extends Fragment implements ZXingScannerView.ResultHandler {
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;
    String from;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        from = getArguments().getString("from");
        mScannerView = new ZXingScannerView(getActivity());

        if (checkPermission()) {

        } else {
            requestPermission();
        }
        return mScannerView;
    }


    @Override
    public void handleResult(Result rawResult) {
        final String detectedText = rawResult.getText();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setPositiveButton("Ok", (dialog, which) ->
                Navigation.findNavController(getView()).popBackStack());

        if(from.equals("organise")|| from.equals("home")) {
            Firebase.getProduct(detectedText, product -> {
                if (product == null) {
                    builder.setTitle("Could not detect product");
                    builder.setMessage("Speak to a manager");

                    AlertDialog alert = builder.create();
                    alert.show();

                } else {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("product", product);
                    if (from.equals("home")) {
                        Navigation.findNavController(getView()).navigate(R.id.action_cameraScanFragment_to_productDetailsFragment, bundle);
                    } else {
                        Navigation.findNavController(getView()).navigate(R.id.action_cameraScanFragment_to_organiseItemFragment, bundle);
                    }


                }
            });

        }else
            if(from.equals("sale")){
            Firebase.getProduct(detectedText, product -> {
                if (product == null) {
                    builder.setTitle("Could not detect product");
                    builder.setMessage("Speak to a manager");

                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    if(product.getFoh()==0){
                        //TODO alert no more products in front
                    }else{
                        Sale s = new Sale(product.getReference(),false);
                        Firebase.saleProduct(product);
                        Firebase.createSale(s);
                        builder.setTitle("Product sold");
                        builder.create().show();
                    }

                }
            });
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }


    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        Toast.makeText(getContext(), "Perm granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Perm denied", Toast.LENGTH_SHORT).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                displayAlertMessage("You need to allow acces for both permissions", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(new String[]{CAMERA},REQUEST_CAMERA);
                                    }
                                });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.M){
            if(checkPermission())
            {
                if(mScannerView ==null ){
                    mScannerView= new ZXingScannerView(getActivity());
                    getActivity().setContentView(mScannerView);
                }
                mScannerView.setResultHandler(this);
                mScannerView.startCamera();
            }else{
                requestPermission();
            }
        }
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("ok", listener)
                .setNegativeButton("Cancel", null)
                .create().show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }
}

