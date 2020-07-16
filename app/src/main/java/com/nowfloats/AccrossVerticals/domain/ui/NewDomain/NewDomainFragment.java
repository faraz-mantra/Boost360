package com.nowfloats.AccrossVerticals.domain.ui.NewDomain;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nowfloats.AccrossVerticals.domain.DomainEmailActivity;
import com.thinksity.R;

public class NewDomainFragment extends Fragment {

    private NewDomainViewModel mViewModel;

    public static NewDomainFragment newInstance() {
        return new NewDomainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.new_domain_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(NewDomainViewModel.class);
        // TODO: Use the ViewModel
    }
}