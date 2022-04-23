package com.example.projectteam23mobiledev;

import static android.content.ContentValues.TAG;

import static com.example.projectteam23mobiledev.Utilities.Constants.KEY_EMAIL;
import static com.example.projectteam23mobiledev.Utilities.Constants.KEY_UID;
import static com.example.projectteam23mobiledev.Utilities.Constants.KEY_WALLET;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText username_edtTxt;
    private Button btnLogin;
    private static final String PASS = "Pass@123!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        username_edtTxt = (EditText) findViewById(R.id.usrname_edTxt);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        // validate username

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = username_edtTxt.getText().toString();
                LoginUI(username);
            }
        });




    }

    private void LoginUI(String username) {
        if (username.equals("")) {

            Toast.makeText(LoginActivity.this, "Enter a valid username.",
                    Toast.LENGTH_SHORT).show();
        } else {

            mAuth.signInWithEmailAndPassword(username, PASS)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {

                                createUser(username);
                            }
                        }
                    });


        }
    }

    private void createUser(String username) {
        mAuth.createUserWithEmailAndPassword(username, PASS)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            Map<String, Object> data = new HashMap<>();
                            data.put(KEY_UID, user.getUid().toString());
                            data.put(KEY_EMAIL, user.getEmail().toString());
                            data.put(KEY_WALLET, 1000);


                            db.collection("users")
                                    .add(data)
                                    .addOnSuccessListener(documentReference -> {
//                                        Toast.makeText(getApplicationContext(), "Data Inserted", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(exception -> {
                                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                                    });

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

}