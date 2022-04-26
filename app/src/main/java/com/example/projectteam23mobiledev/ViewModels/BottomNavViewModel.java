package com.example.projectteam23mobiledev.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BottomNavViewModel extends ViewModel {

    private final MutableLiveData<Integer> selected = new MutableLiveData<Integer>();

    public void select(Integer id) {
        selected.setValue(id);
    }

    public MutableLiveData<Integer> getSelected() {
        return selected;
    }
}
