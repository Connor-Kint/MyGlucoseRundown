///////////////////////////////////////////
// Class: PractitionerHomePageActivity
// Description: This class will allow practitioner to view their patients trends, ability to change
//              between patients, and time range for trends
// Last Artifact Update: 8/19/2020
// Variables:
//      drawerLayout - DrawerLayout component for the navigation drawer
//      navView - NavigationView component for the navigation drawer
//      toolbar - Toolbar component for accessing the navigation drawer through "hamburger menu"
//      dailyAverage, morningAverage, afternoonAverage, nighttimeAverage - TextViews to display different average's of user's readings
//      userID, userName, textPatient  - String, texts used in the class for data storage
//      patientNamesSpinner - Spinner component for choice of current patient to view data of
//      timeSpinner - Spinner component for choice of time interval
//      mAuth - Firebase Authentication instance used in database access
//      database - Firebase database, reference to connected database
//      myRef - Reference to top node of database, used for accessing within the database
// Error Handling: will handle if user has no previous readings to calculate averages
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
import android.widget.Spinner;
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
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PractitionerHomePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Firebase Access Variable Declarations
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    //Layout Attributes Variable Declarations
    private Spinner patientNamesSpinner, timeSpinner;
    private String userID, userName, textPatient;
    private TextView dailyAverage, morningAverage, afternoonAverage, nighttimeAverage;
    //Navigation Drawer Variable Declarations
    DrawerLayout drawerLayout;
    NavigationView navView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practitioner_homepage_activity);

        //Navigation Drawer Setup///////////////////////////
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.prac_nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        navView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);
        navView.setCheckedItem(R.id.nav_home);
        /////////////////////////////////////////////////////

        //Time Spinner Setup////////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        patientNamesSpinner = (Spinner) findViewById(R.id.prac_patient_spinner);

        timeSpinner = (Spinner) findViewById(R.id.trend_time_period_prac);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(com.example.my_glucose_rundown.PractitionerHomePageActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.times_prac));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(myAdapter);
        /////////////////////////////////////////////////////

        userID = mAuth.getCurrentUser().getUid();
        myRef = database.getReference().child("Users").child("Practitioner");
        myRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() { //Get the name of the current User's Name
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("firstName").getValue().toString() + " " + snapshot.child("lastName").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("FragmentActivity", "loadPost:onCancelled", databaseError.toException());
            }
        });

        myRef = database.getReference().child("Users").child("Patients");

        //Fill Patient Spinner if the patient's practitionerName field is equal to the current user's name///////
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final List<String> names = new ArrayList<String>();

                names.add("None");
                for (DataSnapshot nameSnapshot: snapshot.getChildren()) { //
                    String pracName = nameSnapshot.child("practitionerName").getValue(String.class);
                    String name = nameSnapshot.child("firstName").getValue(String.class) + " " + nameSnapshot.child("lastName").getValue(String.class);

                    if (pracName.equals(userName)) {
                        if (name!=null) {
                            names.add(name);
                        }
                    }
                }

                ArrayAdapter<String> namesAdapter = new ArrayAdapter<String>(PractitionerHomePageActivity.this, android.R.layout.simple_spinner_item, names);
                namesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                patientNamesSpinner.setAdapter(namesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //////////////////////////////////////////////////////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////
        DecimalFormat df = new DecimalFormat("#.##");

        dailyAverage = findViewById(R.id.daily_average_label_prac);
        morningAverage = findViewById(R.id.morning_average_label_prac);
        afternoonAverage = findViewById(R.id.afternoon_average_label_prac);
        nighttimeAverage = findViewById(R.id.nightime_average_label_prac);

        myRef = database.getReference();

        //a listener for if a new patient is selected on the spinner
        patientNamesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //on new selection, default the time back to None and set the new patient to the textPatient variable
                timeSpinner.setSelection(0);
                textPatient = patientNamesSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //nothing for patientNamesSpinner
            }

        });

        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //the listener below checks if the user changes the time range spinner
                //if changed, it loads the user selected from the patient spinner with the latest readings of the given day range
                ValueEventListener postListenerPatient = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //first it checks if a patient has been selected and if the time spinner was reset to None
                        if(textPatient != null && textPatient != "None" && !timeSpinner.getSelectedItem().toString().equals("None")) {
                            //goes through all patients and checks if patients practitioner is the current practitioner user
                            for(DataSnapshot patients : dataSnapshot.child("Patients").getChildren()) {
                                if (textPatient.startsWith(patients.child("firstName").getValue(String.class)) && textPatient.endsWith(patients.child("lastName").getValue(String.class))) {
                                    //if the current practitioner user is the practitioner for the patient, load the patients readings into the gui
                                    if(dataSnapshot.child("Patients").child(patients.getKey()).hasChild("readings")) {
                                        LinkedHashMap<String, LinkedHashMap<String, Double>> days = new LinkedHashMap<>();
                                        for (DataSnapshot dates : dataSnapshot.child("Patients").child(patients.getKey()).child("readings").getChildren()) {
                                            LinkedHashMap<String, Double> temp = new LinkedHashMap<>();
                                            for (DataSnapshot times : dates.getChildren()) {
                                                temp.put(times.getKey(), times.getValue(Double.class));
                                            }
                                            days.put(dates.getKey(), temp);
                                        }

                                        List keys = new ArrayList(days.keySet());

                                        int dayCounter = 0;
                                        Double dayAverage = 0.0;

                                        Double morningTotal = 0.0;
                                        int morningCounter = 0;

                                        Double afternoonTotal = 0.0;
                                        int afternoonCounter = 0;

                                        Double nighttimeTotal = 0.0;
                                        int nighttimeCounter = 0;

                                        int startingNum;

                                        String text = timeSpinner.getSelectedItem().toString();
                                        //sets the starting number for the user to display given the spinner
                                        if (keys.size() >= 7 && text.equals("Last 7 Days")) {
                                            startingNum = keys.size() - 7;
                                        } else if (keys.size() >= 30 && text.equals("Last 30 Days")) {
                                            startingNum = keys.size() - 30;
                                        } else if (keys.size() >= 60 && text.equals("Last 60 Days")) {
                                            startingNum = keys.size() - 60;
                                        } else if (keys.size() >= 90 && text.equals("Last 90 Days")) {
                                            startingNum = keys.size() - 90;
                                        } else {
                                            startingNum = 0;
                                        }

                                        for (int x = startingNum; x < keys.size(); x++) {//goes through all dates given the range of starting number to the end of the readings
                                            List times = new ArrayList(days.get(keys.get(x)).keySet()); //gets the times on a specific day
                                            Double dailyTotal = 0.0;
                                            int dailyCounter = 0;
                                            for (int y = 0; y < times.size(); y++) {//goes through all readings for a given date
                                                if (times.get(y).toString().startsWith("0") || times.get(y).toString().startsWith("10") || times.get(y).toString().startsWith("11")) {
                                                    //checks if a reading is before noon
                                                    morningTotal += days.get(keys.get(x)).get(times.get(y));
                                                    morningCounter += 1;
                                                } else if (times.get(y).toString().startsWith("12") || times.get(y).toString().startsWith("13") || times.get(y).toString().startsWith("14") || times.get(y).toString().startsWith("15") || times.get(y).toString().startsWith("16") || times.get(y).toString().startsWith("17") || times.get(y).toString().startsWith("18")) {
                                                    //checks if a reading is after noon and before evening
                                                    afternoonTotal += days.get(keys.get(x)).get(times.get(y));
                                                    afternoonCounter += 1;
                                                } else {
                                                    //any remaining times will be after evening
                                                    nighttimeTotal += days.get(keys.get(x)).get(times.get(y));
                                                    nighttimeCounter += 1;
                                                }
                                                //regardless of time, add to the daily total
                                                dailyTotal += days.get(keys.get(x)).get(times.get(y));
                                                dailyCounter += 1;
                                            }
                                            dayAverage += (dailyTotal / dailyCounter);
                                            dayCounter += 1;
                                        }
                                        //load averages into the text-views
                                        dailyAverage.setText(getString(R.string.daily_average_title) + " " + df.format(dayAverage / dayCounter));
                                        morningAverage.setText(getString(R.string.morning_average_title) + " " + df.format(morningTotal / morningCounter));
                                        afternoonAverage.setText(getString(R.string.afternoon_average_title) + " " + df.format(afternoonTotal / afternoonCounter));
                                        nighttimeAverage.setText(getString(R.string.nighttime_average_title) + " " + df.format(nighttimeTotal / nighttimeCounter));
                                    }else{
                                        //if patient has no readings, set labels to 0
                                        dailyAverage.setText(getString(R.string.daily_average_title) + " " + df.format(0));
                                        morningAverage.setText(getString(R.string.morning_average_title) + " " + df.format(0));
                                        afternoonAverage.setText(getString(R.string.afternoon_average_title) + " " + df.format(0));
                                        nighttimeAverage.setText(getString(R.string.nighttime_average_title) + " " + df.format(0));
                                    }
                                    break;
                                }
                            }
                        }else{
                            //if switching users it defaults all labels back to 0
                            dailyAverage.setText(getString(R.string.daily_average_title) + " " + df.format(0));
                            morningAverage.setText(getString(R.string.morning_average_title) + " " + df.format(0));
                            afternoonAverage.setText(getString(R.string.afternoon_average_title) + " " + df.format(0));
                            nighttimeAverage.setText(getString(R.string.nighttime_average_title) + " " + df.format(0));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("FragmentActivity", "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                };

                myRef.child("Users").addValueEventListener(postListenerPatient); //add the listener to users reference in the database
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //nothing for timeSpinner
            }
        });

    }

    @Override
    public void onBackPressed() { //Method so that when back button is pressed, navigation drawer will close
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) { //activity switching activity for when an Item has been selected in Navigation Drawer
        switch(menuItem.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_profile_info:
                Intent i = new Intent(PractitionerHomePageActivity.this, ProfilePageActivity.class);
                startActivity(i);
                break;
            case R.id.nav_logout:
                Intent i4 = new Intent(PractitionerHomePageActivity.this, ProfileLogoutActivity.class);
                startActivity(i4);
                break;
            case R.id.nav_about:
                Intent i5 = new Intent(PractitionerHomePageActivity.this, AboutPageActivity.class);
                startActivity(i5);
                break;
            case R.id.nav_contact:
                Intent i6 = new Intent(PractitionerHomePageActivity.this, ContactPageActivity.class);
                startActivity(i6);
                break;
        }
        return true;
    }
}
