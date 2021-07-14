package com.nowfloats.PreSignUp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.thinksity.R;

public class FragmentOne extends Fragment {

    //	public static SEditText email,phone;
    Button next, back;
    View root = null;
    RelativeLayout mainLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            root = inflater.inflate(R.layout.ps_screen1, null, false);
        } catch (Exception e) {

        }

        return root;

    }

}
