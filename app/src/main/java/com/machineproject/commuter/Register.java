package com.machineproject.commuter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import model.User;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;

    private EditText lastName;
    private EditText firstName;
    private EditText email;
    private EditText password;

    private String fName;
    private String lName;
    private String eMail;
    private String pWord;

    private Button nextStep;
    private Button submit;

    private TextView registerStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Register to Commuter");

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                }

                else {

                }
            }
        };

        database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();


        firstName = (EditText) findViewById(R.id.fname_register);
        lastName = (EditText) findViewById(R.id.lname_register);
        nextStep = (Button) findViewById(R.id.next_step);

        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_register_second);

                email = (EditText) findViewById(R.id.email_register);
                password = (EditText) findViewById(R.id.password_register);
                submit = (Button) findViewById(R.id.submit);
                registerStatus = (TextView) findViewById(R.id.login_status_text);

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fName = firstName.getText().toString();
                        lName = lastName.getText().toString();
                        eMail = email.getText().toString();
                        pWord = password.getText().toString();

                        try
                        {
                            mAuth.createUserWithEmailAndPassword(eMail, pWord).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(!task.isSuccessful()) {
                                        Toast.makeText(Register.this, "Account creation - failed", Toast.LENGTH_SHORT).show();
                                        registerStatus.setText("Account creation failed. Please try again later.");
                                    }

                                    else {

                                        myRef.child("users").child(mAuth.getCurrentUser().getUid()).setValue(new User(fName, lName, eMail));

                                        //account creation success, redirect to main activity
                                        //Toast.makeText(Register.this, "Account creation - success", Toast.LENGTH_SHORT).show();
                                        Intent mapsIntent = new Intent();
                                        mapsIntent.setClass(getBaseContext(), MainActivity.class);
                                        startActivity(mapsIntent);
                                    }
                                }
                            });
                        }

                        catch (Exception ex)
                        {
                            //Toast.makeText(Register.this, "Account creation - failed", Toast.LENGTH_SHORT).show();
                            registerStatus.setText("Account creation failed. Please try again later.");
                        }
                    }
                });

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
