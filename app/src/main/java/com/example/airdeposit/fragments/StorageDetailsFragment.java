package com.example.airdeposit.fragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.InputType;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.airdeposit.Firebase;
import com.example.airdeposit.Product;
import com.example.airdeposit.ProductAdapter;
import com.example.airdeposit.R;
import com.example.airdeposit.StorageAdapter;
import com.example.airdeposit.StorageSpace;
import com.example.airdeposit.callbacks.CallbackSuccessMessage;
import com.example.airdeposit.databinding.FragmentStorageDetailsBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.HashMultimap;
import com.google.firebase.firestore.Query;

import java.util.HashMap;


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

    public StorageDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
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
                startRemovingProduct(input,product);
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

    private void startRemovingProduct(EditText input,Product product) {
        if(input.getText().toString().length()>0 && !input.getText().toString().contains(" ")){
            int totalQuantity = currentStorage.getStoredProducts().get(product.getName());
            final int  quantityOfProductsToBeRemoved = Integer.parseInt(input.getText().toString());

            if(quantityOfProductsToBeRemoved > totalQuantity){
                Snackbar.make(getView(),"You do not have this many products in this storage",BaseTransientBottomBar.LENGTH_SHORT).show();
                recyclerView.setAdapter(adapter);
            }else{
                int diff = totalQuantity-quantityOfProductsToBeRemoved;
                if(diff>0){
                    product.getPlacesDeposited().put(currentStorage.getStorageID(),diff);
                    currentStorage.getStoredProducts().put(product.getName(),diff);

                    for (int i = 0; i < quantityOfProductsToBeRemoved ; i++) {
                        try {
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
                            currentStorage.removeProductFromStorage(product);
                        } catch (Exception e) {
                            Snackbar.make(getView(),e.getMessage(),BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                    }
                }


                Firebase.removeProductFromStorage(product);
                Firebase.updateStorage(currentStorage);
                setViews();
            }
        }
    }

    ;

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
        currentStorage.emptyStorage();
        int numberOfProductsInStorage = adapter.getItemCount();
        for (int i = 0; i < numberOfProductsInStorage; i++) {
            Product product =adapter.getItem(i);
            product.removeProductFromStorage(currentStorage.getStorageID());
            Firebase.removeProductFromStorage(product);
        }
        Firebase.emptyStorage(currentStorage, message -> Snackbar.make(getView(),message, BaseTransientBottomBar.LENGTH_SHORT).show());
        setViews();
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
