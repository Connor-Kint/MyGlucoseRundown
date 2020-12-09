///////////////////////////////////////////
// Class: ProfilePageActivity
// Description: This class will allow users to see their own profile info, and if they are a patient, they have the ability to change their practitioner
// Last Artifact Update: 8/19/2020
// Variables:
//      drawerLayout - DrawerLayout component for the navigation drawer
//      navView - NavigationView component for the navigation drawer
//      toolbar - Toolbar component for accessing the navigation drawer through "hamburger menu"
//      textFirstName, textLastName, textEmail - TextViews to display user's information
//      userID - String to store current user's user id
//      pracNameSpinner - Spinner component so that patient's can change their practitioner
//      mAuth - Firebase Authentication instance used in database access
//      database - Firebase database, reference to connected database
//      myRef - Reference to top node of database, used for accessing within the database
// Error Handling: will display none if data is missing
// Outside Access: Firebase Database accessed for user data access
// Project: My Glucose Rundown
// Project-id: CP317-TP22
// Authors: Connor Kint, Nash McConnell, Rachel Sousa
// Student-ids: 180792270, 180827470, 180563960
//////////////////////////////////////////
package com.example.my_glucose_rundown;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfilePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Firebase Access Variable Declarations
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    //Layout Attributes Variable Declarations
    private String userID;
    private Spinner pracNameSpinner;
    private TextView textFirstName, textLastName, textEmail;
    //Navigation Drawer Variable Declaration
    DrawerLayout drawerLayout;
    NavigationView navView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page_activity);
        //Firebase Access Setup/////////////////////////
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference();
        ////////////////////////////////////////////////

        //Navigation Drawer Setup///////////////////////
        textFirstName = (TextView) findViewById(R.id.first_name_text);
        textLastName = (TextView) findViewById(R.id.last_name_text);
        textEmail = (TextView) findViewById(R.id.email_text);

        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        navView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(ProfilePageActivity.this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        /////////////////////////////////////////////////

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Users").child("Patients").hasChild(mAuth.getCurrentUser().getUid())) {

                    userID = mAuth.getCurrentUser().getUid();
                    myRef = database.getReference().child("Users").child("Patients"); //may not work may need to fix
                    myRef.child(userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            textFirstName.setText(snapshot.child("firstName").getValue(String.class));
                            textLastName.setText(snapshot.child("lastName").getValue(String.class));
                            textEmail.setText(snapshot.child("email").getValue(String.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //will not crash as user has to be logged in
                        }
                    });

                    pracNameSpinner = (Spinner) findViewById(R.id.practitioner_spinner);

                    myRef = database.getReference().child("Users").child("Practitioner");
                    myRef.addValueEventListener(new ValueEventListener() { //Fill Spinner with all practitioner names, that way users can change practitioners
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            final List<String> names = new ArrayList<String>();
                            names.add("Click to change practitioner!");
                            names.add("None");
                            for (DataSnapshot nameSnapshot: snapshot.getChildren()) {
                                String name = nameSnapshot.child("firstName").getValue(String.class) + " " + nameSnapshot.child("lastName").getValue(String.class);
                                if (name!=null) {
                                    names.add(name);
                                }
                            }

                            ArrayAdapter<String> namesAdapter = new ArrayAdapter<String>(ProfilePageActivity.this, android.R.layout.simple_spinner_item, names);
                            namesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            pracNameSpinner.setAdapter(namesAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    pracNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //Change user's practitioner if they change it
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (!pracNameSpinner.getSelectedItem().toString().equals("Click to change practitioner!")) {
                                userID = mAuth.getCurrentUser().getUid();
                                myRef = database.getReference().child("Users").child("Patients");
                                myRef.child(userID).child("practitionerName").setValue(pracNameSpinner.getSelectedItem().toString());
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() { //Changes Activity based on the selected item in the navigation drawer
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.nav_home:
                                    Intent i7 = new Intent(ProfilePageActivity.this, PatientHomePageActivity.class);
                                    startActivity(i7);
                                    break;
                                case R.id.nav_enter_reading:
                                    Intent i = new Intent(ProfilePageActivity.this, EnterNewReadingActivity.class);
                                    startActivity(i);
                                    break;
                                case R.id.nav_trends:
                                    Intent i2 = new Intent(ProfilePageActivity.this, PatientTrendsActivity.class);
                                    startActivity(i2);
                                    break;
                                case R.id.nav_prac_page:
                                    Intent i3 = new Intent(ProfilePageActivity.this, PractitionerPageActivity.class);
                                    startActivity(i3);
                                    break;
                                case R.id.nav_profile_info:
                                    break;
                                case R.id.nav_logout:
                                    Intent i4 = new Intent(ProfilePageActivity.this, ProfileLogoutActivity.class);
                                    startActivity(i4);
                                    break;
                                case R.id.nav_about:
                                    Intent i5 = new Intent(ProfilePageActivity.this, AboutPageActivity.class);
                                    startActivity(i5);
                                    break;
                                case R.id.nav_contact:
                                    Intent i6 = new Intent(ProfilePageActivity.this, ContactPageActivity.class);
                                    startActivity(i6);
                                    break;
                            }
                            return false;
                        }
                    });
                    navView.setCheckedItem(R.id.nav_profile_info);
                } else {
                    navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() { //Changes Activity based on the selected item in the navigation drawer
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                            switch(menuItem.getItemId()) {
                                case R.id.nav_home:
                                    Intent i = new Intent(ProfilePageActivity.this, PractitionerHomePageActivity.class);
                                    startActivity(i);
                                    break;
                                case R.id.nav_enter_reading:
                                    drawerLayout.closeDrawer(GravityCompat.START, false);
                                    Toast.makeText(ProfilePageActivity.this, "This page are for Patients only!", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.nav_trends:
                                    drawerLayout.closeDrawer(GravityCompat.START, false);
                                    Toast.makeText(ProfilePageActivity.this, "This page are for Patients only!", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.nav_prac_page:
                                    drawerLayout.closeDrawer(GravityCompat.START, false);
                                    Toast.makeText(ProfilePageActivity.this, "This page are for Patients only!", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.nav_profile_info:
                                    break;
                                case R.id.nav_logout:
                                    Intent i4 = new Intent(ProfilePageActivity.this, ProfileLogoutActivity.class);
                                    startActivity(i4);
                                    break;
                                case R.id.nav_about:
                                    Intent i5 = new Intent(ProfilePageActivity.this, AboutPageActivity.class);
                                    startActivity(i5);
                                    break;
                                case R.id.nav_contact:
                                    Intent i6 = new Intent(ProfilePageActivity.this, ContactPageActivity.class);
                                    startActivity(i6);
                                    break;
                            }
                            return false;
                        }
                    });
                    navView.setCheckedItem(R.id.nav_profile_info);

                    userID = mAuth.getCurrentUser().getUid();
                    myRef = database.getReference().child("Users").child("Practitioner"); //may not work may need to fix
                    myRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            textFirstName.setText(snapshot.child("firstName").getValue(String.class));
                            textLastName.setText(snapshot.child("lastName").getValue(String.class));
                            textEmail.setText(snapshot.child("email").getValue(String.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //will not crash as user has to be logged in
                        }
                    });



                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FragmentActivity", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        myRef.addListenerForSingleValueEvent(postListener);


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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Method needed to get rid of errors, however method is used in OnCreate due to choice between practitioner navigation drawer and patient navigation drawer
        return false;
    }

}
