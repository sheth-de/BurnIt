package com.example.projectteam23mobiledev.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.projectteam23mobiledev.DistanceFragment;
import com.example.projectteam23mobiledev.TimeFragment;
import com.example.projectteam23mobiledev.ViewModels.BottomNavViewModel;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private BottomNavViewModel bottomNavViewModel;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, BottomNavViewModel bottomNavViewModel)
    {
        super(fragmentActivity);
        this.bottomNavViewModel=bottomNavViewModel;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return  new DistanceFragment(bottomNavViewModel);
            case 1:
                return new TimeFragment(bottomNavViewModel);
            default:
                return  new DistanceFragment(bottomNavViewModel);
        }
    }
    @Override
    public int getItemCount() {
        return 2;
    }

}
