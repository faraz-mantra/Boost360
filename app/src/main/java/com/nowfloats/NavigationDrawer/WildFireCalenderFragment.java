package com.nowfloats.NavigationDrawer;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksity.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Admin on 22-12-2017.
 */

public class WildFireCalenderFragment extends Fragment implements View.OnClickListener, CalendarView.OnDateChangeListener {

    private Context mContext;
    CalendarView calendarView;
    private int editDateViewId;
    private TextView startDateTv, endDateTv, saveTv;
    private long startDate = -1,endDate = -1;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_calender_view,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editDateViewId = R.id.tv_date_period;
        calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(this);
        saveTv = view.findViewById(R.id.tv_save);
        saveTv.setOnClickListener(this);
        startDateTv = view.findViewById(R.id.tv_date_period);
        startDateTv.setOnClickListener(this);
        endDateTv = view.findViewById(R.id.et_end);
        endDateTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_save:
                if (startDate ==-1 || endDate == -1){
                    Toast.makeText(mContext, getString(R.string.please_select_start_end_date), Toast.LENGTH_SHORT).show();
                }else if(startDate>=endDate){
                    Toast.makeText(mContext, getString(R.string.end_date_should_be_grater_than_start_date), Toast.LENGTH_SHORT).show();
                }else {
                    ((WildFireDialogFragment.OnMenuDialogOptionSelection) mContext).onDateSelected(String.format(Locale.ENGLISH, "%s_%s", startDate, endDate));
                }
                break;
            case R.id.tv_date_period:
                editDateViewId = view.getId();
                startDateTv.setBackgroundResource(R.drawable.focused_edittext_bg);
                startDateTv.setTextColor(ContextCompat.getColor(mContext,R.color.gray));
                endDateTv.setBackgroundResource(R.drawable.unfocused_edittext_bg);
                endDateTv.setTextColor(ContextCompat.getColor(mContext,R.color.e0e0e0));
                if (startDate != -1) {
                    calendarView.setDate(startDate, true, true);
                }
                break;
            case R.id.et_end:
                editDateViewId = view.getId();
                endDateTv.setBackgroundResource(R.drawable.focused_edittext_bg);
                endDateTv.setTextColor(ContextCompat.getColor(mContext,R.color.gray));
                startDateTv.setBackgroundResource(R.drawable.unfocused_edittext_bg);
                startDateTv.setTextColor(ContextCompat.getColor(mContext,R.color.e0e0e0));
                if (endDate != -1) {
                    calendarView.setDate(endDate, true, true);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        SimpleDateFormat formatter = new SimpleDateFormat("d MMMM yyyy",Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,dayOfMonth);
        String s = formatter.format(calendar.getTime());
        if (editDateViewId == R.id.tv_date_period) {
            startDate = calendar.getTime().getTime();
            startDateTv.setText(s);
            endDateTv.performClick();
        } else if (editDateViewId == R.id.et_end) {
            endDate = calendar.getTime().getTime();
            endDateTv.setText(s);
        }
    }
}
