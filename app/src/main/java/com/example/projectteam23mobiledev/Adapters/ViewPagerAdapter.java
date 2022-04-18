package com.example.projectteam23mobiledev.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.projectteam23mobiledev.DistanceFragment;
import com.example.projectteam23mobiledev.TimeFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity)
    {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return  new DistanceFragment();
            case 1:
                return new TimeFragment();
            default:
                return  new DistanceFragment();
        }
    }
    @Override
    public int getItemCount() {
        return 2;
    }

}
