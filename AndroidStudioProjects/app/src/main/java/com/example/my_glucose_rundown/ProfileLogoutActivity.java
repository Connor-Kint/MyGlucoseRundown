///////////////////////////////////////////
// Class: ProfileLogoutActivity
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
import android.widget.Button;
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



public class ProfileLogoutActivity extends AppCompatActivity implements View.OnClickListener {
    //Layout Attributes Variable Declarations
    private Button logoutButton;
    private TextView logoutText;
    //Firebase Access Variable Declarations
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    //Navigation Drawer Variable Declarations
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_logout_activity);
        //Firebase Access Setup////////////////////////////
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        ///////////////////////////////////////////////////

        //Layout Attribute Setup///////////////////////////
        logoutText = (TextView) findViewById(R.id.logout_text);
        logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(this);
        ///////////////////////////////////////////////////

        //Navigation Drawer Setup/////////////////////////
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        navView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(ProfileLogoutActivity.this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        ////////////////////////////////////////////////////
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //activity switching activity for when an Item has been selected in Navigation Drawer
                if(dataSnapshot.child("Users").child("Practitioner").hasChild(mAuth.getCurrentUser().getUid())) {
                    navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                            switch(menuItem.getItemId()) {
                                case R.id.nav_home:
                                    Intent i = new Intent(ProfileLogoutActivity.this, PractitionerHomePageActivity.class);
                                    startActivity(i);
                                    break;
                                case R.id.nav_enter_reading:
                                    drawerLayout.closeDrawer(GravityCompat.START, false);
                                    Toast.makeText(getApplicationContext(), "This page are for Patients only!", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.nav_trends:
                                    drawerLayout.closeDrawer(GravityCompat.START, false);
                                    Toast.makeText(getApplicationContext(), "This page are for Patients only!", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.nav_prac_page:
                                    drawerLayout.closeDrawer(GravityCompat.START, false);
                                    Toast.makeText(getApplicationContext(), "This page are for Patients only!", Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.nav_profile_info:
                                    Intent i7 = new Intent(ProfileLogoutActivity.this, ProfilePageActivity.class);
                                    startActivity(i7);
                                case R.id.nav_logout:
                                    break;
                                case R.id.nav_about:
                                    Intent i5 = new Intent(ProfileLogoutActivity.this, AboutPageActivity.class);
                                    startActivity(i5);
                                    break;
                                case R.id.nav_contact:
                                    Intent i6 = new Intent(ProfileLogoutActivity.this, ContactPageActivity.class);
                                    startActivity(i6);
                                    break;
                            }
                            return false;
                        }
                    });
                    navView.setCheckedItem(R.id.nav_logout);
                } else {
                    navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.nav_home:
                                    Intent i7 = new Intent(ProfileLogoutActivity.this, PatientHomePageActivity.class);
                                    startActivity(i7);
                                    break;
                                case R.id.nav_enter_reading:
                                    Intent i = new Intent(ProfileLogoutActivity.this, EnterNewReadingActivity.class);
                                    startActivity(i);
                                    break;
                                case R.id.nav_trends:
                                    Intent i2 = new Intent(ProfileLogoutActivity.this, PatientTrendsActivity.class);
                                    startActivity(i2);
                                    break;
                                case R.id.nav_prac_page:
                                    Intent i3 = new Intent(ProfileLogoutActivity.this, PractitionerPageActivity.class);
                                    startActivity(i3);
                                    break;
                                case R.id.nav_profile_info:
                                    Intent i4 = new Intent(ProfileLogoutActivity.this, ProfilePageActivity.class);
                                    startActivity(i4);
                                    break;
                                case R.id.nav_logout:
                                    break;
                                case R.id.nav_about:
                                    Intent i5 = new Intent(ProfileLogoutActivity.this, AboutPageActivity.class);
                                    startActivity(i5);
                                    break;
                                case R.id.nav_contact:
                                    Intent i6 = new Intent(ProfileLogoutActivity.this, ContactPageActivity.class);
                                    startActivity(i6);
                                    break;
                            }
                            return false;
                        }
                    });
                    navView.setCheckedItem(R.id.nav_logout);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
    public void onClick(View v) {
        mAuth.signOut();
        Intent i = new Intent(ProfileLogoutActivity.this, LoginActivity.class);
        startActivity(i);
    }
}
