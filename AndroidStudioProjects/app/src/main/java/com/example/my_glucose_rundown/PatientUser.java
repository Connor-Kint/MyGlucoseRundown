///////////////////////////////////////////
// Class: PatientUser
// Project: My Glucose Rundown
// Project-id: CP317-TP22
// Authors: Connor Kint, Nash McConnell, Rachel Sousa
// Student-ids: 180792270, 180827470, 180563960
//////////////////////////////////////////
package com.example.my_glucose_rundown;

import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class PatientUser {
    public String firstName, lastName, email, practitionerName, userID;

    public PatientUser() {

    }
    public PatientUser(String firstName, String lastName, String email, String practitionerName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.practitionerName = practitionerName;
    }
    public PatientUser(List<String> dataList) {
        this.userID = dataList.get(0);
        this.firstName = dataList.get(1);
        this.lastName = dataList.get(2);
        this.email = dataList.get(3);
        this.practitionerName = dataList.get(4);
    }

    //getter functions if needed
    public String getUID() {
        return userID;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() { return lastName;}
    public String getEmail() {
        return this.email;
    }
    public String getPractitionerName() {
        return practitionerName;
    }

}
