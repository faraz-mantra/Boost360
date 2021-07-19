package com.nowfloats.Analytics_Screen.Graph;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.framework.views.customViews.CustomToolbar;
import com.nowfloats.Analytics_Screen.Graph.fragments.UniqueVisitorsFragment;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.nowfloats.Analytics_Screen.Graph.fragments.UniqueVisitorsFragment.pattern;

/**
 * Created by Admin on 17-01-2018.
 */

public class SiteViewsAnalytics extends AppCompatActivity implements UniqueVisitorsFragment.ViewCallback, View.OnClickListener {


    public static final String VISITS_TYPE = "visits_type";
    public static final String VISITS_TYPE_STRING = "visits_type_string";
    AppCompatTextView tvYear;
    private TextView tvMonth, tvWeek;
    private UniqueVisitorsFragment.BatchType currentTabType;
    private PopupWindow popup;
    private VisitsType mVisitsType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        CustomToolbar toolbar = (CustomToolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mVisitsType = (VisitsType) getIntent().getSerializableExtra(VISITS_TYPE);
        if (mVisitsType == null) {
            mVisitsType = VisitsType.fromName(getIntent().getStringExtra(VISITS_TYPE_STRING));
        }
        if (mVisitsType == null) finish();

        switch (mVisitsType) {
            case UNIQUE:
                setTitle(getString(R.string.unique_visitors));
                break;
            case TOTAL:
                setTitle(getString(R.string.overall_visits));
                break;
            case MAP_VISITS:
                setTitle(getString(R.string.map_visits));
                break;
            default:
                finish();
        }
        tvMonth = findViewById(R.id.tv_month_tab);
        tvWeek = findViewById(R.id.tv_week_tab);
        tvYear = findViewById(R.id.tv_year_tab);
        tvYear.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(this, R.drawable.ic_drop_down_white), null);
        tvMonth.setOnClickListener(this);
        tvWeek.setOnClickListener(this);
        tvYear.setOnClickListener(this);

