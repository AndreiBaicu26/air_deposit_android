package com.example.airdeposit.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.airdeposit.Firebase;
import com.example.airdeposit.Product;
import com.example.airdeposit.R;
import com.example.airdeposit.adapters.OrganiseItemStorageAdapter;
import com.example.airdeposit.adapters.StorageAdapter;
import com.example.airdeposit.StorageSpace;
import com.example.airdeposit.callbacks.CallbackAddStorage;
import com.example.airdeposit.callbacks.CallbackGetStorage;
import com.example.airdeposit.databinding.FragmentOrganiseItemBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


public class OrganiseItemFragment extends Fragment {

    Button btnScan;
    Product product;
    EditText input;
    private RecyclerView recyclerView;
    private OrganiseItemStorageAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<StorageSpace> list;
    private FragmentOrganiseItemBinding binder;
    View view;
    StorageSpace storageSpace ;
    //int quantityToStore = 0;

    public OrganiseItemFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().findViewById(R.id.custom_toolbar).findViewById(R.id.inputProductId).setVisibility(View.GONE);
        getActivity().findViewById(R.id.custom_toolbar).findViewById(R.id.imgSearch).setVisibility(View.GONE);
        binder = FragmentOrganiseItemBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            product = getArguments().getParcelable("product");

        }

        setUpRecyclerView();
        setUpViews(product);

        view = binder.getRoot();


        return view;
    }

    private void setUpViews(Product product) {
        if (product != null) {

            String productName = "Name: " + product.getName();
            String productSize = "Size: " + product.getSize().toUpperCase();
            String itemsToBeStored = "Items to be stored: " + this.product.getUnStoredProducts();
            binder.tvOrganiseItemName.setText(productName);
            binder.tvSize.setText(productSize);
            binder.tvItemsToBeStored.setText(itemsToBeStored);


            updateDepositedInField(product);
        }
    }

    private void updateDepositedInField(Product product) {
        binder.tvDepositedIn.setText(R.string.deposited_in);
        String depositedIn = binder.tvDepositedIn.getText().toString();
        String itemsToBeStored = "Items to be stored: " + this.product.getUnStoredProducts();
        binder.tvItemsToBeStored.setText(itemsToBeStored);
        if (product.getPlacesDeposited() == null) {

            binder.tvDepositedIn.setText(depositedIn + " - ");
        } else {
            StringBuilder listOfDeposited = new StringBuilder();

            listOfDeposited.append(depositedIn);
            for (String storage : product.getPlacesDeposited().keySet()) {
                if(storage.equals("Processing")){
                    if(product.getPlacesDeposited().get("Processing") !=0){
                        listOfDeposited.append("  ").append(storage);
                    }
                }else{
                    listOfDeposited.append("  ").append(storage);
                }


            }
            String result = listOfDeposited.toString();
            binder.tvDepositedIn.setText(result);
        }
    }


    private void setUpRecyclerView() {
        FirestoreRecyclerOptions<StorageSpace> options = new FirestoreRecyclerOptions.Builder<StorageSpace>()
                .setQuery(Firebase.queryForStoragesRecyclerView(), StorageSpace.class)
                .build();
        adapter = new OrganiseItemStorageAdapter(options, false);
        recyclerView = binder.rvAddProductsToStorage;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());


        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new OrganiseItemStorageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, TextView boxImg) {
                createAlertDialogForAddingToStorage(adapter.getItem(position), position);
            }

            @Override
            public void onDeleteItemClick(int position) {

            }
        });
    }


    private void createAlertDialogForAddingToStorage(StorageSpace storage, int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add " + product.getName() + " to " + storage.getStorageID());
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {

            try {
                assignProductToStorage(storage);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

        });
        builder.setNegativeButton("No", ((dialogInterface, i) -> {

        }));
        builder.create().show();
    }

    private void assignProductToStorage(StorageSpace storage) throws CloneNotSupportedException {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("How many products to store?");
        final EditText input = new EditText(getContext());

        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        StorageSpace clonedStorageSpace = (StorageSpace) storage.clone();
        Product clonedProduct = (Product) product.clone();

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(input.getText().toString().length() > 0 && !input.getText().toString().contains(" ")) {

                        storeItems(clonedStorageSpace, clonedProduct, dialogInterface,input);


                }
            }

        });


        product = clonedProduct;
        builder.create().show();

//
    }

    private void storeItems(StorageSpace clonedStorageSpace, Product clonedProduct, DialogInterface dialogInterface, EditText input) {
        int quantity = Integer.parseInt(input.getText().toString());
        if(quantity > clonedProduct.getUnStoredProducts()){
            Snackbar.make(getView(), "You do not have this many items", BaseTransientBottomBar.LENGTH_SHORT).show();
            dialogInterface.dismiss();
        }else{
            try {

                for(int y = 0; y < quantity; y++){

                    clonedStorageSpace.storeProduct(clonedProduct);
                    clonedProduct.addProductToStorage(clonedStorageSpace);
                    clonedProduct.getPlacesDeposited().put("Processing", clonedProduct.getPlacesDeposited().get("Processing") -1 );

                }
                Firebase.getStorage("Processing", new CallbackGetStorage() {
                    @Override
                    public void onCallbackGetStorage(StorageSpace storage) {
                        try {
                            for(int y = 0; y < quantity; y++){
                                 storage.removeProductFromProcessing(clonedProduct);
                            }
                            Firebase.updateStorage(storage);
                            Firebase.removeFromProcessing(clonedProduct.getName(),clonedProduct.getPlacesDeposited().get("Processing"));
                            Firebase.updateAllSales(clonedProduct);
                            Firebase.addProductToStorage(clonedStorageSpace, clonedProduct, message -> Snackbar.make(getView(), message, BaseTransientBottomBar.LENGTH_SHORT).show());
                            updateDepositedInField(clonedProduct);

                        } catch (Exception e) {
                            Snackbar.make(getView(), e.getMessage(),BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (Exception e) {
                Snackbar.make(getView(), e.getMessage(),BaseTransientBottomBar.LENGTH_SHORT).show();

            }
        }
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
}
