package com.newfloats.staffs.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.newfloats.staffs.R;
import com.newfloats.staffs.ui.BaseFragmentStaff;

public class StaffHomeFragment extends BaseFragmentStaff {
    private StaffHomeViewModel mViewModel;
    View view;
    public static StaffHomeFragment newInstance() {
        return new StaffHomeFragment();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_staff_home, container, false);
        FrameLayout takeToListingBtn = view.findViewById(R.id.btn_take_to_listing);
        takeToListingBtn.setOnClickListener(v -> launchFragment(StaffAddFragment.newInstance()));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(StaffHomeViewModel.class);
    }

}