///////////////////////////////////////////
// Class: Login Activity
// Project: My Glucose Rundown
// Project-id: CP317-TP22
// Authors: Connor Kint, Nash McConnell, Rachel Sousa
// Student-ids: 180792270, 180827470, 180563960
//////////////////////////////////////////
package com.example.my_glucose_rundown;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    //Layout Attributes Variable Declarations
    private EditText editEmail, editPassword;
    private Button loginButton, registerButton;

    //Firebase Access Variable Declarations
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        //Firebase Access Setup///////////////////////////////
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference();
        //////////////////////////////////////////////////////

        //Layout Attributes Setup/////////////////////////////
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);

        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);

        editEmail = (EditText) findViewById(R.id.login_email);
        editPassword = (EditText) findViewById(R.id.login_password);
        ///////////////////////////////////////////////////////
    }




    @Override
    public void onClick(View v) { //Determines what is done depending on which button is pressed, either "login" or "register"
        switch (v.getId()) {
            case R.id.register_button:
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                break;
            case R.id.login_button:
                userLogin();
                break;
        }
    }

    private void userLogin() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        //Conditions for Registration Info///////////////////////
        if (email.isEmpty()) {
            editEmail.setError("Email is required!");
            editEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Please enter a valid email!");
            editEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editPassword.setError("Password is required");
            editPassword.requestFocus();
            return;
        }
        ////////////////////////////////////////////////////////

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) { //Login in users
                if (task.isSuccessful()){
                    ValueEventListener postListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) { //If login is successful, determine what type of user is logging in, and direct them as such
                            if(dataSnapshot.child("Users").child("Practitioner").hasChild(mAuth.getCurrentUser().getUid())) {
                                Intent i = new Intent(LoginActivity.this, PractitionerHomePageActivity.class);
                                startActivity(i);
                            } else {
                                Intent i2 = new Intent(LoginActivity.this, PatientHomePageActivity.class);
                                startActivity(i2);
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
                } else { //Notify user if login has failed
                    Toast.makeText(LoginActivity.this, "Failed to login! Please check email or password, or register.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}