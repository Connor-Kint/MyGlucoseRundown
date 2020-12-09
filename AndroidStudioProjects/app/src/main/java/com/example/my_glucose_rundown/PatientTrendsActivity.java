///////////////////////////////////////////
// Class: PatientTrendsActivity
// Description: This class will allow patients to view their readings' trends, including a graph,
//              and the ability to choose how far back data will be accessed for trends
// Last Artifact Update: 8/19/2020
// Variables:
//      drawerLayout - DrawerLayout component for the navigation drawer
//      navView - NavigationView component for the navigation drawer
//      toolbar - Toolbar component for accessing the navigation drawer through "hamburger menu"
//      timeSpinner - Spinner component for choice of time interval
//      df - DecimalFormat formatter for decimal values
//      dailyAverage, morningAverage, afternoonAverage, nighttimeAverage - TextViews to display different average's of user's readings
//      graph - GraphView component to display trend lines for user's readings
//      mAuth - Firebase Authentication instance used in database access
//      database - Firebase database, reference to connected database
//      myRef - Reference to top node of database, used for accessing within the database
// Error Handling: will display NAN for Not applicable if the user has to readings entered
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PatientTrendsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Navigation Drawer Variable Declarations
    DrawerLayout drawerLayout;
    NavigationView navView;
    Toolbar toolbar;

    //Layout Attributes Variable Declarations
    private Spinner timeSpinner;
    private DecimalFormat df;
    private TextView dailyAverage, morningAverage, afternoonAverage, nighttimeAverage;
    private GraphView graph;

    //Firebase Access Variable Declarations
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_trends_activity);

        //Navigation Drawer Setup//////////////////////////
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
        navView.setCheckedItem(R.id.nav_trends);
        /////////////////////////////////////////////////////

        //Time Range Spinner Setup///////////////////////////
        timeSpinner = (Spinner) findViewById(R.id.trend_time_period);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(com.example.my_glucose_rundown.PatientTrendsActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.times));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(myAdapter);
        /////////////////////////////////////////////////////

        df = new DecimalFormat("#.##");

        //Firebase Access Setup///////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        //////////////////////////////////////////////////////

        dailyAverage = findViewById(R.id.daily_average_label);
        morningAverage = findViewById(R.id.morning_average_label);
        afternoonAverage = findViewById(R.id.afternoon_average_label);
        nighttimeAverage = findViewById(R.id.nightime_average_label);

        graph = findViewById(R.id.graph);
        graph.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.graph_x_axis));
        graph.getGridLabelRenderer().setVerticalAxisTitle(getString(R.string.mmoll_unit));

        //the listener below checks if the user changes the spinner
        //if changed, it reloads the users readings with the latest readings of the given day range
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //The listener below is creating a data snapshot of the database to be used for reading the current users information
                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //all reading data for a user will be stored in the fullReadings HashMap that is used for calculating the averages
                        LinkedHashMap<String, LinkedHashMap<String, Double>> fullReadings = new LinkedHashMap<>();
                        //this first for loop goes through all reading dates entered into the database
                        for(DataSnapshot dates : dataSnapshot.child("readings").getChildren()){
                            LinkedHashMap<String, Double> temp = new LinkedHashMap<>();
                            //this for loop will go through all the entered readings for a specific date
                            for(DataSnapshot times : dates.getChildren()){
                                temp.put(times.getKey(), times.getValue(Double.class));
                            }
                            fullReadings.put(dates.getKey(), temp);
                        }

                        List keys = new ArrayList(fullReadings.keySet());
                        graph.removeAllSeries(); //removes any old graph data
                        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(); //creates the new series for refreshed data

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
                        if(keys.size() >= 7 && text.equals("Last 7 Days")){
                            startingNum = keys.size()-7;
                        }else if(keys.size() >= 30 && text.equals("Last 30 Days")){
                            startingNum = keys.size()-30;
                        }else if(keys.size() >= 60 && text.equals("Last 60 Days")){
                            startingNum = keys.size()-60;
                        }else if(keys.size() >= 90 && text.equals("Last 90 Days")){
                            startingNum = keys.size()-90;
                        }else{
                            startingNum = 0;
                        }

                        for(int x = startingNum; x < keys.size(); x++){ //goes through all dates given the range of starting number to the end of the readings
                            List times = new ArrayList(fullReadings.get(keys.get(x)).keySet()); //gets the times on a specific day
                            Double dailyTotal = 0.0;
                            int dailyCounter = 0;
                            for(int y = 0; y < times.size(); y++){ //goes through all readings for a given date
                                if(times.get(y).toString().startsWith("0")||times.get(y).toString().startsWith("10")||times.get(y).toString().startsWith("11")){
                                    //checks if a reading is before noon
                                    morningTotal += fullReadings.get(keys.get(x)).get(times.get(y));
                                    morningCounter += 1;
                                }else if(times.get(y).toString().startsWith("12")||times.get(y).toString().startsWith("13")||times.get(y).toString().startsWith("14")||times.get(y).toString().startsWith("15")||times.get(y).toString().startsWith("16")||times.get(y).toString().startsWith("17")||times.get(y).toString().startsWith("18")){
                                    //checks if a reading is after noon and before evening
                                    afternoonTotal += fullReadings.get(keys.get(x)).get(times.get(y));
                                    afternoonCounter += 1;
                                }else{
                                    //any remaining times will be after evening
                                    nighttimeTotal += fullReadings.get(keys.get(x)).get(times.get(y));
                                    nighttimeCounter += 1;
                                }
                                //regardless of time, add to the daily total
                                dailyTotal += fullReadings.get(keys.get(x)).get(times.get(y));
                                dailyCounter += 1;
                            }
                            dayAverage += (dailyTotal/dailyCounter);
                            dayCounter += 1;
                            series.appendData(new DataPoint(dayCounter/1.0, Double.parseDouble(df.format(dailyTotal/dailyCounter))), false, keys.size()-startingNum);
                            //add the daily average to the graph as a data point
                        }
                        dailyAverage.setText(getString(R.string.daily_average_title)+" "+df.format(dayAverage/dayCounter));
                        morningAverage.setText(getString(R.string.morning_average_title)+" "+df.format(morningTotal/morningCounter));
                        afternoonAverage.setText(getString(R.string.afternoon_average_title)+" "+df.format(afternoonTotal/afternoonCounter));
                        nighttimeAverage.setText(getString(R.string.nighttime_average_title)+" "+df.format(nighttimeTotal/nighttimeCounter));

                        graph.getViewport().setMinX(1);
                        graph.getViewport().setMaxX(keys.size()-startingNum); //this sets the limit on the X axis to the total number of days a user has or range entered by spinner
                        graph.getViewport().setMinY(2.0); //limit the Y axis to 2 because no glucose readings will be below this
                        graph.getViewport().setMaxY(14.0);//limit the Y axis to 14 because no glucose readings will be above this
                        graph.getViewport().setYAxisBoundsManual(true);
                        graph.getViewport().setXAxisBoundsManual(true);

                        graph.addSeries(series); //adds the series of points to the graph on the gui
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("FragmentActivity", "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                };
                myRef.child("Users").child("Patients").child(mAuth.getCurrentUser().getUid()).addValueEventListener(postListener); //adds the listener above to the current user

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
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
                Intent i7 = new Intent(PatientTrendsActivity.this, PatientHomePageActivity.class);
                startActivity(i7);
                break;
            case R.id.nav_enter_reading:
                Intent i = new Intent(PatientTrendsActivity.this, EnterNewReadingActivity.class);
                startActivity(i);
                break;
            case R.id.nav_trends:
                break;
            case R.id.nav_prac_page:
                Intent i3 = new Intent(PatientTrendsActivity.this, PractitionerPageActivity.class);
                startActivity(i3);
                break;
            case R.id.nav_profile_info:
                Intent i2 = new Intent(PatientTrendsActivity.this, ProfilePageActivity.class);
                startActivity(i2);
                break;
            case R.id.nav_logout:
                Intent i4 = new Intent(PatientTrendsActivity.this, ProfileLogoutActivity.class);
                startActivity(i4);
                break;
            case R.id.nav_about:
                Intent i5 = new Intent(PatientTrendsActivity.this, AboutPageActivity.class);
                startActivity(i5);
                break;
            case R.id.nav_contact:
                Intent i6 = new Intent(PatientTrendsActivity.this, ContactPageActivity.class);
                startActivity(i6);
                break;
        }
        return true;
    }
}
