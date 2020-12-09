///////////////////////////////////////////
// Class: PatientHomePageActivity
// Project: My Glucose Rundown
// Project-id: CP317-TP22
// Authors: Connor Kint, Nash McConnell, Rachel Sousa
// Student-ids: 180792270, 180827470, 180563960
//////////////////////////////////////////
package com.example.my_glucose_rundown;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class PatientHomePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Navigation Drawer Variable Declarations
    DrawerLayout drawerLayout;
    NavigationView navView;
    Toolbar toolbar;
    //Layout Attributes Variable Declarations
    private TextView helloText, sevenDayNum, todayAvgNum, latestReadingNum;
    private DecimalFormat df;
    private String currentDateForReading;
    //Firebase Access Variable Declarations
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_homepage_activity);

        //Navigation Drawer Setup///////////////////////
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        navView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);
        navView.setCheckedItem(R.id.nav_home);

        df = new DecimalFormat("#.##");
        //creates a format to round the reading averages
        currentDateForReading = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        //creates a format for the date to be used to read the readings from the database

        //Firebase Access Setup///////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        //////////////////////////////////////////////////////

        helloText = findViewById(R.id.hello_text);
        sevenDayNum = findViewById(R.id.num_seven_day_average);
        todayAvgNum = findViewById(R.id.num_day_average);
        latestReadingNum = findViewById(R.id.num_latest_reading);
        helloText.setText("");

        //The listener below is creating a data snapshot of the database to be used for reading the current users information
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                helloText.setText("Hello, "+dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("firstName").getValue()+" "+dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("lastName").getValue()+"!");

                //this checks if the user has any readings in the database to read from
                if(dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild("readings")) {
                    //all reading data for a user will be stored in the fullReadings HashMap that is used for calculating the averages
                    LinkedHashMap<String, LinkedHashMap<String, Double>> fullReadings = new LinkedHashMap<>();
                    //this first for loop goes through all reading dates entered into the database
                    for (DataSnapshot dates : dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("readings").getChildren()) {
                        LinkedHashMap<String, Double> temp = new LinkedHashMap<>();
                        //this for loop will go through all the entered readings for a specific date
                        for (DataSnapshot times : dates.getChildren()) {
                            temp.put(times.getKey(), times.getValue(Double.class));
                        }
                        fullReadings.put(dates.getKey(), temp);
                    }
                    List keys = new ArrayList(fullReadings.keySet()); //a list with all dates from the user readings
                    Double total = 0.0;
                    int readingCounter = 0;
                    int startingNum = 0;
                    if (keys.size() >= 7) {// if the user has more than 7 dates for readings, it takes the most recent 7 days for averages
                        startingNum = keys.size() - 7;
                    }
                    for (int x = startingNum; x < keys.size(); x++) {
                        List times = new ArrayList(fullReadings.get(keys.get(x)).keySet()); //a list with all times a reading was entered for a given day
                        for (int y = 0; y < times.size(); y++) {
                            total += fullReadings.get(keys.get(x)).get(times.get(y)); //adds each reading of the day to the total for averaging
                            readingCounter += 1;
                        }
                    }
                    sevenDayNum.setText(df.format(total / readingCounter));
                    ////////////////
                    total = 0.0;
                    readingCounter = 0;
                    List times = new ArrayList(fullReadings.get(keys.get(keys.size() - 1)).keySet()); //a list with all times a reading was entered for the last day
                    if (keys.get(keys.size() - 1).toString().equals(currentDateForReading)) { // checks if the last reading was entered on today's date
                        for (int y = 0; y < times.size(); y++) {
                            total += fullReadings.get(keys.get(keys.size() - 1)).get(times.get(y)); //adds each reading of today's date to the total for day average
                            readingCounter += 1;
                        }
                        todayAvgNum.setText(df.format(total / readingCounter));
                    } else {
                        todayAvgNum.setText(df.format(0.0));
                    }

                    ////////////////
                    latestReadingNum.setText(df.format(fullReadings.get(keys.get(keys.size() - 1)).get(times.get(times.size() - 1)))); //gets the users last reading entered into the database
                }else{ // if the user has no readings, set the labels to 0
                    todayAvgNum.setText(df.format(0.0));
                    latestReadingNum.setText(df.format(0.0));
                    sevenDayNum.setText(df.format(0.0));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FragmentActivity", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        myRef.child("Users").child("Patients").addValueEventListener(postListener); //adds the listener above to the current user
    }

    @Override
    public void onBackPressed() { //Function to make navigation drawer close when back button is pressed
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) { //Changes Activity based on the selected item in the navigation drawer
        switch(menuItem.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_enter_reading:
                Intent i = new Intent(PatientHomePageActivity.this, EnterNewReadingActivity.class);
                startActivity(i);
                break;
            case R.id.nav_trends:
                Intent i2 = new Intent(PatientHomePageActivity.this, PatientTrendsActivity.class);
                startActivity(i2);
                break;
            case R.id.nav_prac_page:
                Intent i3 = new Intent(PatientHomePageActivity.this, PractitionerPageActivity.class);
                startActivity(i3);
                break;
            case R.id.nav_profile_info:
                Intent i7 = new Intent(PatientHomePageActivity.this, ProfilePageActivity.class);
                startActivity(i7);
                break;
            case R.id.nav_logout:
                Intent i4 = new Intent(PatientHomePageActivity.this, ProfileLogoutActivity.class);
                startActivity(i4);
                break;
            case R.id.nav_about:
                Intent i5 = new Intent(PatientHomePageActivity.this, AboutPageActivity.class);
                startActivity(i5);
                break;
            case R.id.nav_contact:
                Intent i6 = new Intent(PatientHomePageActivity.this, ContactPageActivity.class);
                startActivity(i6);
                break;
        }
        return true;
    }

    public void onButtonClick(View v) { //Changes activity if user wants to enter reading
        Intent i = new Intent(PatientHomePageActivity.this, EnterNewReadingActivity.class);
        startActivity(i);
    }
}



