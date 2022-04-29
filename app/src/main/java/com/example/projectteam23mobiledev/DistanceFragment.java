package com.example.projectteam23mobiledev;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectteam23mobiledev.Models.Challenge;
import com.example.projectteam23mobiledev.Utilities.Enums.StatusEnum;
import com.example.projectteam23mobiledev.ViewModels.BottomNavViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DistanceFragment<btnCreateDistanceChallenge> extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    FirebaseAuth mAuth;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    long walletBalance = 0;
    Spinner spinner_distance_miles;
    Spinner spinner_distance_min_points;
    Spinner spinner_distance_add_users;
    Button btnCreateDistanceChallenge;
    EditText distanceAddUsers;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private BottomNavViewModel bottomNavViewModel;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DistanceFragment(BottomNavViewModel bottomNavViewModel) {
        this.bottomNavViewModel=bottomNavViewModel;
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DistanceFragment.
     */
    // TODO: Rename and change types and number of parameters


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
        spinner_distance_miles= v.findViewById(R.id.spinner_distance_miles);
        spinner_distance_min_points= v.findViewById(R.id.spinner_distance_min_points);
        List<String> dist_items = new ArrayList<String>();
        dist_items.add("1");
        dist_items.add("2");
        dist_items.add("3");
        dist_items.add("5");
        dist_items.add("7");
        dist_items.add("9");
        dist_items.add("10");
        ArrayAdapter<String> mSortAdapter1 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, dist_items);
        mSortAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_distance_miles.setAdapter(mSortAdapter1);

        List<String> min_points = new ArrayList<>();
        min_points.add("20");
        min_points.add("50");
        min_points.add("100");
        min_points.add("150");
        min_points.add("200");
        min_points.add("300");
        min_points.add("400");
        List<String> updated_list =  new ArrayList<>();
        Query findWallect = db.collection("users").whereEqualTo("email",mAuth.getCurrentUser().getEmail().toString());
        findWallect.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    walletBalance = (long) task.getResult().getDocuments().get(0).get("wallet");
                        for(int i=0; i<min_points.size();i++){
                            if(Integer.parseInt(min_points.get(i))<=walletBalance){
                                updated_list.add(min_points.get(i));
                            }
                        }
                        if(updated_list.size()==0){
                            updated_list.add("0");
                        }
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, updated_list);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_distance_min_points.setAdapter(adapter2);
                }
                else{
                    updated_list.add("0");
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, updated_list);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_distance_min_points.setAdapter(adapter2);
                }
            }
        });

        spinner_distance_add_users = v.findViewById(R.id.spinner_distance_add_users);
        List<String> listOfUsers = new ArrayList<>();
        Query findUsers = db.collection("users").whereNotEqualTo("email",mAuth.getCurrentUser().getEmail());
        findUsers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        listOfUsers.add(document.getData().get("email").toString());
                    }
                    ArrayAdapter<String> adapter3 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listOfUsers);
                    adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_distance_add_users.setAdapter(adapter3);
                }
                else{
                    Toast.makeText(getActivity(), "Cannot fetch data, some problem occured", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });





        btnCreateDistanceChallenge = v.findViewById(R.id.btnCreateDistanceChallenge);
        btnCreateDistanceChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Integer  distance =0;
                Integer minPoints =0;
                Date d = new Date();
                d.getTime();
                try{
                    distance = Integer.parseInt(spinner_distance_miles.getSelectedItem().toString());
                }
                catch (Exception e){
                    Toast.makeText(getActivity(), "Enter Valid distance", Toast.LENGTH_SHORT).show();
                    return;
                }
                try{
                     minPoints = Integer.parseInt(spinner_distance_min_points.getSelectedItem().toString());
                }
                catch (Exception e){
                    Toast.makeText(getActivity(), "Enter Valid Points", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(minPoints==0){
                    Toast.makeText(getActivity(), "Not enough points to create a challenge!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String userAdded = spinner_distance_add_users.getSelectedItem().toString();
                Query query = db.collection("users").whereEqualTo("email", userAdded);
                Integer finalDistance = distance;
                Integer finalMinPoints = minPoints;
                Integer finalMinPoints1 = minPoints;
                String currEmail = mAuth.getCurrentUser().getEmail();
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                                      @Override
                                                      public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            for (DocumentSnapshot document : task.getResult()) {
                                                                if (document.exists()) {
                                                                    Challenge challenge = new Challenge("distance",
                                                                            finalDistance, 0,
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
                                                                                                wallBal -= minPoint;
                                                                                                // deduct points from wallet
                                                                                                db.collection("users").document(document.getId())
                                                                                                        .update(
                                                                                                                "wallet", wallBal
                                                                                                        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @SuppressLint("ResourceType")
                                                                                                    @Override
                                                                                                    public void onSuccess(Void unused) {
                                                                                                        db.collection("challenges")
                                                                                                                .add(challenge)
                                                                                                                .addOnSuccessListener(documentReference -> {
                                                                                                                    Toast.makeText(getActivity(), "Challenge created successfully", Toast.LENGTH_SHORT).show();
                                                                                                                    Intent intent = new Intent(getActivity(),MainActivity.class);
                                                                                                                    intent.putExtra("challengeScreen",true);
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
                                                                            });


                                                                    //


                                                                } else {

                                                                }
                                                            }
//
//                                                            }
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