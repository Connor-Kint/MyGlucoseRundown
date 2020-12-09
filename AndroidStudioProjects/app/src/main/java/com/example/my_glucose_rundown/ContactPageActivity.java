///////////////////////////////////////////
// Class: ContactPage Activity
// Project: My Glucose Rundown
// Project-id: CP317-TP22
// Authors: Connor Kint, Nash McConnell, Rachel Sousa
// Student-ids: 180792270, 180827470, 180563960
//////////////////////////////////////////
package com.example.my_glucose_rundown;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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

public class ContactPageActivity extends AppCompatActivity {
    //Firebase access Variable Declarations
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
        setContentView(R.layout.contact_page_activity);

        //Firebase Access Variable Setup///////////////////
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        ///////////////////////////////////////////////////

        //Navigation Drawer Setup/////////////////////////
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        navView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(ContactPageActivity.this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        ///////////////////////////////////////////////////

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //Depending on user type, establish user access to different pages
                if (dataSnapshot.child("Users").child("Practitioner").hasChild(mAuth.getCurrentUser().getUid())) {
                    navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.nav_home:
                                    Intent i = new Intent(ContactPageActivity.this, PractitionerHomePageActivity.class);
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
                                    Intent i7 = new Intent(ContactPageActivity.this, ProfilePageActivity.class);
                                    startActivity(i7);
                                    break;
                                case R.id.nav_logout:
                                    Intent i4 = new Intent(ContactPageActivity.this, ProfileLogoutActivity.class);
                                    startActivity(i4);
                                    break;
                                case R.id.nav_about:
                                    Intent i6 = new Intent(ContactPageActivity.this, AboutPageActivity.class);
                                    startActivity(i6);
                                    break;
                                case R.id.nav_contact:

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
                                    Intent i7 = new Intent(ContactPageActivity.this, PatientHomePageActivity.class);
                                    startActivity(i7);
                                    break;
                                case R.id.nav_enter_reading:
                                    Intent i = new Intent(ContactPageActivity.this, EnterNewReadingActivity.class);
                                    startActivity(i);
                                    break;
                                case R.id.nav_trends:
                                    Intent i2 = new Intent(ContactPageActivity.this, PatientTrendsActivity.class);
                                    startActivity(i2);
                                    break;
                                case R.id.nav_prac_page:
                                    Intent i3 = new Intent(ContactPageActivity.this, PractitionerPageActivity.class);
                                    startActivity(i3);
                                    break;
                                case R.id.nav_profile_info:
                                    Intent i5 = new Intent(ContactPageActivity.this, ProfilePageActivity.class);
                                    startActivity(i5);
                                    break;
                                case R.id.nav_logout:
                                    Intent i4 = new Intent(ContactPageActivity.this, ProfileLogoutActivity.class);
                                    startActivity(i4);
                                    break;
                                case R.id.nav_about:
                                    Intent i6 = new Intent(ContactPageActivity.this, AboutPageActivity.class);
                                    startActivity(i6);
                                    break;
                                case R.id.nav_contact:
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

}
