package com.newfloats.staffs.ui.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.newfloats.staffs.R;
import com.newfloats.staffs.ui.BaseFragmentStaff;
import com.newfloats.staffs.ui.breaks.ScheduledBreaksFragmnt;
import com.newfloats.staffs.ui.details.timing.StaffTimingFragment;
import com.newfloats.staffs.ui.services.StaffServicesFragment;


public class StaffDetailsFragment extends BaseFragmentStaff {
    View view;
    private StaffDetailsViewModel mViewModel;
    private RelativeLayout staffTiming;
    public static final int REQUEST_IMAGE = 297;
    public static StaffDetailsFragment newInstance() {
        return new StaffDetailsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_staff_details, container, false);

        this.staffTiming = view.findViewById(R.id.rl_staff_timing);
        staffTiming.setOnClickListener(v -> launchFragment(StaffTimingFragment.newInstance()));
        RelativeLayout staffServices = view.findViewById(R.id.rl_service_provided);
        staffServices.setOnClickListener(v -> launchFragment(StaffServicesFragment.newInstance()));
        RelativeLayout staffBreaks = view.findViewById(R.id.rl_scheduled_breaks);
        staffBreaks.setOnClickListener(v -> launchFragment(ScheduledBreaksFragmnt.newInstance()));
        FrameLayout addStaffImage = view.findViewById(R.id.fl_add_staff_img);
        addStaffImage.setOnClickListener(v -> {
//            startActivityForResult(new Intent(getActivity(),ImageActivity.class),REQUEST_IMAGE);
        });
        ((StaffDetailsActivity) requireActivity()).setToolBarTitle("Staff Details", false);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(StaffDetailsViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    @Override
    public void onStart() {
        super.onStart();
    }
}