package com.nowfloats.PreSignUp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinksity.R;

public class FragmentTwo extends Fragment {


    View root = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            root = inflater.inflate(R.layout.ps_screen2, null, false);
        } catch (Exception e) {

        }

        return root;

    }

}
