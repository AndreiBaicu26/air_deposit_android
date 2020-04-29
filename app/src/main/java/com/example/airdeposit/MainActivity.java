package com.example.airdeposit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.airdeposit.fragments.HomeFragment;
import com.example.airdeposit.fragments.OrganiseFragment;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity  {
    private DrawerLayout drawer;
    Toolbar toolbar;
    TextView textViewEmployeeName;
    TextView textViewEmployeeID;
    Employee currentEmployee;
    NavController controller;
    NavigationView navView;
    AppBarConfiguration appBarConfiguration;
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

    }

    private void initFragments() {
        controller = Navigation.findNavController(this,R.id.nav_host_fragment);
        appBarConfiguration =new AppBarConfiguration.Builder(controller.getGraph()).build();
        NavigationUI.setupWithNavController(navView,controller);


    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }


}
