package com.nowfloats.manufacturing.digitalbrochures.ui.brochuresdetails;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinksity.R;


public class DigitalBrochuresDetailsFragment extends Fragment {

    private DigitalBrochuresDetailsViewModel mViewModel;

    public static DigitalBrochuresDetailsFragment newInstance() {
        return new DigitalBrochuresDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(DigitalBrochuresDetailsViewModel.class);
        return inflater.inflate(R.layout.digital_brochures_details_fragment, container, false);
    }

}