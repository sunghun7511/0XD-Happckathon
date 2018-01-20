package com.SHGroup.cometooceantofish.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SHGroup.cometooceantofish.R;

public class LendFragment extends FragmentBase{
    public LendFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lend, container, false);



        return rootView;
    }
}
