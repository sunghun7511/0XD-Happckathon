package com.SHGroup.cometooceantofish.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SHGroup.cometooceantofish.R;

public class CommunityFragment extends FragmentBase{
    public CommunityFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_community, container, false);



        return rootView;
    }
}
