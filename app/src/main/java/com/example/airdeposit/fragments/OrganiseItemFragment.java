package com.example.airdeposit.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.mbms.MbmsErrors;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.airdeposit.Firebase;
import com.example.airdeposit.Product;
import com.example.airdeposit.R;
import com.example.airdeposit.StorageAdapter;
import com.example.airdeposit.StorageSpace;
import com.example.airdeposit.callbacks.CallbackGetStorages;
import com.example.airdeposit.callbacks.CallbackProductAddedToStorage;
import com.example.airdeposit.databinding.FragmentOrganiseItemBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrganiseItemFragment extends Fragment {

    Button btnScan;
    Product product;
    private RecyclerView recyclerView;
    private StorageAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<StorageSpace> list;
    private FragmentOrganiseItemBinding binder;
    View view;
    public OrganiseItemFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binder = FragmentOrganiseItemBinding.inflate(inflater, container, false);

       if(getArguments() != null) {
           product = getArguments().getParcelable("product");
       }

       setUpRecyclerView();
        setUpViews(product);

        view = binder.getRoot();
        return view;
    }

    private void setUpViews(Product product) {
        if(product != null){

            String productName = "Name: " +product.getNameOfProduct();
            String productSize = "Size: " + product.getSize().toUpperCase();
            binder.tvOrganiseItemName.setText(productName);
            binder.tvSize.setText(productSize);

            updateDepositedInField(product);
        }
    }

    private void updateDepositedInField(Product product){
        binder.tvDepositedIn.setText(R.string.deposited_in);
        String depositedIn = binder.tvDepositedIn.getText().toString();
        if(product.getListOfPlacesDeposited() == null) {

            binder.tvDepositedIn.setText(depositedIn + " - ");
        }else{
            StringBuilder listOfDeposited = new StringBuilder();
            listOfDeposited.append(depositedIn);
            for(String storage : product.getListOfPlacesDeposited().keySet()){
                listOfDeposited.append("  ").append(storage);
            }
            String result = listOfDeposited.toString() ;
            binder.tvDepositedIn.setText(result);
        }
    }



    private void setUpRecyclerView() {
        FirestoreRecyclerOptions<StorageSpace> options = new FirestoreRecyclerOptions.Builder<StorageSpace>()
                .setQuery(Firebase.queryForStoragesRecyclerView(),StorageSpace.class)
                .build();
        adapter = new StorageAdapter(options,false);
        recyclerView = binder.rvAddProductsToStorage;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());


        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new StorageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                createAlertDialogForAddingToStorage(adapter.getItem(position),position);
            }

            @Override
            public void onDeleteItemClick(int position) {

            }
        });
    }

    //TODO Remove product from storage space when removing storage space;
    private void createAlertDialogForAddingToStorage(StorageSpace storage, int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add " + product.getNameOfProduct() + " to " +storage.getStorageID() );
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {

            assignProductToStorage(storage);

        });
        builder.setNegativeButton("No", ((dialogInterface, i) -> {

        }));
        builder.create().show();
    }

    private void assignProductToStorage(StorageSpace storage) {
        try {
            storage.storeProduct(product);
            product.addProductToStorage(storage);
            Firebase.addProductToStorage(storage, product, message -> Snackbar.make(getView(), message, BaseTransientBottomBar.LENGTH_SHORT).show());
            updateDepositedInField(product);
        }catch (Exception e) {
            Snackbar.make(getView(),e.getMessage(),BaseTransientBottomBar.LENGTH_SHORT).show();
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
