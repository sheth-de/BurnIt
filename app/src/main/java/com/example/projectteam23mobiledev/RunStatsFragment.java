package com.example.projectteam23mobiledev;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.projectteam23mobiledev.Models.Challenge;
import com.example.projectteam23mobiledev.Models.RunModel;
import com.example.projectteam23mobiledev.ViewModels.BottomNavViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class RunStatsFragment extends Fragment {
    private BottomNavViewModel bottomNavViewModel;
    public RunStatsFragment(BottomNavViewModel bottomNavViewModel) {
        this.bottomNavViewModel = bottomNavViewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run_stats, container, false);

        TextView distance_value_stats = view.findViewById(R.id.distance_value);
        TextView time_value_stats = view.findViewById(R.id.time_value);
        TextView pace_value_stats = view.findViewById(R.id.pace_value);
        TextView steps_value_stats = view.findViewById(R.id.steps_value);
        TextView calories_value_stats = view.findViewById(R.id.calories_value);

        TextView challenge_details = view.findViewById(R.id.txt_ch_details);
        TextView wallet_details = view.findViewById(R.id.txt_wallet_details);

        String currEmail  = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        Bundle bundle = this.getArguments();
        RunModel runModel;
        if (bundle != null) {
            runModel  = (RunModel) bundle.getSerializable("runStats");
//            Toast.makeText(getContext(), challengeId, Toast.LENGTH_SHORT).show();
        } else {

            return view;
        }

        double distance_val = runModel.getDistance();
        //double distance_val = Double.parseDouble(document.getData().get("distance").toString());
        distance_value_stats.setText(String.format("%.2f", distance_val));

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


        DocumentReference docRef = db.collection("challenges").document(runModel.getChallengeId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        final String line1;
                        final String line2;
                        Challenge challenge = document.toObject(Challenge.class);
                        String ch_snd_status = (String) document.getData().get("sendStatus");
                        String ch_rcv_status = (String) document.getData().get("receiverStatus");

                        // who vs who details
                        if (currEmail.equals(challenge.getSender())){
                            String rcvId = challenge.getReceiver();
                            line1 = String.format("You VS %s\n", rcvId.substring(0, rcvId.indexOf('@')));

                            if (challenge.getStatus().equals("ongoing")) {
                                line2 = String.format("%s is yet to complete the challenge.\nKeep an eye out for more updates!",
                                        rcvId.substring(0, rcvId.indexOf('@')));
                            } else {
                                if(challenge.getStatus().equals("rcv")) {
                                    line2 = String.format("You lost the challenge and %d is deducted from your wallet",
                                            challenge.getMinPoints());
                                } else {
                                    line2 = String.format("You won the challenge and %d is added to your wallet",
                                            challenge.getMinPoints());
                                }
                            }
                        } else {
                            String sndId = challenge.getSender();
                            line1 = String.format("You VS %s\n", sndId.substring(0, sndId.indexOf('@')));

                            if (challenge.getStatus().equals("ongoing")) {
                                line2 = String.format("%s is yet to complete the challenge.\nKeep an eye out for more updates!",
                                        sndId.substring(0, sndId.indexOf('@')));
                            } else {
                                if(challenge.getStatus().equals("snd")) {
                                    line2 = String.format("You lost the challenge and %d is deducted from your wallet",
                                            challenge.getMinPoints());
                                } else {
                                    line2 = String.format("You won the challenge and %d is added to your wallet",
                                            challenge.getMinPoints());
                                }
                            }
                        }

                        challenge_details.setText(line1+line2);





                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });



        db.collection("users")
                .whereEqualTo("email", currEmail)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Long wall = (Long) document.getData().get("wallet");

                                String wallBal = String.format("%d XP", wall);
                                wallet_details.setText(wallBal);
                                break;
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                bottomNavViewModel.select(1);
            }
        });
        return view;
    }

    private String getResult() {
        return "";
    }

}
