package com.example.petproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.petproject.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    EditText username, email, password, passwordVerify, phone;
    Button registerButton;
    ProgressBar loadingProgressBar;
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    FirebaseFirestore db;
    private String userID;
    private String TAG = "LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        passwordVerify = findViewById(R.id.password_verify);
        phone = findViewById(R.id.mobile_number);
        registerButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewAccount();
            }
        });

    }


    private void registerNewAccount() {

        final String mEmail = this.email.getText().toString().trim();
        final String mPassword = this.password.getText().toString().trim();
        final String mVerifyPassword = this.passwordVerify.getText().toString().trim();
        final String mPhone = this.phone.getText().toString().trim();
        final String mUsername = this.username.getText().toString().trim();

        if (TextUtils.isEmpty(mEmail)) {
            email.setError("Email is Required");
            return;
        }
        if (password.length() <6) {
            password.setError("Password Must be >= 6 Characters");
            return;
        }
        if (!mVerifyPassword.equals(mPassword)) {
            passwordVerify.setError("Passwords not equals!");
        }

        loadingProgressBar.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.GONE);

        firebaseAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegistrationActivity.this, "User Created!", Toast.LENGTH_SHORT).show();
                    userID = firebaseAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = db.collection("users").document(userID);
                    Map<String, Object> user = new HashMap<>();
                    user.put("username", mUsername);
                    user.put("emal", mEmail);
                    user.put("phone", mPhone);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG,"onSucces: User profle created for:"+ userID);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG,"onFailure"+ e.toString());
                        }
                    });
                    updateUI(LoginActivity.class);
                } else {
                    Toast.makeText(RegistrationActivity.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    loadingProgressBar.setVisibility(View.GONE);
                    registerButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void updateUI(Class<?> Class) {
        Intent registerIntent = new Intent(RegistrationActivity.this, Class);
        startActivity(registerIntent);
    }
}


