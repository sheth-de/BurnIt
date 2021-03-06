package com.example.projectteam23mobiledev;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.projectteam23mobiledev.Adapters.ChallengeCardAdapter;
import com.example.projectteam23mobiledev.Models.Challenge;
import com.example.projectteam23mobiledev.Models.ChallengeCardModel;
import com.example.projectteam23mobiledev.Utilities.Enums.StatusEnum;
import com.example.projectteam23mobiledev.ViewModels.BottomNavViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ChallengeFragment extends Fragment {

    private ViewPager openChViewPager;
    private ViewPager ongoingChViewPager;
    private ViewPager pastChViewPager;
    private ArrayList<ChallengeCardModel> modelArrayList;
    private ArrayList<ChallengeCardModel> ongoingModelArrayList;
    private ArrayList<ChallengeCardModel> pastModelArrayList;
    private ChallengeCardAdapter challengeCardAdapter;
    private ChallengeCardAdapter onGoingChallengeCardAdapter;
    private ChallengeCardAdapter pastChallengeCardAdapter;
    private Button fragmentBtnCreateChallenge;
    private TextView txt_open, txt_ongoing, txt_past;

    FirebaseAuth mAuth;
    private BottomNavViewModel bottomNavViewModel;

    public ChallengeFragment(BottomNavViewModel bottomNavViewModel) {
        this.bottomNavViewModel = bottomNavViewModel;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_challenge, container, false);
        fragmentBtnCreateChallenge = view.findViewById(R.id.fragmentBtnCreateChallenge);
        fragmentBtnCreateChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateChallenge.class);
//                intent.putExtra("Bottom_Navigation_View_Model", (Parcelable) bottomNavViewModel);
                startActivity(intent);
            }
        });


        mAuth = FirebaseAuth.getInstance();
        String currEmail  = mAuth.getCurrentUser().getEmail();


        openChViewPager = view.findViewById(R.id.vp_open_ch);
        txt_open = view.findViewById(R.id.txt_open_chl);
        openChViewPager.setVisibility(View.GONE);
        txt_open.setVisibility(View.GONE);
        modelArrayList = new ArrayList<>();

        ongoingChViewPager = view.findViewById(R.id.vp_ongoing_ch);
        txt_ongoing = view.findViewById(R.id.txt_ongoing_chl);
        ongoingChViewPager.setVisibility(View.GONE);
        txt_ongoing.setVisibility(View.GONE);
        ongoingModelArrayList = new ArrayList<>();

        pastChViewPager = view.findViewById(R.id.vp_past_ch);
        txt_past = view.findViewById(R.id.txt_past_chl);
        pastChViewPager.setVisibility(View.GONE);
        txt_past.setVisibility(View.GONE);
        pastModelArrayList = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("challenges")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        String rcv = "";
                        String snd = "";
                        String docId ="";
