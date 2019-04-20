package com.example.dell.firebasephoneauthexample;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dell.firebasephoneauthexample.smsmodule.SMSActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity {
    EditText etname, etaddress, etphonenumber, etoccupation, etbankname, etbankbranch;
    Button buttonsubmit;
    String id;

    private DatabaseReference dbRef;
    private FirebaseAuth fbAuth;



    Patterns patterns;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        dbRef = FirebaseDatabase.getInstance().getReference("User");

        etname = findViewById(R.id.etName);
        etaddress = findViewById(R.id.etAddress);
        etphonenumber = findViewById(R.id.etPhoneNumber);
        etoccupation = findViewById(R.id.etOccupation);
        etbankname = findViewById(R.id.etBankName);
        etbankbranch = findViewById(R.id.etbankBranch);
        buttonsubmit = findViewById(R.id.ButtonSubmit);



        buttonsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                startActivity(new Intent(Profile.this, SMSActivity.class));
            }
        });


    }

    protected void saveData() {
        String name = etname.getText().toString();
        String address = etaddress.getText().toString();
        String phonenumber = etphonenumber.getText().toString();
        String occupation = etoccupation.getText().toString();
        String bankname = etbankname.getText().toString();
        String bankbranch = etbankbranch.getText().toString();

        String id = dbRef.push().getKey();


        @SuppressLint("RestrictedApi")

        User user = new User( id,name,address,phonenumber,occupation,bankname,bankbranch);
        dbRef.child(id).setValue(user);
        Log.d("Profile","Saved Successfully!");



        Toast.makeText(this, "data saved successfully", Toast.LENGTH_SHORT).show();

    }
}