        changeTab(UniqueVisitorsFragment.BatchType.dy);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_week_tab:
                changeTab(UniqueVisitorsFragment.BatchType.dy);
                break;
            case R.id.tv_month_tab:
                changeTab(UniqueVisitorsFragment.BatchType.ww);
                break;
            case R.id.tv_year_tab:
                initiatePopupWindow(view);
                changeTab(UniqueVisitorsFragment.BatchType.mm);
                break;
        }
    }

    private void initiatePopupWindow(final View image) {

        if (popup == null) {
            try {
                UserSessionManager manager = new UserSessionManager(this, this);
                String createdDate = manager.getFPDetails(Key_Preferences.GET_FP_DETAILS_CREATED_ON);
                if (createdDate.contains("/Date")) {
                    createdDate = createdDate.replace("/Date(", "").replace(")/", "");
                }
                Calendar c = Calendar.getInstance();
                int currentYear = c.get(Calendar.YEAR);
                c.setTimeInMillis(Long.valueOf(createdDate));
                int createdYear = c.get(Calendar.YEAR);
                final List<String> yearsList = new ArrayList<>(currentYear - createdYear + 1);
                for (int i = currentYear; i >= createdYear; i--) {
                    yearsList.add(String.valueOf(i));
                }
                popup = new PopupWindow(this);
                View layout = LayoutInflater.from(this).inflate(R.layout.layout_drop_down_list, null);
                popup.setContentView(layout);

                popup.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.white_round_corner));
                popup.setOutsideTouchable(true);
                ListView mListView = layout.findViewById(R.id.list_view);
                mListView.setAdapter(new ArrayAdapter<String>(this, R.layout.simple_text_center_item1, yearsList));
                popup.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        initiatePopupWindow(image);
                        onYearSelected(Integer.valueOf(yearsList.get(i)));
                    }
                });
                popup.setFocusable(true);
                popup.showAsDropDown(image, 0, 5);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (popup.isShowing()) {
            popup.dismiss();
        } else {
            popup.showAsDropDown(image, 0, 5);
        }
    }

    private void changeTab(UniqueVisitorsFragment.BatchType viewType) {
        if (currentTabType == viewType) return;
        changeTabColors(viewType);
        Bundle b = new Bundle();
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        HashMap<String, String> map = new HashMap<>();
        map.put("endDate", Methods.getFormattedDate(c.getTimeInMillis(), pattern));
        switch (viewType) {
            case dy:
                int weekDay = c.get(Calendar.DAY_OF_WEEK);
                int month = c.get(Calendar.MONTH);
                c.add(Calendar.DAY_OF_MONTH, -((weekDay - c.getFirstDayOfWeek() + 7) % 7));
                if (month == c.get(Calendar.MONTH)) {
                    map.put("startDate", String.format(Locale.ENGLISH, "%s/%02d/%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH)));
                } else {
                    map.put("startDate", String.format(Locale.ENGLISH, "%s/%02d/%s", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, "01"));
                }
                break;
            case ww:
                map.put("startDate", String.format(Locale.ENGLISH, "%s/%02d/%s", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, "01"));
                break;
            case mm:
                map.put("startDate", String.format(Locale.ENGLISH, "%s/%s/%s", c.get(Calendar.YEAR), "01", "01"));
                b.putInt("year", c.get(Calendar.YEAR));
                break;
        }
        b.putInt("pos", viewType.val);
        b.putSerializable("hashmap", map);
        b.putSerializable(VISITS_TYPE, mVisitsType);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_analytics_fragment, UniqueVisitorsFragment.getInstance(b))
                .commit();
    }

    private void changeTabColors(UniqueVisitorsFragment.BatchType viewType) {
        currentTabType = viewType;
        tvWeek.setBackgroundColor(ContextCompat.getColor(this, R.color.fafafa));
        tvMonth.setBackgroundColor(ContextCompat.getColor(this, R.color.fafafa));
        tvYear.setBackgroundColor(ContextCompat.getColor(this, R.color.fafafa));

        tvWeek.setTextColor(ContextCompat.getColor(this, R.color.dark_grey));
        tvMonth.setTextColor(ContextCompat.getColor(this, R.color.dark_grey));
        tvYear.setTextColor(ContextCompat.getColor(this, R.color.dark_grey));

        switch (viewType) {
            case dy:
                tvWeek.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_grey));
                tvWeek.setTextColor(ContextCompat.getColor(this, R.color.fafafa));
                break;
            case ww:
                tvMonth.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_grey));
                tvMonth.setTextColor(ContextCompat.getColor(this, R.color.fafafa));
                break;
            case mm:
                tvYear.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_grey));
                tvYear.setTextColor(ContextCompat.getColor(this, R.color.fafafa));
                break;
        }
    }

    private void onYearSelected(int yearSelected) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        HashMap<String, String> map = new HashMap<>();
        map.put("batchType", UniqueVisitorsFragment.BatchType.mm.name());
        map.put("startDate", String.format(Locale.ENGLISH, "%s/%s", yearSelected, "01/01"));
        if (yearSelected == year) {
            map.put("endDate", Methods.getFormattedDate(c.getTimeInMillis(), pattern));
        } else {
            map.put("endDate", String.format(Locale.ENGLISH, "%s/%s", yearSelected, "12/31"));
        }
        Bundle b = new Bundle();
        b.putInt("pos", UniqueVisitorsFragment.BatchType.mm.val);
        b.putSerializable("hashmap", map);
        b.putSerializable(VISITS_TYPE, mVisitsType);
        b.putInt("year", yearSelected);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_analytics_fragment, UniqueVisitorsFragment.getInstance(b))
                .commit();
        //remove all stack fragment
        int stackCount = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < stackCount; i++) {
            getSupportFragmentManager().popBackStack();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onChartBarClicked(HashMap<String, String> map, int views) {
        FragmentManager manager = getSupportFragmentManager();
        Bundle b = new Bundle();
        changeTabColors(UniqueVisitorsFragment.BatchType.valueOf(map.get("batchType")));
        b.putInt("pos", UniqueVisitorsFragment.BatchType.valueOf(map.get("batchType")).val);
        b.putInt("totalViews", views);
        b.putSerializable(VISITS_TYPE, mVisitsType);
        b.putSerializable("hashmap", map);
        manager.beginTransaction()
                .replace(R.id.fl_analytics_fragment, UniqueVisitorsFragment.getInstance(b), map.get("batchType"))
                .addToBackStack(map.get("batchType"))
                .commit();

        Log.d("onChartBarClicked", "" + views);
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        int fragmentCount = manager.getBackStackEntryCount();
        if (fragmentCount > 0) {
            FragmentManager.BackStackEntry backEntry = manager.getBackStackEntryAt(fragmentCount - 1);
            String tag = backEntry.getName();
            Fragment fragment = manager.findFragmentByTag(tag);
            if (fragment instanceof UniqueVisitorsFragment) {
                switch (((UniqueVisitorsFragment) fragment).batchType) {
                    case dy:
                        changeTabColors(UniqueVisitorsFragment.BatchType.ww);
                        break;
                    case ww:
                        changeTabColors(UniqueVisitorsFragment.BatchType.mm);
                        break;
                    case mm:
                        break;
                }

            }
        }

        super.onBackPressed();
    }


    public enum VisitsType {
        UNIQUE, TOTAL, MAP_VISITS;

        public static VisitsType fromName(String name) {
            for (VisitsType b : VisitsType.values()) {
                if (b.name().equalsIgnoreCase(name)) {
                    return b;
                }
            }
            return null;
        }
    }
}