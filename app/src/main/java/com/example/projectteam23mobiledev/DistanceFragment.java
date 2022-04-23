package com.example.projectteam23mobiledev;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projectteam23mobiledev.Models.Challenge;
import com.example.projectteam23mobiledev.Utilities.Enums.StatusEnum;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DistanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DistanceFragment<btnCreateDistanceChallenge> extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    FirebaseAuth mAuth;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button btnCreateDistanceChallenge;
    EditText distanceEnterDistance;
    EditText distanceMinPoints;
    EditText distanceAddUsers;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DistanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DistanceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DistanceFragment newInstance(String param1, String param2) {
        DistanceFragment fragment = new DistanceFragment();
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_distance, container, false);
        distanceEnterDistance = v.findViewById(R.id.distanceEnterDistance);
        distanceMinPoints = v.findViewById(R.id.distanceMinPoints);
        distanceAddUsers = v.findViewById(R.id.distanceAddUsers);
        btnCreateDistanceChallenge = v.findViewById(R.id.btnCreateDistanceChallenge);
        btnCreateDistanceChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Double  distance =0.0;
                Integer minPoints =0;
                Date d = new Date();
                d.getTime();
                try{
                    distance = Double.parseDouble(distanceEnterDistance.getText().toString());
                }
                catch (Exception e){
                    Toast.makeText(getActivity(), "Enter Valid distance", Toast.LENGTH_SHORT).show();
                    return;
                }
                try{
                     minPoints = Integer.parseInt(distanceMinPoints.getText().toString());
                }
                catch (Exception e){
                    Toast.makeText(getActivity(), "Enter Valid Points", Toast.LENGTH_SHORT).show();
                    return;
                }
                String userAdded = distanceAddUsers.getText().toString();
                Query query = db.collection("users").whereEqualTo("email", userAdded);
                Double finalDistance = distance;
                Integer finalMinPoints = minPoints;
                Integer finalMinPoints1 = minPoints;
                final Boolean[] flag = {false};
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                      @Override
                                                      public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            for (DocumentSnapshot document : task.getResult()) {
                                                                if (document.exists()) {
                                                                    Challenge challenge = new Challenge("distance",
                                                                            finalDistance, 0.0,
                                                                            userAdded,
                                                                            mAuth.getCurrentUser().getEmail().toString(),
                                                                            finalMinPoints,
                                                                            d.getTime(),
                                                                            finalMinPoints1,
                                                                            "open",
                                                                            StatusEnum.ACCEPTED,
                                                                            StatusEnum.OPEN
                                                                    );
                                                                    db.collection("challenges")
                                                                            .add(challenge)
                                                                            .addOnSuccessListener(documentReference -> {
                                                                            })
                                                                            .addOnFailureListener(exception -> {
                                                                                Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                                                                            });
                                                                    flag[0] =true;
                                                                    Toast.makeText(getActivity(), "Challenge created successfully", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                                                    startActivity(intent);

                                                                } else {

                                                                }
                                                            }
                                                            if(flag[0]==false){
                                                                Toast.makeText(getActivity(), "Enter valid user email", Toast.LENGTH_SHORT).show();
                                                            }
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