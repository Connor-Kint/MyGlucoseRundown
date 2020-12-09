///////////////////////////////////////////
// Class: RegisterActivity
// Project: My Glucose Rundown
// Project-id: CP317-TP22
// Authors: Connor Kint, Nash McConnell, Rachel Sousa
// Student-ids: 180792270, 180827470, 180563960
//////////////////////////////////////////
package com.example.my_glucose_rundown;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    //Layout & Function Variable Declarations
    private Boolean prac_position;
    private EditText editFirstName, editLastName, editEmail, editPassword, editPasswordConfirm;
    private ProgressBar progressBar;
    private Button nextButton;
    private Spinner pracNameSpinner;
    //Firebase Access Variable Declaration
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        //Firebase Access Setup//////////////////////////
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference().child("Users").child("Practitioner");
        /////////////////////////////////////////////////

        //Function and Variable Setups///////////////////
        prac_position = Boolean.FALSE;

        editFirstName = (EditText) findViewById(R.id.reg_clinic_name);
        editLastName = (EditText) findViewById(R.id.reg_clinic_address);
        editEmail = (EditText) findViewById(R.id.reg_phone_number);
        editPassword = (EditText) findViewById(R.id.reg_password_blank);
        editPasswordConfirm = (EditText) findViewById(R.id.reg_password_confirm);

        progressBar = (ProgressBar) findViewById(R.id.reg_progress_bar);

        nextButton = (Button) findViewById(R.id.register_next_button);
        nextButton.setOnClickListener(this);

        pracNameSpinner = (Spinner) findViewById(R.id.reg_prac_spinner);
        /////////////////////////////////////////////////////

        Switch prac_switch = (Switch) findViewById(R.id.practitioner_switch);
        prac_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { //changes boolean value to check if a registrant is a practitioner
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prac_position = Boolean.TRUE;
                } else {
                    prac_position = Boolean.FALSE;
                }
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final List<String> names = new ArrayList<String>();

                names.add("None");
                for (DataSnapshot nameSnapshot: snapshot.getChildren()) { //Fill spinner with names of potential practitioners
                    String name = nameSnapshot.child("firstName").getValue(String.class) + " " + nameSnapshot.child("lastName").getValue(String.class);
                    if (name!=null) {
                        names.add(name);
                    }
                }

                ArrayAdapter<String> namesAdapter = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_item, names);
                namesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                pracNameSpinner.setAdapter(namesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) { //Changes activity depending on the button press
        if (prac_position) {
            Boolean successRegister = registerUser(prac_position);
            if(successRegister){
                Intent i = new Intent(RegisterActivity.this, PractitionerRegisterActivity.class);
                startActivity(i);
            }
        } else {
            Boolean successRegister = registerUser(prac_position);
            if(successRegister) {
                Intent i = new Intent(RegisterActivity.this, PatientHomePageActivity.class);
                startActivity(i);
            }
        }
    }

    private Boolean registerUser(Boolean prac_position) { //Registers a user
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String passwordConfirm = editPasswordConfirm.getText().toString().trim();
        String pracName = pracNameSpinner.getSelectedItem().toString();

        //All constraint checking on fields
        if(firstName.isEmpty()){
            editFirstName.setError("First name is required!");
            editFirstName.requestFocus();
            return false;
        }
        if(lastName.isEmpty()){
            editLastName.setError("Last name is required!");
            editLastName.requestFocus();
            return false;
        }
        if(email.isEmpty()){
            editEmail.setError("Email is required!");
            editEmail.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Valid email is required");
            editEmail.requestFocus();
            return false;
        }
        if(password.isEmpty()){
            editPassword.setError("Password is required!");
            editPassword.requestFocus();
            return false;
        }
        if(password.length() < 4) {
            editPassword.setError("Password must be at least 4 characters long!");
            editPassword.requestFocus();
            return false;
        }
        if(passwordConfirm.isEmpty()){
            editPasswordConfirm.setError("Password is required!");
            editPasswordConfirm.requestFocus();
            return false;
        }

        if(!passwordConfirm.equals(password)){
            editPasswordConfirm.setError("Password Does not match!");
            editPasswordConfirm.requestFocus();
            return false;
        }


        if (!prac_position) { //Register User depending on account type
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                PatientUser PatientUser = new PatientUser(firstName, lastName, email, pracName);

                                FirebaseDatabase.getInstance().getReference("Users/Patients")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(PatientUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "User has been successfully registered!", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Failed to register! Try Again!", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to register! Try Again!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                PractitionerUser PractitionerUser = new PractitionerUser(firstName, lastName, email);

                                FirebaseDatabase.getInstance().getReference("Users/Practitioner")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(PractitionerUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "User has been successfully registered!", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Failed to register! Try Again!", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to register! Try Again!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
        return true;
    }

}
