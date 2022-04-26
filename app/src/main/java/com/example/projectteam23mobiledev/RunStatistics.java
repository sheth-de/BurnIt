package com.example.projectteam23mobiledev;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectteam23mobiledev.Models.RunModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class RunStatistics extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_stats);

        TextView distance_text_stats = (TextView) findViewById(R.id.distance_text_stats);
        TextView distance_value_stats = (TextView) findViewById(R.id.distance_value_stats);
        TextView time_text_stats = (TextView) findViewById(R.id.time_text_stats);
        TextView time_value_stats = (TextView) findViewById(R.id.time_value_stats);

        TextView pace_text_stats = (TextView) findViewById(R.id.pace_text_stats);
        TextView pace_value_stats = (TextView) findViewById(R.id.pace_value_stats);
        TextView steps_text_stats = (TextView) findViewById(R.id.steps_text_stats);
        TextView steps_value_stats = (TextView) findViewById(R.id.steps_value_stats);

        TextView calories_value_stats = (TextView) findViewById(R.id.calories_value_stats);

        mAuth = FirebaseAuth.getInstance();

        String currEmail  = mAuth.getCurrentUser().getEmail();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("runstats")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userEmail = (String) document.getData().get("user");
                                if (userEmail.equals(currEmail)) {
                                    RunModel runModel = (RunModel) getIntent().getSerializableExtra("run");

                                    double distance_val = runModel.getDistance();
                                    //double distance_val = Double.parseDouble(document.getData().get("distance").toString());
                                    distance_value_stats.setText(String.format("%.3f", distance_val));

                                    int steps = runModel.getSteps();
                                    steps_value_stats.setText(String.valueOf(steps));

                                   // int seconds = Integer.parseInt(document.getData().get("seconds").toString());
                                    int seconds = (int) runModel.getSeconds();
                                    int p1 = seconds % 60;
                                    int p2 = seconds / 60;
                                    int p3 = p2 % 60;
                                    p2 = p2 / 60;

                                    time_value_stats.setText( p2 + ":" + p3 + ":" + p1);


                                    double pace_val = runModel.getSpeed();
                                    //double pace_val = Double.parseDouble(document.getData().get("speed").toString());
                                    pace_value_stats.setText(String.format("%.2f", pace_val));

                                    double calories_val = runModel.getCalories();
                                    //double pace_val = Double.parseDouble(document.getData().get("speed").toString());
                                    calories_value_stats.setText(String.format("%.2f",calories_val));

                                }

                            }


                        }
                    }
                });

    }

//    public void onBackPressed()
//    {
//        //do whatever you want the 'Back' button to do
//        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
//        this.startActivityFromFragment();
//        //this.startActivit(new Intent(RunStatistics.this,StartRunActivity.class));
//
//        return;
//    }

}