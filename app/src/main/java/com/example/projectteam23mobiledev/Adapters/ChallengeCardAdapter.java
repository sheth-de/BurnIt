package com.example.projectteam23mobiledev.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.projectteam23mobiledev.ChallengeFragment;
import com.example.projectteam23mobiledev.Models.ChallengeCardModel;
import com.example.projectteam23mobiledev.R;

import java.util.ArrayList;
import java.util.Date;

public class ChallengeCardAdapter extends PagerAdapter {
    //private Context context;
    private ArrayList<ChallengeCardModel> modelArrayList;
    private boolean isOpen;

    public ChallengeCardAdapter(ChallengeFragment context, ArrayList<ChallengeCardModel> modelArrayList, boolean isOpen) {
        //this.context = context;
        this.isOpen = isOpen;
        this.modelArrayList = modelArrayList;
    }

    @Override
    public int getCount() {
        return modelArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        //inlate layout card_item
        System.out.println(container);
        View view;
        if( isOpen) {
            view = LayoutInflater.from(container.getContext()).inflate(R.layout.open_challenge_card_item, container, false);
        } else {
            view = LayoutInflater.from(container.getContext()).inflate(R.layout.ongoing_challenge_card_item, container, false);
        }

        //initialize the uid views
        TextView ch_title = view.findViewById(R.id.txt_ch_title);
        TextView ch_details = view.findViewById(R.id.txt_ch_details);
        TextView ch_createdBy = view.findViewById(R.id.txt_ch_createdby);
        TextView ch_time = view.findViewById(R.id.txt_ch_time);
        Button btn_accpt = view.findViewById(R.id.btn_accpt);
        Button btn_decline = view.findViewById(R.id.btn_decline);

        //get data
        ChallengeCardModel challengeCardModel = modelArrayList.get(position);
        final String title = challengeCardModel.getTitle();
        final String details = challengeCardModel.getDetails();
        final String createdBy = challengeCardModel.getCreated_by();
        final long time = challengeCardModel.getDate();


        //set data
        ch_title.setText(title);
        ch_details.setText(details);
        ch_createdBy.setText(createdBy);
        ch_time.setText(String.valueOf(new Date(time)));

        // handle accept click
        btn_accpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(context, "Accepted challenge", Toast.LENGTH_SHORT).show();
            }
        });


        //handle decline click
        btn_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Declined challenge", Toast.LENGTH_SHORT).show();
            }
        });


        //add view to container
        container.addView(view, position);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
