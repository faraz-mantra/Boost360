package com.nowfloats.education.batches.ui.batchesdetails;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinksity.R;


public class BatchesDetailsFragment extends Fragment {

    private BatchesDetailsViewModel mViewModel;

    public static BatchesDetailsFragment newInstance() {
        return new BatchesDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(BatchesDetailsViewModel.class);
        return inflater.inflate(R.layout.batches_details_fragment, container, false);
    }

}