///////////////////////////////////////////
// Class: EnterNewReadingActivity
// Project: My Glucose Rundown
// Project-id: CP317-TP22
// Authors: Connor Kint, Nash McConnell, Rachel Sousa
// Student-ids: 180792270, 180827470, 180563960
//////////////////////////////////////////
package com.example.my_glucose_rundown;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class EnterNewReadingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Navigation Drawer Variable Declarations
    DrawerLayout drawerLayout;
    NavigationView navView;
    Toolbar toolbar;
    //Firebase Access Variable Declarations
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_reading_activity);

        //Navigation Drawer Setup/////////////////////////
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
        navView.setCheckedItem(R.id.nav_enter_reading);

        String currentDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());

        TextView text_date = findViewById(R.id.text_currentDate);
        text_date.setText(currentDate);


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
                Intent i = new Intent(EnterNewReadingActivity.this, PatientHomePageActivity.class);
                startActivity(i);
                break;
            case R.id.nav_enter_reading:
                break;
            case R.id.nav_trends:
                Intent i2 = new Intent(EnterNewReadingActivity.this, PatientTrendsActivity.class);
                startActivity(i2);
                break;
            case R.id.nav_prac_page:
                Intent i3 = new Intent(EnterNewReadingActivity.this, PractitionerPageActivity.class);
                startActivity(i3);
                break;
            case R.id.nav_profile_info:
                Intent i7 = new Intent(EnterNewReadingActivity.this, ProfilePageActivity.class);
                startActivity(i7);
                break;
            case R.id.nav_logout:
                Intent i4 = new Intent(EnterNewReadingActivity.this, ProfileLogoutActivity.class);
                startActivity(i4);
                break;
            case R.id.nav_about:
                Intent i5 = new Intent(EnterNewReadingActivity.this, AboutPageActivity.class);
                startActivity(i5);
                break;
            case R.id.nav_contact:
                Intent i6 = new Intent(EnterNewReadingActivity.this, ContactPageActivity.class);
                startActivity(i6);
                break;
        }
        return true;
    }
    public void onButtonClick(View v) { //Adds value in edit text field to database, under readings and with title of reading entry time
        EditText entered_reading = findViewById(R.id.glucose_reading);

        //Firebase Access Setup///////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        //////////////////////////////////////////////////////

        String currentDateForReading = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());//gets current date
        String currentTimeForReading = new SimpleDateFormat("kk:mm", Locale.getDefault()).format(Calendar.getInstance().getTime()); //gets current time
        if (!entered_reading.getText().toString().matches("")) { //checks if a user has entered data
            myRef.child("Users").child("Patients").child(mAuth.getCurrentUser().getUid()).child("readings").child(currentDateForReading).child(currentTimeForReading).setValue(Double.parseDouble(entered_reading.getText().toString()));
            //adds the reading into the current users profile in the database

            Intent i = new Intent(EnterNewReadingActivity.this, PatientHomePageActivity.class);
            //after the reading is entered, return the user to the home screen
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), "Please enter a reading!", Toast.LENGTH_LONG).show();
        }

    }
}