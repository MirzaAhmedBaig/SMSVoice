package com.example.dell.firebasephoneauthexample;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {
    private String mVerificationId;
    private EditText PhoneNumber;
    private FirebaseAuth mAuth;
    Button btnregister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        PhoneNumber = findViewById(R.id.etPhoneNumber);

        Intent intent = getIntent();
        String Phone = intent.getStringExtra("phone");
        sendVerificationCode(Phone);
        btnregister = findViewById(R.id.ButtonRegister);
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Phone = PhoneNumber.getText().toString().trim();

                if (Phone.isEmpty() || Phone.length() < 10) {
                    PhoneNumber.setError("Enter a valid mobile");
                    PhoneNumber.requestFocus();
                    return;

                }

                startActivity(new Intent(Register.this,MainActivity.class));
                Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_SHORT).show();

               /* verifyVerificationCode(Phone);

                Intent intent = new Intent(Register.this, SMSActivity.class);
                intent.putExtra("mobile", Phone);
                startActivity(intent);*/
            }

        });
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();

            if (code != null) {
                PhoneNumber.setText(code);
                verifyVerificationCode(code);
            }else {
                Toast.makeText(Register.this, "Couldn't Registered", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            /*Intent intent = new Intent(Register.this, SMSActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);*/
                            startActivity(new Intent(Register.this,MainActivity.class));

                        } else {


                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }


                        }
                    }
                });
    }
}



