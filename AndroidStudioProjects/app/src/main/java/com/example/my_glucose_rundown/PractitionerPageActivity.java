///////////////////////////////////////////
// Class: PractitionerPageActivity
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
import android.widget.TextView;

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

public class PractitionerPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Nagivation Drawer Variable Declarations
    DrawerLayout drawerLayout;
    NavigationView navView;
    Toolbar toolbar;
    //Firebase Access Variable Declarations
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    //Layout Attributes Variable Declarations
    private TextView practName, clinic, clinicAddress, clinicPhoneNumber, emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practitioner_page_activity);
        //Navigation Drawer Setup/////////////////////////////////
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
        navView.setCheckedItem(R.id.nav_prac_page);
        ////////////////////////////////////////////////////////////

        //Firebase Access Setup/////////////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        ///////////////////////////////////////////////////////////

        //Layout Attributes Setup//////////////////////////////////
        practName = findViewById(R.id.prac_name_blank);
        clinic = findViewById(R.id.clinic_name_title);
        clinicAddress = findViewById(R.id.clinic_name_title2);
        clinicPhoneNumber = findViewById(R.id.clinic_name_title3);
        emailAddress = findViewById(R.id.clinic_name_title4);
        ///////////////////////////////////////////////////////////

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String practitionerNameFromUser = dataSnapshot.child("Patients").child(mAuth.getCurrentUser().getUid()).child("practitionerName").getValue(String.class);
                //finds the name of the Practitioner for the current User

                //goes through all practitioners until the current users practitioner is found
                for(DataSnapshot practitioners : dataSnapshot.child("Practitioner").getChildren()) {
                    if (practitionerNameFromUser.startsWith(practitioners.child("firstName").getValue(String.class)) && practitionerNameFromUser.endsWith(practitioners.child("lastName").getValue(String.class))) {
                        //when found, loads the practitioners information into the text fields
                        DataSnapshot getPractitionerObject = dataSnapshot.child("Practitioner").child(practitioners.getKey());

                        String clinicName = getPractitionerObject.child("clinic_name").getValue(String.class);
                        String address = getPractitionerObject.child("clinic_address").getValue(String.class);
                        String phoneNumber = getPractitionerObject.child("phone_number").getValue(String.class);
                        String email = getPractitionerObject.child("contact_email").getValue(String.class);

                        practName.setText(practitionerNameFromUser);
                        clinic.setText(getString(R.string.clinic_name_title)+" "+clinicName);
                        clinicAddress.setText(getString(R.string.clinic_address_title)+" "+address);
                        clinicPhoneNumber.setText(getString(R.string.phone_number_title)+" "+phoneNumber);
                        emailAddress.setText(getString(R.string.email_address_title)+" "+email);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FragmentActivity", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        myRef.child("Users").addValueEventListener(postListener); //adds listener to the database reference
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
                Intent i3 = new Intent(PractitionerPageActivity.this, PatientHomePageActivity.class);
                startActivity(i3);
                break;
            case R.id.nav_enter_reading:
                Intent i = new Intent(PractitionerPageActivity.this, EnterNewReadingActivity.class);
                startActivity(i);
                break;
            case R.id.nav_trends:
                Intent i2 = new Intent(PractitionerPageActivity.this, PatientTrendsActivity.class);
                startActivity(i2);
                break;
            case R.id.nav_prac_page:
                break;
            case R.id.nav_profile_info:
                Intent i7 = new Intent(PractitionerPageActivity.this, ProfilePageActivity.class);
                startActivity(i7);
                break;
            case R.id.nav_logout:
                Intent i4 = new Intent(PractitionerPageActivity.this, ProfileLogoutActivity.class);
                startActivity(i4);
                break;
            case R.id.nav_about:
                Intent i5 = new Intent(PractitionerPageActivity.this, AboutPageActivity.class);
                startActivity(i5);
                break;
            case R.id.nav_contact:
                Intent i6 = new Intent(PractitionerPageActivity.this, ContactPageActivity.class);
                startActivity(i6);
                break;
        }
        return true;
    }
}
