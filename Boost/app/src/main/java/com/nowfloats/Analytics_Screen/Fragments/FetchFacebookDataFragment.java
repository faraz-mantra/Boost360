package com.nowfloats.Analytics_Screen.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinksity.R;

/**
 * Created by Abhi on 11/28/2016.
 */

public class FetchFacebookDataFragment extends Fragment {
    public FetchFacebookDataFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nfx_fatch_facebook,container,false);
    }
}
