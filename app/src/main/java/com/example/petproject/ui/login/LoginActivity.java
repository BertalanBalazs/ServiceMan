package com.example.petproject.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petproject.MainActivity;
import com.example.petproject.NavigationMenuActivity;

import com.facebook.AccessToken;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.example.petproject.R;
import com.example.petproject.RegistrationActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    private SignInButton googleLoginInButton;
    private LoginButton fbLoginButton;
    private static final String EMAIL = "email";
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 0;
    private EditText email, password;
    private Button loginButton;
    private ProgressBar loadingProgressBar;
    private TextView registerUser;
    FirebaseAuth firebaseAuth;
    private CallbackManager mcallbackManager;
    private String TAG = "TAG";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_mail);
        password = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login);
        fbLoginButton = (LoginButton) findViewById(R.id.login_button);
        googleLoginInButton = findViewById(R.id.sign_in_button);
        loadingProgressBar = findViewById(R.id.login_loading);
        registerUser = findViewById(R.id.not_registered);
        firebaseAuth = FirebaseAuth.getInstance();
        mcallbackManager = CallbackManager.Factory.create();


        //// BASIC LOGIN
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
            }
        });

        //// BASIC REGISTRATION
        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI(RegistrationActivity.class);
            }
        });


        //// GOOGLE LOGIN
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleLoginInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.sign_in_button) {
                    signIn();
                }
            }
        });


        // Initialize Facebook Login button
        fbLoginButton.setReadPermissions("email", "public_profile");
        fbLoginButton.registerCallback(mcallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                updateUI(NavigationMenuActivity.class);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, "Authentication SUCCES.",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "signInWithCredential:success");
                            updateUI(NavigationMenuActivity.class);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        mcallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void startLogin(){
        String mEmail = this.email.getText().toString().trim();
        String mPassword = password.getText().toString().trim();

        if (TextUtils.isEmpty(mEmail)) {
            email.setError("Email is Required");
            return;
        }
        if (password.length() <6) {
            password.setError("Password Must be >= 6 Characters");
            return;
        }

        loginButton.setVisibility((View.GONE));
        loadingProgressBar.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Logged in Succesfully!", Toast.LENGTH_SHORT).show();
                    updateUI(MainActivity.class);
                } else {
                    Toast.makeText(LoginActivity.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    loadingProgressBar.setVisibility(View.GONE);
                    loginButton.setVisibility((View.VISIBLE));
                }
            }
        });
    }



    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(MainActivity.class);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("error", "signInResult:failed code=" + e.getStatusCode());
            updateUI(RegistrationActivity.class);
        }
    }

    private void updateUI(Class<?> Class) {
        Intent registerIntent = new Intent(LoginActivity.this, Class);
        startActivity(registerIntent);
    }
}
