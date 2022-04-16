package com.example.projectteam23mobiledev;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projectteam23mobiledev.Adapters.ChallengeCardAdapter;
import com.example.projectteam23mobiledev.Models.ChallengeCardModel;

import java.util.ArrayList;

public class ChallengeFragment extends Fragment {

    private ViewPager openChViewPager;
    private ViewPager ongoingChViewPager;
    private ArrayList<ChallengeCardModel> modelArrayList;
    private ArrayList<ChallengeCardModel> ongoingModelArrayList;
    private ChallengeCardAdapter challengeCardAdapter;
    private ChallengeCardAdapter onGoingChallengeCardAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_challenge, container, false);


        openChViewPager = view.findViewById(R.id.vp_open_ch);
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

        ongoingModelArrayList.add(new ChallengeCardModel("BAA-5k","5km - 1000xp", "2 days ago", "Chayank"));
        ongoingModelArrayList.add(new ChallengeCardModel("SAA-10k","10km - 1000xp", "2 days ago", "John"));
        ongoingModelArrayList.add(new ChallengeCardModel("BAE-5k","5km - 1000xp", "2 days ago", "Grisham"));
        ongoingModelArrayList.add(new ChallengeCardModel("BAt-5k","5km - 1000xp", "2 days ago", "Krsna"));

        onGoingChallengeCardAdapter = new ChallengeCardAdapter(this, ongoingModelArrayList, false);

        ongoingChViewPager.setAdapter(onGoingChallengeCardAdapter);

        ongoingChViewPager.setPadding(50, 0, 50, 0);
    }

    private void loadOpenCards() {
        modelArrayList = new ArrayList<>();

        modelArrayList.add(new ChallengeCardModel("BAA-5k","5km - 1000xp", "2 days ago", "Chayank"));
        modelArrayList.add(new ChallengeCardModel("SAA-10k","10km - 1000xp", "2 days ago", "John"));
        modelArrayList.add(new ChallengeCardModel("BAE-5k","5km - 1000xp", "2 days ago", "Grisham"));
        modelArrayList.add(new ChallengeCardModel("BAt-5k","5km - 1000xp", "2 days ago", "Krsna"));

        challengeCardAdapter = new ChallengeCardAdapter(this, modelArrayList, true);

        openChViewPager.setAdapter(challengeCardAdapter);

        openChViewPager.setPadding(50, 0, 50, 0);
    }
}