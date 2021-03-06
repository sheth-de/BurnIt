package com.example.projectteam23mobiledev;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectteam23mobiledev.ViewModels.BottomNavViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView usr;
//    private TextView usr;
    private BottomNavigationView bottomNavigationView;
    private StartRunActivity StartRunFragment;
    private ProfileFragment profileFragment = new ProfileFragment();
    private ChallengeFragment challengeFragment;

    private BottomNavViewModel bottomNavViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the ViewModel.
        bottomNavViewModel = new ViewModelProvider(this).get(BottomNavViewModel.class);

        bottomNavViewModel.getSelected().observe(this, id -> {
            switch (id) {
                case 1:
                    bottomNavigationView.setSelectedItemId(R.id.run);
                    break;
                case 2:
                    bottomNavigationView.setSelectedItemId(R.id.challenge);
                    break;
                case 3:
                    bottomNavigationView.setSelectedItemId(R.id.profile);
                    break;
                default:
                    break;
            }
        });

        bottomNavViewModel.getIsVisible().observe(this, id -> {
            if (id) {
                bottomNavigationView.setVisibility(View.GONE);
            } else {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

        challengeFragment = new ChallengeFragment(bottomNavViewModel);
        StartRunFragment = new StartRunActivity(bottomNavViewModel);

        mAuth = FirebaseAuth.getInstance();
//        usr = (TextView) findViewById(R.id.usrname_edTxt);
//        usr = (TextView) findViewById(R.id.usr);

        if (mAuth.getCurrentUser() != null) {
            String em = mAuth.getCurrentUser().getEmail();
//            usr.setText("Hi "+em.substring(0, em.indexOf('@'))+ "!");
        }
        bottomNavigationView = findViewById(R.id.bottom_navigation);



//        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.profile);
//        badgeDrawable.setVisible(true);
//        badgeDrawable.setNumber(8);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.run:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, StartRunFragment).commit();
                        return true;
                    case R.id.profile:
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                        return true;
                    case R.id.challenge:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, challengeFragment).commit();
                        return true;
                }

                return false;
            }
        });
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, StartRunFragment).commit();
        } else {
            boolean getChallengeScreen= extras.getBoolean("challengeScreen");
            if(getChallengeScreen){
                bottomNavigationView.setSelectedItemId(R.id.challenge);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String email = user.getEmail();
            String uid = user.getUid();
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }
}