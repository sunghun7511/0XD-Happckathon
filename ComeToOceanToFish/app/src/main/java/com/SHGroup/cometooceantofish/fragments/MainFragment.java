package com.SHGroup.cometooceantofish.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SHGroup.cometooceantofish.MainActivity;
import com.SHGroup.cometooceantofish.R;

public class MainFragment extends FragmentBase{
    public MainFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);



        return rootView;
    }
}
