package com.nowfloats.NavigationDrawer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    public enum SortType{
        ALPHABETIC,DATE,CLICKS
    }

    public static WildFireDialogFragment getInstance(Bundle bundle){
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
        return inflater.inflate(R.layout.layout_wildfire_sort_option,container,false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRadioGroup = view.findViewById(R.id.radioGroup);
        switch (SortType.valueOf(sortType)){
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
        switch (view.getId()){
            case R.id.btn_apply:
                SortType type;
                switch (mRadioGroup.getCheckedRadioButtonId()){
                    case R.id.rb_alphabetic:
                        type = SortType.ALPHABETIC;
                        break;
                    case R.id.rb_clicks:
                        type = SortType.CLICKS;
                        break;
                    default:
                        return;
                }
                ((OnMenuDialogOptionSelection)mContext).onSortOptionSelect(type);
                break;
            default:
                break;
        }
    }

    public interface OnMenuDialogOptionSelection {
        void onSortOptionSelect(SortType type);
        void onFilterOptionSelect();
        void onDateSelected(String start_end);
        void onMonthOptionSelect(int month);
        void onAllSelected();
    }
}