//                        int lim = snapshots.getDocumentChanges().size();
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {

                            QueryDocumentSnapshot document = dc.getDocument();

                            Challenge challenge = document.toObject(Challenge.class);
                            String sndId = (String) document.getData().get("sender");
                            String rcvId = (String) document.getData().get("receiver");
                            String minPoints = String.valueOf(document.getData().get("minPoints"));
                            String type = (String) document.getData().get("type");
                            Long ch_date = (Long) document.getData().get("timeStamp");
                            String ch_status = (String) document.getData().get("status");
                            String ch_snd_status = (String) document.getData().get("sendStatus");
                            String ch_rcv_status = (String) document.getData().get("receiverStatus");


                            String challengeTitle = String.format("%s based", type);
                            String details = "";
                            if (type.equals("distance")) {
                                details = String.format("%d miles - %s XP ", challenge.getDistance(), minPoints);
                            } else {
                                details = String.format("%d min - %s XP ", challenge.getTime(), minPoints);
                            }

                            switch (dc.getType()) {
                                case ADDED:




//                                    String daysAgo = String.valueOf(new Date(ch_date));


                                    switch (ch_status) {
                                        case "open":
                                            if (sndId.equals(currEmail)) {
                                                continue;
                                            }
                                            modelArrayList.add(new ChallengeCardModel(challengeTitle,
                                                details,
                                                ch_date,
                                                sndId.substring(0, sndId.indexOf('@')),
                                                    document.getId(),
                                                    challenge));
                                            break;
                                        case "ongoing":
                                            if ((currEmail.equals(sndId) && ch_snd_status.equals(StatusEnum.ACCEPTED.toString()))||
                                                    (currEmail.equals(rcvId) && ch_rcv_status.equals(StatusEnum.ACCEPTED.toString()))) {
                                                ongoingModelArrayList.add(new ChallengeCardModel(challengeTitle,
                                                        details,
                                                        ch_date,
                                                        sndId.substring(0, sndId.indexOf('@')),
                                                        document.getId(),
                                                        challenge));
                                            }
                                            break;
                                        case "snd":
                                        case "rcv":
                                        case "closed":
                                            pastModelArrayList.add(new ChallengeCardModel(challengeTitle,
                                                    details,
                                                    ch_date,
                                                    sndId.substring(0, sndId.indexOf('@')),
                                                    document.getId(),
                                                    challenge));
                                            break;

                                        default: break;


                                    }
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());

//                                    String status = (String) dc.getDocument().getData().get("status");
                                    switch (ch_status){
                                        case "snd":
                                        case "rcv":
                                        case "closed":
                                            pastModelArrayList.add(new ChallengeCardModel(challengeTitle,
                                                    details,
                                                    ch_date,
                                                    sndId.substring(0, sndId.indexOf('@')),
                                                    document.getId(),
                                                    challenge));
                                            modelArrayList.removeIf(c -> c.getChallengeId()
                                                    .equals(dc.getDocument().getId()));
                                            ongoingModelArrayList.removeIf(c -> c.getChallengeId()
                                                    .equals(dc.getDocument().getId()));
                                            break;
                                        case "ongoing":
                                            modelArrayList.removeIf(c -> c.getChallengeId()
                                                    .equals(dc.getDocument().getId()));
                                            ongoingModelArrayList.removeIf(c -> c.getChallengeId()
                                                    .equals(dc.getDocument().getId()));
                                            if ((currEmail.equals(sndId) && ch_snd_status.equals(StatusEnum.ACCEPTED.toString()))||
                                                    (currEmail.equals(rcvId) && ch_rcv_status.equals(StatusEnum.ACCEPTED.toString()))) {
                                                ongoingModelArrayList.add(new ChallengeCardModel(challengeTitle,
                                                        details,
                                                        ch_date,
                                                        sndId.substring(0, sndId.indexOf('@')),
                                                        document.getId(),
                                                        challenge));
                                            }
                                            break;

                                        default:
                                            break;
                                    }

                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                        Collections.sort(modelArrayList, new Comparator<ChallengeCardModel>() {
                            public int compare(ChallengeCardModel o1, ChallengeCardModel o2) {
                                return new Date(o1.getDate()).compareTo(new Date(o2.getDate())) * -1;
                            }
                        });

                        Collections.sort(ongoingModelArrayList, new Comparator<ChallengeCardModel>() {
                            public int compare(ChallengeCardModel o1, ChallengeCardModel o2) {
                                return new Date(o1.getDate()).compareTo(new Date(o2.getDate())) * -1;
                            }
                        });

                        Collections.sort(pastModelArrayList, new Comparator<ChallengeCardModel>() {
                            public int compare(ChallengeCardModel o1, ChallengeCardModel o2) {
                                return new Date(o1.getDate()).compareTo(new Date(o2.getDate())) * -1;
                            }
                        });

                        pastChallengeCardAdapter = new ChallengeCardAdapter(ChallengeFragment.this, pastModelArrayList, false, bottomNavViewModel, true);

                        pastChViewPager.setAdapter(pastChallengeCardAdapter);

                        pastChViewPager.setPadding(50, 0, 50, 0);

                        onGoingChallengeCardAdapter = new ChallengeCardAdapter(ChallengeFragment.this, ongoingModelArrayList, false, bottomNavViewModel, false);

                        ongoingChViewPager.setAdapter(onGoingChallengeCardAdapter);

                        ongoingChViewPager.setPadding(50, 0, 50, 0);

                        challengeCardAdapter = new ChallengeCardAdapter(ChallengeFragment.this, modelArrayList, true, bottomNavViewModel, false);

                        openChViewPager.setAdapter(challengeCardAdapter);

                        openChViewPager.setPadding(50, 0, 50, 0);

                        if (modelArrayList.size() > 0) {
                            txt_open.setVisibility(View.VISIBLE);
                            openChViewPager.setVisibility(View.VISIBLE);
                        } else {
                            txt_open.setVisibility(View.GONE);
                            openChViewPager.setVisibility(View.GONE);
                        }

                        if (ongoingModelArrayList.size() > 0) {
                            txt_ongoing.setVisibility(View.VISIBLE);
                            ongoingChViewPager.setVisibility(View.VISIBLE);
                        } else {
                            txt_ongoing.setVisibility(View.GONE);
                            ongoingChViewPager.setVisibility(View.GONE);
                        }

                        if (pastModelArrayList.size() > 0) {
                            txt_past.setVisibility(View.VISIBLE);
                            pastChViewPager.setVisibility(View.VISIBLE);
                        } else {
                            txt_past.setVisibility(View.GONE);
                            pastChViewPager.setVisibility(View.GONE);
                        }
                    }
                });

        openChViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




        return view;
    }

    private void loadOngoingCards() {
        ongoingModelArrayList = new ArrayList<>();

        onGoingChallengeCardAdapter = new ChallengeCardAdapter(this, ongoingModelArrayList, false, bottomNavViewModel, true);

        ongoingChViewPager.setAdapter(onGoingChallengeCardAdapter);

        ongoingChViewPager.setPadding(50, 0, 50, 0);
    }

    private void loadOpenCards() {
        modelArrayList = new ArrayList<>();

        challengeCardAdapter = new ChallengeCardAdapter(this, modelArrayList, true, bottomNavViewModel, true);

        openChViewPager.setAdapter(challengeCardAdapter);

        openChViewPager.setPadding(50, 0, 50, 0);
    }
}