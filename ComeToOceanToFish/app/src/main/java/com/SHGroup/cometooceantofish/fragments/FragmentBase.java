package com.SHGroup.cometooceantofish.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by c on 2018-01-20.
 */

public abstract class FragmentBase extends Fragment {
    protected FragmentBase(){}

    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState);
}
