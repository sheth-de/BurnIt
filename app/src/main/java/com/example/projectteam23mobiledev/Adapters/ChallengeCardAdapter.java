package com.example.projectteam23mobiledev.Adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;

import com.example.projectteam23mobiledev.ChallengeFragment;
import com.example.projectteam23mobiledev.Models.Challenge;
import com.example.projectteam23mobiledev.Models.ChallengeCardModel;
import com.example.projectteam23mobiledev.R;
import com.example.projectteam23mobiledev.RunFragment;
import com.example.projectteam23mobiledev.Utilities.Enums.StatusEnum;
import com.example.projectteam23mobiledev.ViewModels.BottomNavViewModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class ChallengeCardAdapter extends PagerAdapter {
    private final ChallengeFragment context;
    //private Context context;
    private ArrayList<ChallengeCardModel> modelArrayList;
    private boolean isOpen;
    private BottomNavViewModel bottomNavViewModel;
    private ChallengeCardModel model;

    public ChallengeCardAdapter(ChallengeFragment context, ArrayList<ChallengeCardModel> modelArrayList, boolean isOpen, BottomNavViewModel bottomNavViewModel) {
        this.context = context;
        this.isOpen = isOpen;
        this.modelArrayList = modelArrayList;
        this.bottomNavViewModel = bottomNavViewModel;
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

        //get data
        ChallengeCardModel challengeCardModel = modelArrayList.get(position);
        model = challengeCardModel;
        final String title = challengeCardModel.getTitle();
        final String details = challengeCardModel.getDetails();
        final String createdBy = challengeCardModel.getCreated_by();
        final long time = challengeCardModel.getDate();


        //set data
        ch_title.setText(title);
        ch_details.setText(details);
        ch_createdBy.setText(createdBy);

        int days = Days.daysBetween(new DateTime(new Date(time)), new DateTime(new Date())).getDays();
        ch_time.setText(String.format("%d day(s) ago", days));


        if (isOpen) {
            Button btn_accpt = view.findViewById(R.id.btn_accpt);
            Button btn_decline = view.findViewById(R.id.btn_decline);
            // handle accept click
            btn_accpt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // check if curr user has balance to accept
                    // show toast

                    String currEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users")
                            .whereEqualTo("email", currEmail)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Long wallBal = (Long) document.getData().get("wallet");
                                            Challenge c = challengeCardModel.getChallenge();
                                            int minPoint = c.getMinPoints();
                                            if (wallBal > minPoint) {
                                                // can play


                                                wallBal -= minPoint;

                                                c.setStatus("ongoing");
                                                c.setReceiverStatus(StatusEnum.ACCEPTED);
                                                c.setTotalCredit(c.getTotalCredit() + minPoint);

                                                // deduct points from wallet
                                                db.collection("users").document(document.getId())
                                                        .update(
                                                                "wallet", wallBal
                                                        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        ObjectMapper oMapper = new ObjectMapper();
                                                        // object -> Map
                                                        Map<String, Object> map = oMapper
                                                                .convertValue(challengeCardModel.getChallenge(), Map.class);
                                                        //Log.e("doc id",challengeCardModel.getChallengeId() );
                                                        db.collection("challenges")
                                                                .document(challengeCardModel.getChallengeId())
                                                                .update(map);
                                                    }
                                                });



                                            } else {

                                                AlertDialog alertDialog = new AlertDialog.Builder(context.getActivity()).create();
                                                alertDialog.setTitle("Warning");
                                                alertDialog.setMessage("Sorry, Insufficient Balance. Gain more points with those runs and challenges.");
                                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                alertDialog.show();
                                            }
                                        }
                                    }
                                }
                            });



                }
            });


            //handle decline click
            btn_decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Challenge c = challengeCardModel.getChallenge();

                    String oppEmail = c.getSender();
                    db.collection("users")
                            .whereEqualTo("email", oppEmail)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Long wallBal = (Long) document.getData().get("wallet");
                                            Challenge c = challengeCardModel.getChallenge();
                                            int minPoint = c.getMinPoints();

                                            wallBal += minPoint;
                                            c.setTotalCredit(0);

                                            // deduct points from wallet
                                            db.collection("users").document(document.getId())
                                                    .update(
                                                            "wallet", wallBal
                                                    ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                    c.setStatus("closed");
                                                    c.setReceiverStatus(StatusEnum.DECLINED);

                                                    ObjectMapper oMapper = new ObjectMapper();
                                                    // object -> Map
                                                    Map<String, Object> map = oMapper.convertValue(c, Map.class);
                                                    //Log.e("doc id",challengeCardModel.getChallengeId() );
                                                    db.collection("challenges")
                                                            .document(challengeCardModel.getChallengeId())
                                                            .update(map);

                                                }
                                            });

                                        }
                                    }
                                }
                            });


                }
            });
        } else {

            Button btn_strt = view.findViewById(R.id.btn_strt);
            Button btn_withdraw = view.findViewById(R.id.btn_withdraw);

            // handle start click
            btn_strt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String currEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    String rcv_email = challengeCardModel.getChallenge().getReceiver();
                    String snd_email = challengeCardModel.getChallenge().getSender();

                    if (snd_email.equals(currEmail)) {
                        challengeCardModel.getChallenge().setSendStatus(StatusEnum.STARTED);
                    } else {
                        challengeCardModel.getChallenge().setReceiverStatus(StatusEnum.STARTED);
                    }

                    ObjectMapper oMapper = new ObjectMapper();
                    // object -> Map
                    Map<String, Object> map = oMapper.convertValue(challengeCardModel.getChallenge(), Map.class);
                    db.collection("challenges")
                            .document(challengeCardModel.getChallengeId())
                            .update(map);

                    RunFragment fragment = new RunFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("challengeId", challengeCardModel.getChallengeId());
                    bundle.putSerializable("challenge", challengeCardModel.getChallenge());
                    fragment.setArguments(bundle);
                    FragmentManager fm = context.getParentFragmentManager();
                    bottomNavViewModel.select(1);
                    fm.beginTransaction().replace(R.id.container, fragment).commit();
                }
            });


            //handle withdraw click
            btn_withdraw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Challenge c = challengeCardModel.getChallenge();

                    String title = "Warning";
                    String text = String.format("If you withdraw now, you will lose your " +
                            "waged points of %d xp in this %s based challenge.", c.getMinPoints(), c.getType());

                    AlertDialog.Builder builder = new AlertDialog.Builder(context.getActivity());

                    LayoutInflater inflater = context.getActivity().getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.withdrawal_dialog_box, null);

                    builder.setView(dialogView)
                            .setTitle(title)
                            .setNegativeButton("withdraw", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    withdrawFromChallenge();

                                }
                            })
                            .setPositiveButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    TextView txt_msg = dialogView.findViewById(R.id.txt_msg);
                    txt_msg.setText(text);

                    builder.show();

                }
            });
        }



        //add view to container
        container.addView(view, position);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public void withdrawFromChallenge() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Challenge c = model.getChallenge();

        // implment points allocations here after withdraw action

        // the other user gets all the points
        String currEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        String oppEmail = currEmail.equals(c.getSender())? c.getReceiver(): c.getSender();
        db.collection("users")
                .whereEqualTo("email", oppEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Long wallBal = (Long) document.getData().get("wallet");
                                int totalCredit = c.getTotalCredit();

                                wallBal += totalCredit;
                                c.setTotalCredit(0);

                                // add points to wallet
                                db.collection("users").document(document.getId())
                                        .update(
                                                "wallet", wallBal
                                        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        c.setStatus("closed");
                                        ObjectMapper oMapper = new ObjectMapper();
                                        // object -> Map
                                        Map<String, Object> map = oMapper.convertValue(c, Map.class);
                                        //Log.e("doc id",challengeCardModel.getChallengeId() );
                                        db.collection("challenges").document(model.getChallengeId())
                                                .update(map);

                                    }
                                });

                            }
                        }
                    }
                });
    }
}
