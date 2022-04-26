package com.example.projectteam23mobiledev;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.projectteam23mobiledev.Models.Challenge;
import com.example.projectteam23mobiledev.Utilities.Enums.StatusEnum;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    FirebaseAuth mAuth;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button btnCreateTimeChallenge;
    EditText timeEnterTime;
    EditText timeMinPoints;
    EditText timeAddUsers;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TimeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimeFragment newInstance(String param1, String param2) {
        TimeFragment fragment = new TimeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_time, container, false);
        // Inflate the layout for this fragment
        timeEnterTime = v.findViewById(R.id.timeEnterTime);
        timeMinPoints = v.findViewById(R.id.timeMinPoints);
        timeAddUsers = v.findViewById(R.id.timeAddUsers);
        btnCreateTimeChallenge = v.findViewById(R.id.btnCreateTimeChallenge);
        btnCreateTimeChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Double  time =0.0;
                Integer minPoints =0;
                Date d = new Date();
                d.getTime();
                try{
                    time = Double.parseDouble(timeEnterTime.getText().toString());
                }
                catch (Exception e){
                    Toast.makeText(getActivity(), "Enter Valid Time in Minutes", Toast.LENGTH_SHORT).show();
                    return;
                }
                try{
                    minPoints = Integer.parseInt(timeMinPoints.getText().toString());
                }
                catch (Exception e){
                    Toast.makeText(getActivity(), "Enter Valid Points", Toast.LENGTH_SHORT).show();
                    return;
                }
                String userAdded = timeAddUsers.getText().toString();
                Query query = db.collection("users").whereEqualTo("email", userAdded);
                Double finalTime = time;
                Integer finalMinPoints = minPoints;
                Integer finalMinPoints1 = minPoints;
                final Boolean[] flag = {false};
                String currEmail = mAuth.getCurrentUser().getEmail();
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    Challenge challenge = new Challenge("time",
                                            0.0,
                                            finalTime,
                                            userAdded,
                                            mAuth.getCurrentUser().getEmail().toString(),
                                            finalMinPoints,
                                            d.getTime(),
                                            finalMinPoints1,
                                            "open",
                                            StatusEnum.ACCEPTED,
                                            StatusEnum.OPEN
                                    );

                                    // deduct points from sender of the challenger wallet
                                    db.collection("users")
                                            .whereEqualTo("email", currEmail)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            Long wallBal = (Long) document.getData().get("wallet");
                                                            Challenge c = challenge;
                                                            int minPoint = c.getMinPoints();
                                                            if (wallBal > minPoint) {
                                                                // can play
                                                                wallBal -= minPoint;

                                                                // deduct points from wallet
                                                                db.collection("users").document(document.getId())
                                                                        .update(
                                                                                "wallet", wallBal
                                                                        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        db.collection("challenges")
                                                                                .add(challenge)
                                                                                .addOnSuccessListener(documentReference -> {
                                                                                    flag[0] =true;
                                                                                    Toast.makeText(getActivity(), "Challenge created successfully", Toast.LENGTH_SHORT).show();
                                                                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                                                                    startActivity(intent);
                                                                                })
                                                                                .addOnFailureListener(exception -> {
                                                                                    Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                });

                                                                    }
                                                                });



                                                            }
                                                        }
                                                    }
                                                }
                                            });

                                } else {

                                }
                            }
//                            if(flag[0]==false){
//                                Toast.makeText(getActivity(), "Enter valid user email", Toast.LENGTH_SHORT).show();
//                            }
                        }
                        else {
                            Toast.makeText(getActivity(), "Some error occurred!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return v;
    }
}