package com.example.projectteam23mobiledev;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView usr;
    private Button btnGoToChallengeTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        usr = (TextView) findViewById(R.id.usr);

        if (mAuth.getCurrentUser() != null) {
            String em = mAuth.getCurrentUser().getEmail();
            usr.setText("Hi "+em.substring(0, em.indexOf('@'))+ "!");
        }
        btnGoToChallengeTemp = findViewById(R.id.btnGoToChallengeTemp);
        btnGoToChallengeTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChallengesMainPage.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String email = user.getEmail();
            String uid = user.getUid();
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }
}