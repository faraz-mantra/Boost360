package com.newfloats.staffs.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.newfloats.staffs.R;
import com.newfloats.staffs.ui.BaseFragmentStaff;

public class StaffAddFragment extends BaseFragmentStaff {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff_add, container, false);
        FrameLayout addStaffBtn = view.findViewById(R.id.fl_add_staff);
        addStaffBtn.setOnClickListener(v -> {
        });
        return view;
    }

    public static StaffAddFragment newInstance() {
        Bundle args = new Bundle();
        StaffAddFragment fragment = new StaffAddFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
