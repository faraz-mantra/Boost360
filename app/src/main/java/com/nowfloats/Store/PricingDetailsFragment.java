package com.nowfloats.Store;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinksity.R;

public class PricingDetailsFragment extends Fragment {


    public PricingDetailsFragment() {

    }


    public static PricingDetailsFragment newInstance() {
        PricingDetailsFragment fragment = new PricingDetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_pricing_details, container, false);
    }
}
