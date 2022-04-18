package com.example.projectteam23mobiledev;

import static android.content.ContentValues.TAG;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.projectteam23mobiledev.Adapters.ChallengeCardAdapter;
import com.example.projectteam23mobiledev.Models.ChallengeCardModel;
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
    private ArrayList<ChallengeCardModel> modelArrayList;
    private ArrayList<ChallengeCardModel> ongoingModelArrayList;
    private ChallengeCardAdapter challengeCardAdapter;
    private ChallengeCardAdapter onGoingChallengeCardAdapter;
    private Button fragmentBtnCreateChallenge;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_challenge, container, false);
        fragmentBtnCreateChallenge = view.findViewById(R.id.fragmentBtnCreateChallenge);
        fragmentBtnCreateChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateChallenge.class);
                startActivity(intent);
            }
        });

        openChViewPager = view.findViewById(R.id.vp_open_ch);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("challenges")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        String rcv = "";
                        String snd = "";
//                        int lim = snapshots.getDocumentChanges().size();
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:

                                    QueryDocumentSnapshot document = dc.getDocument();

                                    String sndId = (String) document.getData().get("sender");
                                    String rcvId = (String) document.getData().get("receiver");
                                    String minPoints = String.valueOf(document.getData().get("minPoints"));
                                    String type = (String) document.getData().get("type");
                                    Long ch_date = (Long) document.getData().get("timeStamp");
                                    String ch_status = (String) document.getData().get("status");
                                    String ch_time = String.valueOf(document.getData().get("distance"));
                                    String ch_dist = String.valueOf(document.getData().get("time"));


                                    String challengeTitle = String.format("%s based challenge", type);
                                    String details = "";
                                    if (type.equals("distance")) {
                                        details = String.format("%s miles - %s XP ", ch_dist, minPoints);
                                    } else {
                                        details = String.format("%s min - %s XP ", ch_time, minPoints);
                                    }


//                                    String daysAgo = String.valueOf(new Date(ch_date));


                                    switch (ch_status) {
                                        case "open":
                                            modelArrayList.add(new ChallengeCardModel(challengeTitle,
                                                details,
                                                ch_date,
                                                sndId.substring(0, sndId.indexOf('@'))));
                                            break;
                                        case "ongoing":
                                            ongoingModelArrayList.add(new ChallengeCardModel(challengeTitle,
                                                    details,
                                                    ch_date,
                                                    sndId.substring(0, sndId.indexOf('@'))));
                                            break;
                                        default: break;


                                    }
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                        Collections.sort(modelArrayList, new Comparator<ChallengeCardModel>() {
                            public int compare(ChallengeCardModel o1, ChallengeCardModel o2) {
                                return new Date(o1.getDate()).compareTo(new Date(o2.getDate()));
                            }
                        });

                        Collections.sort(ongoingModelArrayList, new Comparator<ChallengeCardModel>() {
                            public int compare(ChallengeCardModel o1, ChallengeCardModel o2) {
                                return new Date(o1.getDate()).compareTo(new Date(o2.getDate()));
                            }
                        });

                        challengeCardAdapter.notifyDataSetChanged();
                        onGoingChallengeCardAdapter.notifyDataSetChanged();
                    }
                });

        loadOpenCards();
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


        ongoingChViewPager = view.findViewById(R.id.vp_ongoing_ch);
        loadOngoingCards();

        return view;
    }

    private void loadOngoingCards() {
        ongoingModelArrayList = new ArrayList<>();

//        ongoingModelArrayList.add(new ChallengeCardModel("BAA-5k","5km - 1000xp", "2 days ago", "Chayank"));
//        ongoingModelArrayList.add(new ChallengeCardModel("SAA-10k","10km - 1000xp", "2 days ago", "John"));
//        ongoingModelArrayList.add(new ChallengeCardModel("BAE-5k","5km - 1000xp", "2 days ago", "Grisham"));
//        ongoingModelArrayList.add(new ChallengeCardModel("BAt-5k","5km - 1000xp", "2 days ago", "Krsna"));

        onGoingChallengeCardAdapter = new ChallengeCardAdapter(this, ongoingModelArrayList, false);

        ongoingChViewPager.setAdapter(onGoingChallengeCardAdapter);

        ongoingChViewPager.setPadding(50, 0, 50, 0);
    }

    private void loadOpenCards() {
        modelArrayList = new ArrayList<>();



//        modelArrayList.add(new ChallengeCardModel("BAA-5k","5km - 1000xp", "2 days ago", "Chayank"));
//        modelArrayList.add(new ChallengeCardModel("SAA-10k","10km - 1000xp", "2 days ago", "John"));
//        modelArrayList.add(new ChallengeCardModel("BAE-5k","5km - 1000xp", "2 days ago", "Grisham"));
//        modelArrayList.add(new ChallengeCardModel("BAt-5k","5km - 1000xp", "2 days ago", "Krsna"));

        challengeCardAdapter = new ChallengeCardAdapter(this, modelArrayList, true);

        openChViewPager.setAdapter(challengeCardAdapter);

        openChViewPager.setPadding(50, 0, 50, 0);
    }
}