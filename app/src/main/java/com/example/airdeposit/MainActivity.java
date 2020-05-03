package com.example.airdeposit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.airdeposit.callbacks.CallBackProduct;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DrawerLayout drawer;
    Toolbar toolbar;
    TextView textViewEmployeeName;
    TextView textViewEmployeeID;
    Employee currentEmployee;
    NavController controller;
    NavigationView navView;
    AppBarConfiguration appBarConfiguration;
    EditText productCodeId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        setToolbar();
        setDrawer();
        Intent it = getIntent();
        currentEmployee = it.getParcelableExtra("employee");

        setCurrentEmployee();

        initFragments();
        productCodeId = toolbar.findViewById(R.id.inputProductId);


    }

    private void initFragments() {

        controller = Navigation.findNavController(this,R.id.nav_host_fragment);
       NavigationUI.setupActionBarWithNavController(this,controller,drawer);
        NavigationUI.setupWithNavController(navView,controller);
        navView.setNavigationItemSelectedListener(this);

    }

    private void setCurrentEmployee() {
       navView = findViewById(R.id.nav_view);

        View header = navView.getHeaderView(0);
        textViewEmployeeName = header.findViewById(R.id.textView_employeeName);
        textViewEmployeeID = header.findViewById(R.id.textView_employeeID);
        String employeeID = "ID: " + currentEmployee.getEmployeeID();
        textViewEmployeeID.setText(employeeID);
        String employeeFullName = currentEmployee.getFirstName() + " " + currentEmployee.getLastName();
        textViewEmployeeName.setText(employeeFullName);
    }

    public void setToolbar(){
        toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
    }

    public void setDrawer(){
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen((GravityCompat.START))){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }






    public void imgPressSearchProduct(final View view) {

        if(productCodeId.getText().toString().length() > 0) {
            String productCode = productCodeId.getText().toString();
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                }});

            Firebase.getProduct(productCode, new CallBackProduct() {
                @Override
                public void onCallbackProduct(Product product) {
                    if (product == null) {
                        builder.setTitle("Could not detect product");
                        builder.setMessage("Speak to a manager");

                        AlertDialog alert = builder.create();
                        alert.show();

                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("product",product);
                        controller.navigate(R.id.action_homeFragment_to_productDetailsFragment,bundle);
                    }
                }});

        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(controller,drawer);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, controller)
                || super.onOptionsItemSelected(item);
    }
}
