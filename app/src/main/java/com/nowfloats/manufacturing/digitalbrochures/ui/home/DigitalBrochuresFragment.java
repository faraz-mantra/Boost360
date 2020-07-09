package com.nowfloats.manufacturing.digitalbrochures.ui.home;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinksity.R;

public class DigitalBrochuresFragment extends Fragment {

    private DigitalBrochuresViewModel mViewModel;

    public static DigitalBrochuresFragment newInstance() {
        return new DigitalBrochuresFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(DigitalBrochuresViewModel.class);
        return inflater.inflate(R.layout.digital_brochures_fragment, container, false);
    }

}