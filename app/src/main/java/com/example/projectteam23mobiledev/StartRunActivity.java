package com.example.projectteam23mobiledev;

import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class StartRunActivity extends Fragment implements View.OnClickListener {

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
        Fragment fragment = new RunFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.start_run_view, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}