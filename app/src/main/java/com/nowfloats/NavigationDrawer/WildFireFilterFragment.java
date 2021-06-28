package com.nowfloats.NavigationDrawer;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.thinksity.R;

/**
 * Created by Admin on 22-12-2017.
 */

public class WildFireFilterFragment extends Fragment implements View.OnClickListener{
    private RadioGroup mRadioGroup;
    private Context mContext;
    public static final int DATE_SELECT = -1,ALL_SELECTED = -2;
    private int monthSelected;
    private String datePeriod;

    public static WildFireFilterFragment getInstance(Bundle bundle){
        WildFireFilterFragment frag = new WildFireFilterFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        monthSelected = bundle.getInt("monthSelected");
        datePeriod = bundle.getString("datePeriod");
        return inflater.inflate(R.layout.layout_wildfire_filter_option,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRadioGroup = view.findViewById(R.id.radioGroup);

        if (!TextUtils.isEmpty(datePeriod)){
            view.findViewById(R.id.llayout_date_period).setVisibility(View.VISIBLE);
            TextView startDateTv = view.findViewById(R.id.tv_start_date);
            TextView endDateTv = view.findViewById(R.id.tv_end_date);
            String[] datePeriods = datePeriod.split("_");
            startDateTv.setText(datePeriods[0]);
            endDateTv.setText(datePeriods[1]);
        }
        view.findViewById(R.id.rb_select_time).setOnClickListener(this);
        switch (monthSelected){
            case 7:
                mRadioGroup.check(R.id.rb_fifteen_days);
                break;
            case 10:
                mRadioGroup.check(R.id.rb_ten_days);
                break;
            case 15:
                mRadioGroup.check(R.id.rb_fifteen_days);
                break;
            case 30:
                mRadioGroup.check(R.id.rb_thirty_days);
                break;
            case DATE_SELECT:
                mRadioGroup.check(R.id.rb_select_time);
                break;
            case ALL_SELECTED:
                mRadioGroup.check(R.id.rb_select_all);
                break;
        }
        view.findViewById(R.id.btn_apply).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_apply) {
            int checkedRadioButtonId = mRadioGroup.getCheckedRadioButtonId();
            if (checkedRadioButtonId == R.id.rb_seven_days) {
                ((WildFireDialogFragment.OnMenuDialogOptionSelection) mContext).onMonthOptionSelect(7);
            } else if (checkedRadioButtonId == R.id.rb_ten_days) {
                ((WildFireDialogFragment.OnMenuDialogOptionSelection) mContext).onMonthOptionSelect(10);
            } else if (checkedRadioButtonId == R.id.rb_fifteen_days) {
                ((WildFireDialogFragment.OnMenuDialogOptionSelection) mContext).onMonthOptionSelect(15);
            } else if (checkedRadioButtonId == R.id.rb_thirty_days) {
                ((WildFireDialogFragment.OnMenuDialogOptionSelection) mContext).onMonthOptionSelect(30);
            } else if (checkedRadioButtonId == R.id.rb_select_time) {
                ((WildFireDialogFragment.OnMenuDialogOptionSelection) mContext).onFilterOptionSelect();
            } else if (checkedRadioButtonId == R.id.rb_select_all) {
                ((WildFireDialogFragment.OnMenuDialogOptionSelection) mContext).onAllSelected();
            }
        } else if (id == R.id.rb_select_time) {
            ((WildFireDialogFragment.OnMenuDialogOptionSelection) mContext).onFilterOptionSelect();
        }
    }
}
