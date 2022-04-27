package com.example.projectteam23mobiledev;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.projectteam23mobiledev.ViewModels.BottomNavViewModel;

public class StartRunActivity extends Fragment implements View.OnClickListener {

    private BottomNavViewModel bottomNavViewModel;

    public StartRunActivity(BottomNavViewModel bottomNavViewModel) {
        this.bottomNavViewModel = bottomNavViewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View startRunView = inflater.inflate(R.layout.fragment_start_run, container, false);
        Button btnStartRun = (Button) startRunView.findViewById(R.id.btn_start_run);
        btnStartRun.setOnClickListener(this);
        return startRunView;
    }

    @Override
    public void onClick(View view) {
        Fragment fragment = new RunFragment(bottomNavViewModel);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}