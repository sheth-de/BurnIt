package com.example.projectteam23mobiledev;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StartRunActivity extends AppCompatActivity {

    Button btnStartRun;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start_run);

        btnStartRun = findViewById(R.id.btn_start_run);

        btnStartRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(StartRunActivity.this, RunActivity.class));
                //TODO create RunActivity.java and uncomment above line
            }
        });
    }
}
