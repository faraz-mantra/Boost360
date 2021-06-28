package com.newfloats.staffs.ui.breaks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.newfloats.staffs.R;
import com.newfloats.staffs.ui.bottomsheets.BottomSheetFragment;
import com.newfloats.staffs.ui.details.StaffDetailsActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduledBreaksFragmnt#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduledBreaksFragmnt extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static ScheduledBreaksFragmnt newInstance() {
        ScheduledBreaksFragmnt fragment = new ScheduledBreaksFragmnt();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_schedule_breaks, container, false);
        FrameLayout addBreaks = v.findViewById(R.id.fl_add_breaks);
        addBreaks.setOnClickListener(v1 -> {
            BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
            bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
        });
        ((StaffDetailsActivity) requireActivity()).setToolBarTitle("Scheduled Breaks", false);
        return v;
    }

}