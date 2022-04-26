package com.example.projectteam23mobiledev;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;

public class ProfileFragment extends Fragment {
    private Button btnLogOut;
    private TextView userEmail;
    private FirebaseAuth mAuth;
    private TextView textProfileMiles;
    private TextView textProfileAverageSpeed;
    private TextView textProfileTotalChallenges;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        btnLogOut = v.findViewById(R.id.btnLogOut);
        userEmail = v.findViewById(R.id.userEmail);
        textProfileMiles = v.findViewById(R.id.textProfileMiles);
        textProfileAverageSpeed = v.findViewById(R.id.textProfileAverageSpeed);
        textProfileTotalChallenges = v.findViewById(R.id.textProfileTotalChallenges);

        final Double[] totalDistance = {0.0};
        final Double[] count = {0.0};
        final Double[] totalSpeed = {0.0};
        final Integer[] challenges = {0};
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        userEmail.setText(email.toString());
        db.collection("runstats").whereEqualTo("user", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Double distance = Double.parseDouble(document.getData().get("distance").toString());
                                Double speed = Double.parseDouble(document.getData().get("speed").toString());
                                totalDistance[0] += distance;
                                totalSpeed[0] += speed;
                                count[0]+=1;
                            }
                            textProfileMiles.setText(df.format(totalDistance[0]));
                            if(totalSpeed[0]>0){
                                String avgSpeed = df.format(totalSpeed[0]/count[0]);
                                textProfileAverageSpeed.setText(avgSpeed);
                            }
                            else {
                                textProfileAverageSpeed.setText("0.00");
                            }
                        }
                    }
                }
        );

        db.collection("challenges").whereEqualTo("sender", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            challenges[0] += task.getResult().size();
                        }
                    }
                });
        db.collection("challenges").whereEqualTo("receiver", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            challenges[0] += task.getResult().size();
                            textProfileTotalChallenges.setText(challenges[0].toString());
                        }
                    }
                });
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
            }
        });
        return v;
    }
}