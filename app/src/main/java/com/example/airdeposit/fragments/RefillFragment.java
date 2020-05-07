package com.example.airdeposit.fragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.telephony.RadioAccessSpecifier;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.airdeposit.Firebase;
import com.example.airdeposit.Product;
import com.example.airdeposit.R;
import com.example.airdeposit.Sale;
import com.example.airdeposit.StorageSpace;
import com.example.airdeposit.adapters.SaleAdapter;
import com.example.airdeposit.adapters.StorageAdapter;
import com.example.airdeposit.callbacks.CallbackGetStorage;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RefillFragment extends Fragment {

    private static TextView noRefill;

    private RecyclerView recyclerView;
    private SaleAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static View view;
    String selectedStorage;

    boolean saleIsRefilled;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_refill, container, false);
        noRefill = view.findViewById(R.id.tvNoRefill);
        setUpRecyclerView();
        setSwipeToRefill();

        return view;
    }

    public static void checkIfRVEmpty(int count) {
        noRefill = view.findViewById(R.id.tvNoRefill);
        if (count == 0) {
            noRefill.setVisibility(View.VISIBLE);
        } else {
            noRefill.setVisibility(View.INVISIBLE);
        }
    }


    private void setSwipeToRefill() {

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }


            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                RectF background;
                Paint p = new Paint();
                p.setColor(Color.parseColor("#1aa801"));
                if (actionState == ItemTouchHelper.LEFT) {
                    background = new RectF(0, viewHolder.itemView.getTop(), (int) ((int) viewHolder.itemView.getLeft() + dX), viewHolder.itemView.getBottom());
                } else {
                    background = new RectF(viewHolder.itemView.getRight(), viewHolder.itemView.getTop(), (int) ((int) viewHolder.itemView.getLeft() + dY), viewHolder.itemView.getBottom());
                }

                if (isCurrentlyActive) {
                    c.drawRoundRect(background, 20, 20, p);
                } else {

                }
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                Product p = adapter.getItem(viewHolder.getAdapterPosition()).getProduct();
                createAlertDialog(p, viewHolder.getAdapterPosition());

            }
        }).attachToRecyclerView(recyclerView);
    }

    private void createAlertDialog(Product product, int position) {
        this.selectedStorage = null;
        List<String> keys = new ArrayList<>(product.getPlacesDeposited().keySet());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.select_dialog_singlechoice,
                keys);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Where did you take this from?");

        builder.setSingleChoiceItems(arrayAdapter, -1, (dialogInterface, i) -> setStorage(arrayAdapter.getItem(i)));
        builder.setPositiveButton("Ok", (dialog, i) -> {
            if (this.selectedStorage == null && keys.size() > 0) {
                Snackbar.make(getView(), "No storage selected", BaseTransientBottomBar.LENGTH_SHORT).show();

            } else {
                if (this.selectedStorage == null) {
                    product.setBoh(product.getBoh() - 1);
                    product.setFoh(product.getFoh() + 1);
                    Firebase.removeProductFromStorage(product);
                    adapter.updateSale(position);
                } else
                    refillProcess(product, position);
                dialog.dismiss();
            }

        });
        builder.setNegativeButton("Cancel", (dialog, i) -> {
            this.selectedStorage = null;
            recyclerView.setAdapter(adapter);
            dialog.dismiss();
        });
        builder.create().show();

    }

    private void refillProcess(Product p, int position) {
        p.setBoh(p.getBoh() - 1);
        p.setFoh(p.getFoh() + 1);
        if (p.getPlacesDeposited().get(this.selectedStorage) == 1) {
            p.removeProductFromStorage(this.selectedStorage);
        } else {
            p.getPlacesDeposited().put(this.selectedStorage, p.getPlacesDeposited().get(this.selectedStorage) - 1);
        }
        Firebase.removeProductFromStorage(p);
        adapter.updateSale(position);
        Firebase.getStorage(this.selectedStorage, (StorageSpace storage) -> {
            removeFromStorage(storage, p);
        });

    }

    private void removeFromStorage(StorageSpace storage, Product product) {
        try {
            if (storage.getStoredProducts().get(product.getName()) == 1) {
                storage.getStoredProducts().remove(product.getName());
            } else {
                storage.removeProductFromStorage(product);
                storage.getStoredProducts().put(product.getName(), storage.getStoredProducts().get(product.getName()) - 1);
            }


            Firebase.updateStorage(storage);

        } catch (Exception e) {
            Snackbar.make(getView(), e.getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
        }
        recyclerView.setAdapter(adapter);
    }


    private void setStorage(String selected) {
        this.selectedStorage = selected;
    }

    private void setUpRecyclerView() {
        FirestoreRecyclerOptions<Sale> options = new FirestoreRecyclerOptions.Builder<Sale>()
                .setQuery(Firebase.queryForRefill(), Sale.class)
                .build();

        adapter = new SaleAdapter(options);
        recyclerView = view.findViewById(R.id.rvRefill);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());


        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

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
