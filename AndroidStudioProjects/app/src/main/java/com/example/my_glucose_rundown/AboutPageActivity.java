///////////////////////////////////////////
// Class: AboutPageActivity
// Description: This class will displays information about the app
// Last Artifact Update: 8/19/2020
// Variables:
//      drawerLayout - DrawerLayout component for the navigation drawer
//      navView - NavigationView component for the navigation drawer
//      toolbar - Toolbar component for accessing the navigation drawer through "hamburger menu"
//      purposeText, creatorsText - TextViews to display app information
//      mAuth - Firebase Authentication instance used in database access
//      database - Firebase database, reference to connected database
//      myRef - Reference to top node of database, used for accessing within the database
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

import org.w3c.dom.Text;

public class AboutPageActivity extends AppCompatActivity {
    //Layout Attributes Variable Declarations
    private TextView purposeText, creatorsText;

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
        setContentView(R.layout.about_app_activity);

        //Firebase Access Setup////////////////////
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        ///////////////////////////////////////////

        //Layout Attribute variable Setup//////////
        purposeText = (TextView) findViewById(R.id.purpose_text);
        creatorsText = (TextView) findViewById(R.id.creators_text);
        ////////////////////////////////////////////

        //Navigation Drawer Setup///////////////////
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        navView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(AboutPageActivity.this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        ////////////////////////////////////////////

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //Depending on user type, establish user access to different pages
                if (dataSnapshot.child("Users").child("Practitioner").hasChild(mAuth.getCurrentUser().getUid())) {
                    navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.nav_home:
                                    Intent i = new Intent(AboutPageActivity.this, PractitionerHomePageActivity.class);
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
                                    Intent i7 = new Intent(AboutPageActivity.this, ProfilePageActivity.class);
                                    startActivity(i7);
                                case R.id.nav_logout:
                                    Intent i4 = new Intent(AboutPageActivity.this, ProfileLogoutActivity.class);
                                    startActivity(i4);
                                    break;
                                case R.id.nav_about:
                                    break;
                                case R.id.nav_contact:
                                    Intent i6 = new Intent(AboutPageActivity.this, ContactPageActivity.class);
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
                                    Intent i7 = new Intent(AboutPageActivity.this, PatientHomePageActivity.class);
                                    startActivity(i7);
                                    break;
                                case R.id.nav_enter_reading:
                                    Intent i = new Intent(AboutPageActivity.this, EnterNewReadingActivity.class);
                                    startActivity(i);
                                    break;
                                case R.id.nav_trends:
                                    Intent i2 = new Intent(AboutPageActivity.this, PatientTrendsActivity.class);
                                    startActivity(i2);
                                    break;
                                case R.id.nav_prac_page:
                                    Intent i3 = new Intent(AboutPageActivity.this, PractitionerPageActivity.class);
                                    startActivity(i3);
                                    break;
                                case R.id.nav_profile_info:
                                    Intent i5 = new Intent(AboutPageActivity.this, ProfilePageActivity.class);
                                    startActivity(i5);
                                    break;
                                case R.id.nav_logout:
                                    Intent i4 = new Intent(AboutPageActivity.this, ProfileLogoutActivity.class);
                                    startActivity(i4);
                                    break;
                                case R.id.nav_about:
                                    break;
                                case R.id.nav_contact:
                                    Intent i6 = new Intent(AboutPageActivity.this, ContactPageActivity.class);
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
                Log.w("FragmentActivity", "loadPost:onCancelled", error.toException());
            }
        };
        myRef.addListenerForSingleValueEvent(postListener);
        }
    }