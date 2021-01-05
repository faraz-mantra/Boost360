package com.newfloats.staffs.ui.details.timing;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.framework.views.customViews.CustomTextView;
import com.framework.views.viewgroups.BaseRecyclerView;
import com.newfloats.staffs.R;
import com.newfloats.staffs.ui.details.StaffDetailsActivity;
import com.newfloats.staffs.ui.details.timing.adapter.RecyclerSessionAdapter;

public class StaffTimingFragment extends Fragment implements RecyclerSessionAdapter.RecyclerItemClick {
    View view;

    public static StaffTimingFragment newInstance() {
        Bundle args = new Bundle();
        StaffTimingFragment fragment = new StaffTimingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_staff_timing, container, false);
        BaseRecyclerView recyclerView = view.findViewById(R.id.rv_staff_timing);
        recyclerView.setAdapter(new RecyclerSessionAdapter(this));
        CustomTextView headerText = view.findViewById(R.id.ctv_text_header);
        headerText.setText(Html.fromHtml(getString(R.string.clinic_businesses_hour)));
        ((StaffDetailsActivity) requireActivity()).setToolBarTitle("Staff Timing", false);
        return view;
    }

    @Override
    public void onToggle() {

    }

    @Override
    public void onAddSession() {

    }

    @Override
    public void onApplyAllDays() {

    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
