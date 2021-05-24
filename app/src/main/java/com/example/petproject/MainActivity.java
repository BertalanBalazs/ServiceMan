package com.example.petproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    Button signout;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signout = findViewById(R.id.sign_out);

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_out:
                        signOut();
                        break;
                }
            }
        });


        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/2888182671462329?access_token=EAADeAgUbPCQBAFnBjed9k4eID3IzqaZA1mQZCgFjbKZAgk8BAZCZATxFo995kOxlftRbpLLs8UngVFp585n6UYPGnmZAVUf3MPmKwRtypReJzzltrV5UUyFkW9AqN7audWfdx9bzvhf7ZBiylfh5BB4vZCi29FxJSfKusZBdfLVYD4ljaMjKhgYKBnyTBPEmZBhnA5ORdD0JSkJwZDZD",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        TextView eventData = findViewById(R.id.eventData);
                        final JSONObject data = response.getJSONObject();
                        String relelvantText = (data == null ? null : data.optString("id"));
                        eventData.setText(relelvantText);
                    }
                }
        ).executeAsync();


        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
        }
    }


    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "Hová mész kisköcsög nem tetszik az appom?", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}
