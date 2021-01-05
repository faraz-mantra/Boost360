package com.newfloats.staffs.ui.services;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.newfloats.staffs.R;
import com.newfloats.staffs.ui.BaseFragmentStaff;
import com.newfloats.staffs.ui.details.StaffDetailsActivity;

public class StaffServicesFragment extends BaseFragmentStaff {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_select_services, container, false);
        ((StaffDetailsActivity) requireActivity()).setToolBarTitle("Select Services", false);
        RecyclerView recyclerView = view.findViewById(R.id.rv_service_provided);
        recyclerView.setAdapter(new ServicesAdapter());
        return view;
    }
    public static StaffServicesFragment newInstance() {
        Bundle args = new Bundle();
        StaffServicesFragment fragment = new StaffServicesFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
