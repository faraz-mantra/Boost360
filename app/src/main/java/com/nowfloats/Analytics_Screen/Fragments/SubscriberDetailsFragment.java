package com.nowfloats.Analytics_Screen.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinksity.R;

/**
 * Created by Admin on 03-03-2017.
 */

public class SubscriberDetailsFragment extends Fragment {

    public static Fragment getInstance(Bundle bundle){
        Fragment frag = new SubscriberDetailsFragment();
        frag.setArguments(bundle);
        return frag;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subscriber_details,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }
}
