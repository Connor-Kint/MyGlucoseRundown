///////////////////////////////////////////
// Class: PractitionerRegisterActivity
// Project: My Glucose Rundown
// Project-id: CP317-TP22
// Authors: Connor Kint, Nash McConnell, Rachel Sousa
// Student-ids: 180792270, 180827470, 180563960
//////////////////////////////////////////
package com.example.my_glucose_rundown;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PractitionerRegisterActivity extends AppCompatActivity implements View.OnClickListener{
    //Layout Attributes Variable Declarations
    private EditText editClinicName, editClinicAddress, editPhoneNumber, editContactEmail;
    private Button regButton;

    //Firebase Access Variable Declarations
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prac_register_activity);
        //Firebase Access Setup/////////////////////////
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        ////////////////////////////////////////////////

        //Layout Variable Setup/////////////////////////
        editClinicAddress = (EditText) findViewById(R.id.reg_clinic_address);
        editClinicName = (EditText) findViewById(R.id.reg_clinic_name);
        editPhoneNumber = (EditText) findViewById(R.id.reg_phone_number);
        editContactEmail = (EditText) findViewById(R.id.reg_contact_email);

        regButton = (Button) findViewById(R.id.prac_reg_button);
        regButton.setOnClickListener(this);
        /////////////////////////////////////////////////
    }

    @Override
    public void onClick(View v) { //button action for register
        Boolean successPrac = addPracInfo();
        if(successPrac) {
            Intent i = new Intent(PractitionerRegisterActivity.this, PractitionerHomePageActivity.class);
            startActivity(i);
        }
    }

    private Boolean addPracInfo() {  //add practitioner info to the current user registering
        String clinicName = editClinicName.getText().toString().trim();
        String clinicAddress = editClinicAddress.getText().toString().trim();
        String phoneNumber = editPhoneNumber.getText().toString().trim();
        String contactEmail = editContactEmail.getText().toString().trim();

        if(clinicName.isEmpty()){
            editClinicName.setError("Clinic Name Required!");
            editClinicName.requestFocus();
            return false;
        }
        if(clinicAddress.isEmpty()){
            editClinicAddress.setError("Clinic Address Required!");
            editClinicAddress.requestFocus();
            return false;
        }
        if(!(clinicName.isEmpty())){
            if (!(Patterns.PHONE.matcher(phoneNumber).matches())){
                editPhoneNumber.setError("Need Valid Phone Number");
                editPhoneNumber.requestFocus();
                return false;
            }
        }
        if(contactEmail.isEmpty()){
            editContactEmail.setError("Contact Email Required!");
            editContactEmail.requestFocus();
            return false;
        }


        myRef.child("Users").child("Practitioner").child(mAuth.getCurrentUser().getUid()).child("clinic_name").setValue(clinicName);
        myRef.child("Users").child("Practitioner").child(mAuth.getCurrentUser().getUid()).child("clinic_address").setValue(clinicAddress);
        if (!phoneNumber.isEmpty()) {
            myRef.child("Users").child("Practitioner").child(mAuth.getCurrentUser().getUid()).child("phone_number").setValue(phoneNumber);
        }
        myRef.child("Users").child("Practitioner").child(mAuth.getCurrentUser().getUid()).child("contact_email").setValue(contactEmail);
         return true;
    }
}
