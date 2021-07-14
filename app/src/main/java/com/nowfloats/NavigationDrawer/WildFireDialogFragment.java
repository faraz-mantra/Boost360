package com.nowfloats.NavigationDrawer;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.thinksity.R;

/**
 * Created by Admin on 21-12-2017.
 */

public class WildFireDialogFragment extends Fragment implements View.OnClickListener {

    RadioGroup mRadioGroup;
    private Context mContext;
    private String sortType;

    public static WildFireDialogFragment getInstance(Bundle bundle) {
        WildFireDialogFragment frag = new WildFireDialogFragment();
        frag.setArguments(bundle);
        return frag;
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
        sortType = bundle.getString("sortType");
        return inflater.inflate(R.layout.layout_wildfire_sort_option, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRadioGroup = view.findViewById(R.id.radioGroup);
        switch (SortType.valueOf(sortType)) {
            case ALPHABETIC:
                mRadioGroup.check(R.id.rb_alphabetic);
                break;
            case CLICKS:
                mRadioGroup.check(R.id.rb_clicks);
                break;
        }

        view.findViewById(R.id.btn_apply).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_apply) {
            SortType type;
            int checkedRadioButtonId = mRadioGroup.getCheckedRadioButtonId();
            if (checkedRadioButtonId == R.id.rb_alphabetic) {
                type = SortType.ALPHABETIC;
            } else if (checkedRadioButtonId == R.id.rb_clicks) {
                type = SortType.CLICKS;
            } else {
                return;
            }
            ((OnMenuDialogOptionSelection) mContext).onSortOptionSelect(type);
        }
    }

    public enum SortType {
        ALPHABETIC, DATE, CLICKS
    }

    public interface OnMenuDialogOptionSelection {
        void onSortOptionSelect(SortType type);

        void onFilterOptionSelect();

        void onDateSelected(String start_end);

        void onMonthOptionSelect(int month);

        void onAllSelected();
    }
}
