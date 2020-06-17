package com.example.airdeposit.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.example.airdeposit.Firebase;
import com.example.airdeposit.Product;
import com.example.airdeposit.R;
import com.example.airdeposit.adapters.ProductAdapter;
import com.example.airdeposit.StorageSpace;
import com.example.airdeposit.callbacks.CallbackGetAllStorageSpaces;
import com.example.airdeposit.callbacks.CallbackGetStorage;
import com.example.airdeposit.databinding.FragmentStorageDetailsBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class StorageDetailsFragment extends Fragment {

    FragmentStorageDetailsBinding binder;
    StorageSpace currentStorage;

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    View view;
    StorageSpace selectedStorage;
    public StorageDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        getActivity().findViewById(R.id.custom_toolbar).findViewById(R.id.inputProductId).setVisibility(View.GONE);
        getActivity().findViewById(R.id.custom_toolbar).findViewById(R.id.imgSearch).setVisibility(View.GONE);
        binder = FragmentStorageDetailsBinding.inflate(inflater, container, false);
        if(getArguments()!=null){
            currentStorage = getArguments().getParcelable("storage");
        }
        setRecyclerView();
        setViews();
        setSwipeToDelete();


        binder.button.setOnClickListener(v->{
            createAlertDialogEmptyStorage();
        });

        view = binder.getRoot();
        return view;
    }

    private void setSwipeToDelete() {

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT| ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            //ColorDrawable background = new ColorDrawable(Color.parseColor("#65061e"));
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);



                RectF background;
                Paint p = new Paint();
                p.setColor(Color.parseColor("#65061e"));
                if(actionState==ItemTouchHelper.LEFT) {
                    background= new RectF(0, viewHolder.itemView.getTop(), (int) ((int)viewHolder.itemView.getLeft() + dX), viewHolder.itemView.getBottom());
                }else{
                    background= new RectF(viewHolder.itemView.getRight(), viewHolder.itemView.getTop(), (int) ((int)viewHolder.itemView.getLeft() + dY), viewHolder.itemView.getBottom());
                }

                if(isCurrentlyActive){

                    c.drawRoundRect(background,20,20,p);
                }else{

                }
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                Product p = adapter.getItem(viewHolder.getAdapterPosition());
                createAlertDialogRemoveProducts(p);


            }
        }).attachToRecyclerView(recyclerView);
    }

    private void createAlertDialogRemoveProducts( Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Insert number of products to be removed: ");

        final EditText input = new EditText(getContext());

        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                getQuantityInserted(input,product);
                dialog.cancel();
            }

        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                recyclerView.setAdapter(adapter);
            }
        });

        builder.create().show();
    }

    private void getQuantityInserted(EditText input,Product product) {
        if(input.getText().toString().length()>0 && !input.getText().toString().contains(" ")){

            int totalQuantity = currentStorage.getStoredProducts().get(product.getName());
            final int  quantityOfProductsToBeRemoved = Integer.parseInt(input.getText().toString());
            if(quantityOfProductsToBeRemoved > totalQuantity) {
                Snackbar.make(getView(), "You do not have this many products in this storage", BaseTransientBottomBar.LENGTH_SHORT).show();
                recyclerView.setAdapter(adapter);
            }else if(quantityOfProductsToBeRemoved <=0){
                Snackbar.make(getView(), "Please insert a positive number", BaseTransientBottomBar.LENGTH_SHORT).show();
                recyclerView.setAdapter(adapter);

            }else {
                whereToPlaceProductAlertDialog(product, input, quantityOfProductsToBeRemoved, totalQuantity);
            }
        }
    }

    private void setStorage(StorageSpace selected) {
        this.selectedStorage = selected;
    }

    private void whereToPlaceProductAlertDialog(Product product, EditText input, int quantityOfProductsToBeRemoved, int totalQuantity) {

        Firebase.getAllStorages(new CallbackGetAllStorageSpaces() {
            @Override
            public void callbackGetAllStorageSpaces(ArrayList<StorageSpace> list) {
                List<String> keys = new ArrayList<>();
                for(StorageSpace s:list){
                    keys.add(s.getStorageID());
                }
                keys.add("Store");

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        getContext(),
                        android.R.layout.select_dialog_singlechoice,
                        keys);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Where do you want to place this item");
               final StorageSpace s;
                builder.setSingleChoiceItems(arrayAdapter, -1, (dialogInterface, i) ->{
                    selectedStorage = null;
                    if(!arrayAdapter.getItem(i).equals("Store")){
                        selectedStorage = list.get(i);
                    }


                });

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        if (selectedStorage == null) {
                            startRemoving(quantityOfProductsToBeRemoved, totalQuantity, product, true);
                            product.setFoh(product.getFoh() + quantityOfProductsToBeRemoved);
                        }else{
                            startRemoving(quantityOfProductsToBeRemoved, totalQuantity, product, false);
                            storeItems(selectedStorage,product,dialogInterface,quantityOfProductsToBeRemoved);

                        }


                    }
                });
                builder.create().show();
            }
        });

    }

    private void addToProcessing(Product product, int quantityOfProductsToBeRemoved) {
        Firebase.getStorage("Processing", new CallbackGetStorage() {
            @Override
            public void onCallbackGetStorage(StorageSpace storage) {
                try {
                    for (int i = 0; i < quantityOfProductsToBeRemoved ; i++) {
                        product.addProductToStorage(storage);
                    }
                } catch (Exception e) {
                    Snackbar.make(getView(),e.getMessage(),BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startRemoving(int quantityOfProductsToBeRemoved, int totalQuantity, Product product, boolean toStore){


            int diff = totalQuantity-quantityOfProductsToBeRemoved;
            if(diff>0){
                product.getPlacesDeposited().put(currentStorage.getStorageID(),diff);
                currentStorage.getStoredProducts().put(product.getName(),diff);

                for (int i = 0; i < quantityOfProductsToBeRemoved ; i++) {
                    try {
                        if(!toStore) {
                            product.addToProcessing();
                        }
                        currentStorage.removeProductFromStorage(product);

                    } catch (Exception e) {
                        Snackbar.make(getView(),e.getMessage(),BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                }
            }else{
                product.getPlacesDeposited().remove(currentStorage.getStorageID());
                currentStorage.getStoredProducts().remove(product.getName());

                for (int i = 0; i < totalQuantity ; i++) {
                    try {
                        if(!toStore){
                            product.addToProcessing();
                        }
                        currentStorage.removeProductFromStorage(product);

                    } catch (Exception e) {
                        Snackbar.make(getView(),e.getMessage(),BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                }
            }


            Firebase.updateAllSales(product);
            Firebase.removeProductFromStorage(product);
            Firebase.updateStorage(currentStorage);
            setViews();
    }


    private void storeItems(StorageSpace clonedStorageSpace, Product clonedProduct, DialogInterface dialogInterface, int quantityOfProductsToBeRemoved) {

        if(quantityOfProductsToBeRemoved > clonedProduct.getBoh()){
            Snackbar.make(getView(), "You do not have this many items", BaseTransientBottomBar.LENGTH_SHORT).show();
            if(dialogInterface != null){
                dialogInterface.dismiss();
            }

        }else{
            try {
                for(int y = 0; y < quantityOfProductsToBeRemoved; y++){

                    clonedStorageSpace.storeProduct(clonedProduct);
                    if(!clonedStorageSpace.getStorageID().equals("Processing")){
                        clonedProduct.addProductToStorage(clonedStorageSpace);
                    }

                }
                Firebase.addProductToStorage(clonedStorageSpace, clonedProduct, message -> Snackbar.make(getView(), message, BaseTransientBottomBar.LENGTH_SHORT).show());

            } catch (Exception e) {
                Snackbar.make(getView(), e.getMessage(),BaseTransientBottomBar.LENGTH_SHORT).show();

            }
        }
    }

    private void createAlertDialogEmptyStorage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Empty this storage?");
        builder.setPositiveButton("Yes",(v,l)->{
                emptyStorage();
        });
        builder.setNegativeButton("No",(v2,l2)->{});

        builder.create().show();
    }

    private void emptyStorage() {

        int numberOfProductsInStorage = adapter.getItemCount();
        Firebase.getStorage("Processing", new CallbackGetStorage() {
            @Override
            public void onCallbackGetStorage(StorageSpace storage) {

                try {
                    for (int i = 0; i < numberOfProductsInStorage; i++) {

                        Product product = adapter.getItem(i);
                        int totalQuantity = currentStorage.getStoredProducts().get(product.getName());
                        moveAllToProcessing(storage, product,totalQuantity );

                    }
                    currentStorage.emptyStorage();
                    Firebase.emptyStorage(currentStorage, message -> Snackbar.make(getView(),message, BaseTransientBottomBar.LENGTH_SHORT).show());
                    Firebase.updateStorage(storage);
                    setViews();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private void moveAllToProcessing(StorageSpace processing, Product product, int totalQuantity){

        product.getPlacesDeposited().remove(currentStorage.getStorageID());
        currentStorage.getStoredProducts().remove(product.getName());

        for (int i = 0; i < totalQuantity ; i++) {
            try {

                product.addToProcessing();
                currentStorage.removeProductFromStorage(product);
                processing.storeProduct(product);

            } catch (Exception e) {
                Snackbar.make(getView(),e.getMessage(),BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        }
        Firebase.removeProductFromStorage(product);
    }

    private void setRecyclerView() {
        Query q = Firebase.queryForProductInStorageRecyclerView(currentStorage.getStorageID());
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(q,Product.class)
                .build();
        adapter = new ProductAdapter(options,currentStorage);
        recyclerView = binder.rvItemsInStorage;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());


        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setViews() {
        binder.tvIdStorage.setText(currentStorage.getStorageID());
        String filled = "Filled: " + currentStorage.getFilledPercentage() + " %";
        binder.tvFilledStorage.setText(filled);
        if(this.currentStorage.getStorageID().equals("Processing")){
            binder.button.setVisibility(View.GONE);
        }else{
            binder.button.setVisibility(View.VISIBLE);
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
