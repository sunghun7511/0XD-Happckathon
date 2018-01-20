package com.SHGroup.cometooceantofish.fragments;

import android.support.v4.app.Fragment;

public class FragmentBase extends Fragment {
    private String access_token;

    protected String getAccessToken(){
        return "JWT " + access_token;
    }

    public void setAccessToken(String access_token){
        this.access_token = access_token;
    }
}
