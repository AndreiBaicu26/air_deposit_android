package com.example.airdeposit.fragments;

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
import com.example.airdeposit.databinding.FragmentOrganiseItemBinding;

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

       if(product != null){
           String depositedIn = binder.tvDepositedIn.getText().toString();
           binder.tvOrganiseItemName.setText(product.getNameOfProduct());
           binder.tvSize.setText(product.getSize());
           if(product.getListOfPlacesDeposited().isEmpty()) {
               binder.tvDepositedIn.setText(depositedIn + " - ");
           }else{
               StringBuilder listOfDeposited = new StringBuilder();
               listOfDeposited.append(depositedIn);
               for(String storage : product.getListOfPlacesDeposited().keySet()){
                   listOfDeposited.append(" "+ storage );
               }
               String result = listOfDeposited.toString() ;
                binder.tvDepositedIn.setText(result);
           }
       }

        createListToShow();


        view = binder.getRoot();
        return view;
    }

    private void setUpRecyclerView() {
        recyclerView = binder.rvAddProductsToStorage;
        recyclerView.findViewById(R.id.imgRemove).setVisibility(View.INVISIBLE);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new StorageAdapter(list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new StorageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onDeleteItemClick(int position) {

            }
        });
    }

    private void createListToShow(){


        Firebase.getAllStorages(new CallbackGetStorages() {
            @Override
            public void getAllStorages(List<StorageSpace> storageSpaces) {
                list = new ArrayList<StorageSpace>(storageSpaces);
                setUpRecyclerView();
            }

        });
    }
}
