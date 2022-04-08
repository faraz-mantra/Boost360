package com.nowfloats.AccrossVerticals.domain.ui.ExpiredDomain;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinksity.R;

@Deprecated
public class ExpiredDomainFragment extends Fragment {

    private ExpiredDomainViewModel mViewModel;

    public static ExpiredDomainFragment newInstance() {
        return new ExpiredDomainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(ExpiredDomainViewModel.class);
        return inflater.inflate(R.layout.expired_domain_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}