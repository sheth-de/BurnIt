package com.example.projectteam23mobiledev;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChallengesMainPage extends AppCompatActivity {

    private Button btnCreateChallenge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges_main_page);

        btnCreateChallenge = findViewById(R.id.btnCreateChallenge);
        btnCreateChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CreateChallenge.class);
                startActivity(intent);
            }
        });
    }
}