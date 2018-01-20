package com.SHGroup.cometooceantofish.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SHGroup.cometooceantofish.R;

public class PartyFragment extends FragmentBase{
    public PartyFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_party, container, false);



        return rootView;
    }
}
