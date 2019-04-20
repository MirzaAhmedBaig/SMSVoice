package com.example.dell.firebasephoneauthexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    private EditText editTextMobile;
    Button register;
    FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fbAuth = FirebaseAuth.getInstance();

        editTextMobile =findViewById(R.id.editTextMobile);


            register = findViewById(R.id.register);


            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, Register.class));
                }
            });


            findViewById(R.id.buttonContinue).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String mobile = editTextMobile.getText().toString().trim();

                    if (mobile.isEmpty() || mobile.length() < 10) {
                        editTextMobile.setError("Enter a valid mobile");
                        editTextMobile.requestFocus();
                        return;
                    } else {


                        Intent intent = new Intent(MainActivity.this, VerifyPhone.class);
                        intent.putExtra("mobile", mobile);
                        startActivity(intent);
                    }

                }


            });


    }

    @Override
    protected void onStart() {
        super.onStart();
/*
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(this, Profile.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }*/
    }
}
